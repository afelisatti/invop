/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

import java.util.HashMap;
import java.util.Map;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;

public class Exercise12_1
{
    private static final double[][] PRICES = {
                {110, 120, 130, 110, 115},
                {130, 130, 110, 90, 115},
                {110, 140, 130, 100, 95},
                {120, 110, 120, 120, 125},
                {100, 120, 150, 110, 105},
                {90, 100, 140, 80, 135}
            };

    public static void main(String[] args) throws IloException
    {
        //CPLEX instance
        IloCplex cplex = new IloCplex();
        //To set up all the variables, months will go 1 to 6
        //TODO: find a better way to handle all the variables and their references
        String[] oils = new String[]{"V1", "V2", "O1", "O2", "O3"};
        String[] kinds = new String[]{"C", "R" , "S"};
        //To hold the var references once in cplex by name
        Map<String, IloNumVar> variableMap = new HashMap<>();
        //Set up of every variable
        for(int i = 1; i <= 6; i++)
        {
            for (String oil : oils)
            {
                for (String kind : kinds)
                {
                    String varName = getVariableName(oil, kind, i);
                    variableMap.put(varName, cplex.numVar(0.0, Double.MAX_VALUE, varName));
                }
            }
        }

        IloLinearNumExpr objectiveExp = cplex.linearNumExpr();

        for (int month = 1; month <= 6; month++)
        {
            //Objective
            for(int oil = 0; oil <= 4; oil++)
            {
                //Earnings each month for each ton of refined oil
                objectiveExp.addTerm(150, variableMap.get(getVariableName(oils[oil], kinds[1], month)));
                //Storage cost each month for each ton of saved oil
                objectiveExp.addTerm(-5, variableMap.get(getVariableName(oils[oil], kinds[2], month)));
                //Monthly price for each ton of bought oil
                objectiveExp.addTerm(getPrice(oil, month), variableMap.get(getVariableName(oils[oil], kinds[0], month)));
            }

            //Restrictions
            //Refinement
            IloLinearNumExpr vegetableExp = cplex.linearNumExpr();
            vegetableExp.addTerm(1, variableMap.get(getVariableName(oils[0], kinds[1], month)));
            vegetableExp.addTerm(1, variableMap.get(getVariableName(oils[1], kinds[1], month)));
            cplex.addLe(vegetableExp, 200);
            IloLinearNumExpr oilExp = cplex.linearNumExpr();;
            oilExp.addTerm(1, variableMap.get(getVariableName(oils[2], kinds[1], month)));
            oilExp.addTerm(1, variableMap.get(getVariableName(oils[3], kinds[1], month)));
            oilExp.addTerm(1, variableMap.get(getVariableName(oils[4], kinds[1], month)));
            cplex.addLe(oilExp, 250);
            //Storage limit
            for (String oil : oils)
            {
                IloLinearNumExpr maxStorageExp = cplex.linearNumExpr();
                maxStorageExp.addTerm(1, variableMap.get(getVariableName(oil, kinds[2], month)));
            }
            //Hardness
            IloLinearNumExpr overExp = cplex.linearNumExpr();
            overExp.addTerm(5.3, variableMap.get(getVariableName(oils[0], kinds[1], month)));
            overExp.addTerm(3.1, variableMap.get(getVariableName(oils[1], kinds[1], month)));
            overExp.addTerm(-1, variableMap.get(getVariableName(oils[2], kinds[1], month)));
            overExp.addTerm(1.2, variableMap.get(getVariableName(oils[3], kinds[1], month)));
            overExp.addTerm(2, variableMap.get(getVariableName(oils[4], kinds[1], month)));
            cplex.addGe(overExp, 0);
            IloLinearNumExpr underExp = cplex.linearNumExpr();
            underExp.addTerm(2.8, variableMap.get(getVariableName(oils[0], kinds[1], month)));
            underExp.addTerm(0.1, variableMap.get(getVariableName(oils[1], kinds[1], month)));
            underExp.addTerm(-4, variableMap.get(getVariableName(oils[2], kinds[1], month)));
            underExp.addTerm(-1.8, variableMap.get(getVariableName(oils[3], kinds[1], month)));
            underExp.addTerm(-1, variableMap.get(getVariableName(oils[4], kinds[1], month)));
            cplex.addLe(underExp, 0);
        }
        //Restrictions for oils
        for (String oil : oils)
        {
            //January
            IloLinearNumExpr janExp = cplex.linearNumExpr();
            janExp.addTerm(1, variableMap.get(getVariableName(oil, kinds[2], 1)));
            janExp.addTerm(1, variableMap.get(getVariableName(oil, kinds[1], 1)));
            janExp.addTerm(-1, variableMap.get(getVariableName(oil, kinds[0], 1)));
            cplex.addEq(janExp, 500);
            for (int month = 2; month <= 5; month++)
            {
                IloLinearNumExpr genExp = cplex.linearNumExpr();
                genExp.addTerm(1, variableMap.get(getVariableName(oil, kinds[2], month-1)));
                genExp.addTerm(-1, variableMap.get(getVariableName(oil, kinds[1], 1)));
                genExp.addTerm(1, variableMap.get(getVariableName(oil, kinds[0], 1)));
                genExp.addTerm(-1, variableMap.get(getVariableName(oil, kinds[2], 1)));
                cplex.addEq(genExp, 0);
            }
            //June
            IloLinearNumExpr junExp = cplex.linearNumExpr();
            junExp.addTerm(1, variableMap.get(getVariableName(oil, kinds[2], 5)));
            junExp.addTerm(-1, variableMap.get(getVariableName(oil, kinds[1], 6)));
            junExp.addTerm(1, variableMap.get(getVariableName(oil, kinds[0], 6)));
            cplex.addEq(janExp, 500);
        }

        cplex.addObjective(IloObjectiveSense.Maximize, objectiveExp);

        //Solution
        cplex.solve();
        IloCplex.Status status = cplex.getStatus();
        System.out.println("Status = " + status);

        if(status.equals(IloCplex.Status.Optimal) || status.equals(IloCplex.Status.Feasible))
        {
            System.out.println("Objective = " + cplex.getObjValue());
            System.out.println("Variables:");
            for (String variable : variableMap.keySet())
            {
                System.out.println(String.format("%s = %s", variable, cplex.getValue(variableMap.get(variable))));
            }
        }

    }

    private static double getPrice(int oil, int month)
    {
        return -PRICES[month-1][oil];
    }

    private static String getVariableName(String oil, String kind, int month)
    {
        return String.format("%s_%s_%s", oil, kind, month);
    }

}
