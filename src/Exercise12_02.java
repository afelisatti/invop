/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Exercise12_02 extends Exercise12_01
{

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
        super.setUpModel(cplex);

        for (int month = 1; month <= 6; month++)
        {
            IloLinearNumExpr booleanMonthlySum = cplex.linearNumExpr();

            for (String oil : oils)
            {
                String name = String.format("%s_%s", month, oil);
                IloNumVar usageVariable = setVariable(name, cplex.boolVar(name));

                IloLinearNumExpr lowerBound = cplex.linearNumExpr();
                lowerBound.addTerm(20, usageVariable);
                lowerBound.addTerm(-1, getVariable(getVariableName(oil, Kind.Refined, month)));
                cplex.addLe(lowerBound, 0);

                int limit = 250;
                if ("Veg1".equals(oil) || "Veg2".equals(oil))
                {
                    limit = 200;
                }
                IloLinearNumExpr upperBound = cplex.linearNumExpr();
                upperBound.addTerm(limit, usageVariable);
                upperBound.addTerm(-1, getVariable(getVariableName(oil, Kind.Refined, month)));
                cplex.addGe(upperBound, 0);

                booleanMonthlySum.addTerm(1, usageVariable);
            }

            cplex.addLe(booleanMonthlySum, 3);

            IloLinearNumExpr veg1oil3 = cplex.linearNumExpr();
            veg1oil3.addTerm(1, getVariable(String.format("%s_Veg1", month)));
            veg1oil3.addTerm(-1, getVariable(String.format("%s_Oil3", month)));
            cplex.addLe(veg1oil3, 0);

            IloLinearNumExpr veg2oil3 = cplex.linearNumExpr();
            veg2oil3.addTerm(1, getVariable(String.format("%s_Veg2", month)));
            veg2oil3.addTerm(-1, getVariable(String.format("%s_Oil3", month)));
            cplex.addLe(veg2oil3, 0);
        }

    }
}
