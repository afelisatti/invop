/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;

public class Exercise12_23 extends Exercise
{
    Double[][] distances = new Double[21][21];
    int[] demand = new int[]{0, 5, 4, 3, 6, 7, 3, 4, 6, 5, 4, 7, 3, 4, 5, 6, 8, 5, 7, 6, 6};

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
        //add variables, constraints and objective
        IloLinearNumExpr objective = cplex.linearNumExpr();

        for (int day = 1; day <= 2; day++)
        {
            IloLinearNumExpr dailyDemand = cplex.linearNumExpr();

            for (int farmFrom = 1; farmFrom <= 21; farmFrom++)
            {
                String visitName = getVisit(farmFrom, day);
                IloIntVar visit = (IloIntVar) setVariable(visitName, cplex.boolVar(visitName));

                dailyDemand.addTerm(demand[farmFrom -1], visit);

                IloLinearNumExpr farmFollows = cplex.linearNumExpr();

                for (int farmTo = 1; farmTo <= 21; farmTo++)
                {
                    if (farmFrom != farmTo)
                    {
                        String tourName = getTour(day, farmFrom, farmTo);
                        IloIntVar tour = (IloIntVar) setVariable(tourName, cplex.boolVar(tourName));

                        farmFollows.addTerm(1, tour);

                        objective.addTerm(getDistance(farmFrom, farmTo), tour);
                    }

                }

                farmFollows.addTerm(-1, visit);
                cplex.addEq(farmFollows, 0);
            }

            for (int farmTo = 1; farmTo <= 21; farmTo++)
            {
                IloLinearNumExpr farmPrecedes = cplex.linearNumExpr();

                for (int farmFrom = 1; farmFrom <= 21; farmFrom++)
                {
                    if (farmFrom != farmTo)
                    {
                        farmPrecedes.addTerm(1, getVariable(getTour(day, farmFrom, farmTo)));
                    }
                }

                farmPrecedes.addTerm(-1, getVariable(getVisit(farmTo, day)));
                cplex.addEq(farmPrecedes, 0);
            }

            cplex.addLe(dailyDemand, 80);
        }

        for (int farm = 1; farm <= 21; farm++)
        {
            IloLinearNumExpr dailyVisit = cplex.linearNumExpr();
            IloIntVar visit1 = (IloIntVar) getVariable(getVisit(farm, 1));
            IloIntVar visit2 = (IloIntVar) getVariable(getVisit(farm, 2));

            dailyVisit.addTerm(1, visit1);
            dailyVisit.addTerm(1, visit2);

            if (farm <= 10)
            {
                cplex.addEq(dailyVisit, 2);
            }
            else
            {
                cplex.addEq(dailyVisit, 1);
            }
        }

        cplex.addObjective(IloObjectiveSense.Minimize, objective);
    }

    private String getTour(int day, int farmTo, int farmFrom)
    {
        return String.format("Tour_%s_%s_Day%s", farmTo, farmFrom, day);
    }

    private String getVisit(int farm, int day)
    {
        return String.format("Visit_%s_Day%s", farm, day);
    }

    private Double getDistance(int i, int j)
    {
        return 1.0;
        //return distances[i][j];
    }

    private void resolveDistances()
    {
        //here we should take the coordinates array and use it to populate the distances matrix
    }
}
