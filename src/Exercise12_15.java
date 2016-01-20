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
import ilog.concert.IloObjectiveSense;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

public class Exercise12_15 extends Exercise
{

    public enum Variable {
        Amount, Started, Watts
    }

    protected int[] hours = new int[]{6, 3, 6, 3, 6};
    private int[] demand = new int[]{15000, 30000, 25000, 40000, 27000};
    private IloRange[] demandConstraints = new IloRange[5];

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
        int[] minimumProduction = new int[]{850, 1250, 1500};
        int[] maximumProduction = new int[]{2000, 1750, 4000};
        int[] availableUnits = new int[]{12, 10, 5};
        int[] minimumCost = new int[]{1000, 2600, 3000};
        int[] startingCost = new int[]{2000, 1000, 500};
        Double[] price = new Double[]{2.0, 1.3, 3.0};

        //objective
        IloLinearNumExpr objective = cplex.linearNumExpr();

        //Set up variables
        for(int period = 1; period <= 5; period++)
        {
            for (int type = 1; type <= 3; type++)
            {
                String amountName = getVariableName(Variable.Amount, period, type);
                setVariable(amountName, cplex.intVar(0, Integer.MAX_VALUE, amountName));
                String startedName = getVariableName(Variable.Started, period, type);
                setVariable(startedName, cplex.intVar(0, Integer.MAX_VALUE, startedName));
                String wattsName = getVariableName(Variable.Watts, period, type);
                setVariable(wattsName, cplex.numVar(0.0, Double.MAX_VALUE, wattsName));
            }
        }

        for(int period = 1; period <= 5; period++)
        {
            //Produce what is needed
            IloLinearNumExpr satisfyDemand = cplex.linearNumExpr();
            //Be able to produce 15% more with the running generators
            IloLinearNumExpr satisfyExtendedDemand = cplex.linearNumExpr();

            for (int type = 1; type <= 3; type++)
            {
                //Get variables
                String amountName = getVariableName(Variable.Amount, period, type);
                IloIntVar amount = (IloIntVar) getVariable(amountName);
                String startedName = getVariableName(Variable.Started, period, type);
                IloIntVar start = (IloIntVar) getVariable(startedName);
                String wattsName = getVariableName(Variable.Watts, period, type);
                IloNumVar watts = getVariable(wattsName);

                //Started units logic
                IloLinearNumExpr startedUnits = cplex.linearNumExpr();
                startedUnits.addTerm(1, start);
                startedUnits.addTerm(-1.0, amount);
                int previousPeriod = period - 1;
                if (period == 1)
                {
                    previousPeriod = 5;
                }
                startedUnits.addTerm(1, getVariable(getVariableName(Variable.Amount, previousPeriod, type)));
                cplex.addGe(startedUnits, 0);


                //Respect available units
                IloLinearNumExpr respectUnits = cplex.linearNumExpr();
                respectUnits.addTerm(1 , amount);
                cplex.addLe(respectUnits, availableUnits[type-1]);

                //Respect range
                IloLinearNumExpr respectMinimum = cplex.linearNumExpr();
                respectMinimum.addTerm(1, watts);
                respectMinimum.addTerm(-minimumProduction[type - 1], amount);
                cplex.addGe(respectMinimum, 0);
                IloLinearNumExpr respectMaximum = cplex.linearNumExpr();
                respectMaximum.addTerm(1 , watts);
                respectMaximum.addTerm(-maximumProduction[type - 1], amount);
                cplex.addLe(respectMaximum, 0);

                objective.addTerm(startingCost[type - 1], start);
                objective.addTerm(minimumCost[type - 1] * hours[period - 1], amount);
                objective.addTerm(price[type-1]*hours[period-1], watts);
                objective.addTerm(-minimumProduction[type-1]*price[type-1]*hours[period-1], amount);


                satisfyDemand.addTerm(1, watts);

                satisfyExtendedDemand.addTerm(maximumProduction[type - 1], amount);
            }

            addDemandConstraints(cplex, period, satisfyDemand, satisfyExtendedDemand);

            addObjectiveTerms(cplex, objective, period);
        }

        cplex.addObjective(IloObjectiveSense.Minimize, objective);
    }

    protected void addObjectiveTerms(IloCplex cplex, IloLinearNumExpr objective, int period) throws IloException
    {
        return;
    }

    protected void addDemandConstraints(IloCplex cplex, int period, IloLinearNumExpr satisfyDemand, IloLinearNumExpr satisfyExtendedDemand) throws IloException
    {
        demandConstraints[period-1] = cplex.addGe(satisfyDemand, demand[period-1]);

        cplex.addGe(satisfyExtendedDemand, 1.15 * demand[period-1]);
    }

    private String getVariableName(Variable variable, int period, int type)
    {
        return String.format("%s_Type%s_Period%s", variable, type, period);
    }

    @Override
    public void showDuals(IloCplex cplex) throws IloException
    {
        System.out.println("Duals: ");
        cplex.solveFixed();
        for (int period = 0; period <= 4; period++)
        {
            System.out.println(String.format("Period%s = %s", period + 1, cplex.getDual(demandConstraints[period])));
        }
    }
}
