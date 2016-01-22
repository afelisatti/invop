/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

import ilog.concert.*;
import ilog.cplex.IloCplex;
import ilog.concert.IloException;

public class Exercise12_23 extends Exercise
{
    int[] demand = new int[]{0, 5, 4, 3, 6, 7, 3, 4, 6, 5, 4, 7, 3, 4, 5, 6, 8, 5, 7, 6, 6};
    Point[] farmLocations = new Point[]{
            new Point(0,0),
            new Point(-3,3),
            new Point(1,11),
            new Point(4,7),
            new Point(-5,9),
            new Point(-5,-2),
            new Point(-4,-7),
            new Point(6,0),
            new Point(3,-6),
            new Point(-1,-3),
            new Point(0,-6),
            new Point(6,4),
            new Point(2,5),
            new Point(-2,8),
            new Point(6,10),
            new Point(1,8),
            new Point(-3,1),
            new Point(-6,5),
            new Point(2,9),
            new Point(-6,-5),
            new Point(5,-4)
    };

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
        //add variables, constraints and objective
        IloLinearNumExpr objective = cplex.linearNumExpr();

        for (int day = 1; day <= 2; day++)
        {
            IloLinearNumExpr dailyDemand = cplex.linearNumExpr();

            for (int farm = 1; farm <= 21; farm++)
            {
                String visitName = getVisit(farm, day);
                IloIntVar visit = (IloIntVar) setVariable(visitName, cplex.boolVar(visitName));

                if (day == 1 && farm == 11)
                {
                    IloLinearNumExpr firstDayElevel = cplex.linearNumExpr();
                    firstDayElevel.addTerm(1, visit);
                    cplex.addEq(firstDayElevel, 1);
                }

                dailyDemand.addTerm(demand[farm - 1], visit);

                IloLinearNumExpr farmReachedToAndFrom = cplex.linearNumExpr();

                for (int otherPreviousFarm = farm - 1; otherPreviousFarm >=1; otherPreviousFarm--)
                {
                    String tourName = getTour(day, otherPreviousFarm, farm);
                    IloIntVar tour = (IloIntVar) getVariable(tourName);

                    farmReachedToAndFrom.addTerm(1, tour);
                }

                for (int otherFollowingFarm = farm + 1; otherFollowingFarm <= 21; otherFollowingFarm++)
                {
                    String tourName = getTour(day, farm, otherFollowingFarm);
                    IloIntVar tour = (IloIntVar) setVariable(tourName, cplex.boolVar(tourName));

                    farmReachedToAndFrom.addTerm(1, tour);

                    objective.addTerm(getDistance(farm, otherFollowingFarm), tour);
                }

                farmReachedToAndFrom.addTerm(-2, visit);
                cplex.addEq(farmReachedToAndFrom, 0);
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

        //Adding extra constraints
        for (int day = 1; day <=2; day++)
        {
            IloLinearNumExpr two518Cycle = cplex.linearNumExpr();
            two518Cycle.addTerm(1, getVariable(getTour(day, 2, 5)));
            two518Cycle.addTerm(1, getVariable(getTour(day, 2, 18)));
            two518Cycle.addTerm(1, getVariable(getTour(day, 5, 18)));
            cplex.addLe(two518Cycle, 2);

            IloLinearNumExpr six720Cycle = cplex.linearNumExpr();
            six720Cycle.addTerm(1, getVariable(getTour(day, 6, 7)));
            six720Cycle.addTerm(1, getVariable(getTour(day, 6, 20)));
            six720Cycle.addTerm(1, getVariable(getTour(day, 7, 20)));
            cplex.addLe(six720Cycle, 2);

            IloLinearNumExpr eight921Cycle = cplex.linearNumExpr();
            eight921Cycle.addTerm(1, getVariable(getTour(day, 8, 9)));
            eight921Cycle.addTerm(1, getVariable(getTour(day, 8, 21)));
            eight921Cycle.addTerm(1, getVariable(getTour(day, 9, 21)));
            cplex.addLe(eight921Cycle, 2);
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
        return calculateDistanceBetween(farmLocations[i - 1], farmLocations[j - 1]);
    }

    private double calculateDistanceBetween(Point point1, Point point2){
        return Math.hypot((double)point1.xAxis() - point2.xAxis(),(double)point1.yAxis() - point2.yAxis());
    }

    @Override
    public void getPostExecutionData(IloCplex cplex) throws IloException{
        for(int day = 1; day <=2; day++)
        {
            System.out.println("Path day "+String.valueOf(day)+":");
            for (int farm = 1; farm <= 21; farm++)
            {
                String visitName = getVisit(farm, day);
                IloIntVar visit = (IloIntVar) getVariable(visitName);

                if (cplex.getValue(visit) > 0)
                {
                    for (int otherFarm = farm + 1; otherFarm <=21; otherFarm++)
                    {
                        String tourName = getTour(day, farm, otherFarm);
                        IloIntVar tour = (IloIntVar) getVariable(tourName);

                        if (cplex.getValue(tour) > 0)
                        {
                            System.out.println(tourName);
                        }
                    }
                }
            }
        }
    }

    private class Point
    {
        protected int x;
        protected int y;

        Point(int xComponent, int yComponent)
        {
            x = xComponent;
            y = yComponent;
        }

        public int xAxis()
        {
            return x;
        }
        public int yAxis()
        {
            return y;
        }
    }
}
