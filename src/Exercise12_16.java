/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Exercise12_16 extends Exercise12_15
{

    public enum Variable {
        Reservoir, RunHA, RunHB, Pumping, StartHA, StartHB
    }

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
        //Add new variables
        for (int period = 1; period <= 5; period++)
        {
            String reservoirName = getSimpleName(Variable.Reservoir, period);
            setVariable(reservoirName, cplex.numVar(0, Double.MAX_VALUE, reservoirName));
            String hydroAName = getSimpleName(Variable.RunHA, period);
            setVariable(hydroAName, cplex.boolVar(hydroAName));
            String hydroBName = getSimpleName(Variable.RunHB, period);
            setVariable(hydroBName, cplex.boolVar(hydroBName));
            String startAName = getSimpleName(Variable.StartHA, period);
            setVariable(startAName, cplex.boolVar(startAName));
            String startBName = getSimpleName(Variable.StartHB, period);
            setVariable(startBName, cplex.boolVar(startBName));
            String pumpingName = getSimpleName(Variable.Pumping, period);
            setVariable(pumpingName, cplex.numVar(0, Double.MAX_VALUE,pumpingName));
        }

        //Add new constraints
        for (int period = 1; period <= 5; period++)
        {
            String reservoirName = getSimpleName(Variable.Reservoir, period);
            IloNumVar reservoir = getVariable(reservoirName);
            String hydroAName = getSimpleName(Variable.RunHA, period);
            IloIntVar hydroA = (IloIntVar) getVariable(hydroAName);
            String hydroBName = getSimpleName(Variable.RunHB, period);
            IloIntVar hydroB = (IloIntVar) getVariable(hydroBName);
            String startAName = getSimpleName(Variable.StartHA, period);
            IloIntVar startA = (IloIntVar) getVariable(startAName);
            String startBName = getSimpleName(Variable.StartHB, period);
            IloIntVar startB = (IloIntVar) getVariable(startBName);
            String pumpingName = getSimpleName(Variable.Pumping, period);
            IloNumVar pumping = getVariable(pumpingName);

            //Respect range
            IloLinearNumExpr reservoirMinLevel = cplex.linearNumExpr();
            reservoirMinLevel.addTerm(1, reservoir);
            if (period == 1)
            {
                cplex.addEq(reservoirMinLevel, 16);
            }
            else
            {
                cplex.addGe(reservoirMinLevel, 15);
                IloLinearNumExpr reservoirMaxLevel = cplex.linearNumExpr();
                reservoirMaxLevel.addTerm(1, reservoir);
                cplex.addLe(reservoirMaxLevel, 20);
            }
            //Origin & start
            IloLinearNumExpr reservoirOrigin = cplex.linearNumExpr();
            reservoirOrigin.addTerm(1, reservoir);
            reservoirOrigin.addTerm(0.31*hours[period-1], hydroA);
            reservoirOrigin.addTerm(0.47*hours[period-1], hydroB);
            reservoirOrigin.addTerm(-1, pumping);

            int previousPeriod = period - 1;

            if (period == 1)
            {
                previousPeriod = 5;
            }
            reservoirOrigin.addTerm(-1, getVariable(getSimpleName(Variable.Reservoir, previousPeriod)));
            cplex.addEq(reservoirOrigin, 0);


            IloLinearNumExpr startingA = cplex.linearNumExpr();
            startingA.addTerm(1, startA);
            startingA.addTerm(-1, hydroA);
            startingA.addTerm(1, getVariable(getSimpleName(Variable.RunHA, previousPeriod)));
            cplex.addGe(startingA, 0);

            IloLinearNumExpr startingB = cplex.linearNumExpr();
            startingB.addTerm(1, startB);
            startingB.addTerm(-1, hydroB);
            startingB.addTerm(1, getVariable(getSimpleName(Variable.RunHB, previousPeriod)));
            cplex.addGe(startingB, 0);
        }
        super.setUpModel(cplex);
    }

    private String getSimpleName(Variable variable, int period)
    {
        return String.format("%s_Period%s",variable, period);
    }

    @Override
    protected void addObjectiveTerms(IloCplex cplex, IloLinearNumExpr objective, int period) throws IloException
    {
        //add new terms to the objective before adding it
        objective.addTerm(90 * hours[period - 1], getVariable(getSimpleName(Variable.RunHA, period)));
        objective.addTerm(150 * hours[period - 1], getVariable(getSimpleName(Variable.RunHB, period)));
        objective.addTerm(1500, getVariable(getSimpleName(Variable.StartHA, period)));
        objective.addTerm(1200, getVariable(getSimpleName(Variable.StartHB, period)));

        super.addObjectiveTerms(cplex, objective, period);
    }

    @Override
    protected void addDemandConstraints(IloCplex cplex, int period, IloLinearNumExpr satisfyDemand, IloLinearNumExpr satisfyExtendedDemand) throws IloException
    {
        //add new terms to both demand satisfaction constraints before adding them
        satisfyDemand.addTerm(900, getVariable(getSimpleName(Variable.RunHA, period)));
        satisfyDemand.addTerm(1400, getVariable(getSimpleName(Variable.RunHB, period)));
        satisfyDemand.addTerm(-3000/hours[period - 1], getVariable(getSimpleName(Variable.Pumping, period)));

        satisfyExtendedDemand.addTerm(900, getVariable(getSimpleName(Variable.RunHA, period)));
        satisfyExtendedDemand.addTerm(1400, getVariable(getSimpleName(Variable.RunHB, period)));

        super.addDemandConstraints(cplex, period, satisfyDemand, satisfyExtendedDemand);
    }
}
