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
    Double[][] distances = new Double[21][21];
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
        resolveDistances();
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
                        //IloLinearNumExpr cantGoThereAndBackDay = cplex.linearNumExpr();
                        //cantGoThereAndBackDay.addTerm(1,getVariable(getTour(day,farmTo,farmFrom)));
                        //cantGoThereAndBackDay.addTerm(1,getVariable(getTour(day,farmFrom,farmTo)));
                        //cplex.addLe(1,cantGoThereAndBackDay);

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
        //return 1.0;
        //because its Zero-based
        return distances[i-1][j-1];
    }

    private void resolveDistances()
    {
        for(int i = 0; i<21; i++){
            for (int j = 0; j<i ; j++){
                double distance = calculateSquareDistanceBetween(farmLocations[i],farmLocations[j]);
                distances[i][j] = distance;
                distances[j][i] = distance;
            }
            distances[i][i]= 0.0;
        }
    }
    private double calculateSquareDistanceBetween(Point point1, Point point2){
        return Math.pow((double)(point1.xAxis() - point2.xAxis()),2.0) + Math.pow((double)point1.yAxis() - point2.yAxis(),2.0);
    }
    private class Point {
        protected int x;
        protected int y;

        Point(int xComponent, int yComponent){
            x = xComponent;
            y = yComponent;
        }
        public int xAxis(){
            return x;
        }
        public int yAxis(){
            return y;
        }
    }
    @Override
    public void getPostExecutionData(IloCplex cplex) throws IloException{//ilog.cplex.IloCplex.UnknownObjectException{
        for(int day = 1; day <=2; day++){
            System.out.println("Path day "+String.valueOf(day)+":");
            for (int farmTo = 1; farmTo<=21;farmTo++){
                for(int farmFrom = 1; farmFrom<=21;farmFrom++){
                    if(farmTo != farmFrom){
                        String tour = getTour(1,farmFrom,farmTo);
                        IloNumVar cplexVar = getVariable(tour);
                        double visited = cplex.getValue(cplexVar);
                        if (visited > 0)
                            System.out.println(tour);
                        }
                }
            }
        }
    }
}
