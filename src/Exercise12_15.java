/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;

public class Exercise12_15 extends Exercise
{

    public enum Variable {
        Amount, Started, Watts
    }

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
        int[] demand = new int[]{15000, 30000, 25000, 40000, 27000};
        int[] hours = new int[]{6, 3, 6, 3, 6};
        int[] maximumProduction = new int[]{850, 1250, 1500};
        int[] minimumProduction = new int[]{15000, 30000, 25000};
        int[] availableUnits = new int[]{12, 10, 5};
        int[] minimumCost = new int[]{1000, 2600, 3000};
        int[] startingCost = new int[]{2000, 1000, 500};
        Double[] price = new Double[]{2.0, 1.3, 3.0};

        //objective
        IloLinearNumExpr objective = cplex.linearNumExpr();

        for(int period = 1; period <= 5; period++)
        {
            //Produce what is needed
            IloLinearNumExpr satisfyDemand = cplex.linearNumExpr();
            //Be able to produce 15% more with the running generators
            IloLinearNumExpr satisfyExtendedDemand = cplex.linearNumExpr();

            for (int type = 1; type <= 3; type++)
            {
                //Set up variables
                String amountName = getVariableName(Variable.Amount, period, type);
                IloNumVar amount = setVariable(amountName, cplex.intVar(0, 15, amountName));
                String startedName = getVariableName(Variable.Started, period, type);
                IloNumVar start = setVariable(startedName, cplex.intVar(0, 30, startedName));
                String wattsName = getVariableName(Variable.Watts, period, type);
                IloNumVar watts = setVariable(wattsName, cplex.numVar(0.0, Double.MAX_VALUE, wattsName));

                //Started units logic
                IloLinearNumExpr startedUnits = cplex.linearNumExpr();
                startedUnits.addTerm(1, start);
                startedUnits.addTerm(-1, amount);
                if (period == 1)
                {
                    cplex.addEq(startedUnits, 0);
                }
                else
                {
                    startedUnits.addTerm(1, getVariable(getVariableName(Variable.Amount, period-1, type)));
                    cplex.addGe(startedUnits, 0);
                }

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
                satisfyExtendedDemand.addTerm(-1 , watts);
            }

            cplex.addGe(satisfyDemand, demand[period - 1]);

            cplex.addGe(satisfyExtendedDemand, 0.15*demand[period-1]);
        }

        cplex.addObjective(IloObjectiveSense.Minimize, objective);
    }

    private String getVariableName(Variable variable, int period, int type)
    {
        return String.format("%s_Type%s_Period%s", variable, type, period);
    }
}
