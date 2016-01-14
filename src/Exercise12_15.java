/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class Exercise12_15 extends Exercise
{

    public enum Variable {
        Amount, Started, Watts
    }

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
        //Set up variables
        for(int period = 1; period <= 5; period++)
        {
            for (int type = 1; type <= 3; type++)
            {
                String amountName = getVariableName(Variable.Amount, period, type);
                setVariable(amountName, cplex.intVar(0, 15, amountName));
                String startedName = getVariableName(Variable.Started, period, type);
                setVariable(startedName, cplex.intVar(0, 30, startedName));
                String wattsName = getVariableName(Variable.Watts, period, type);
                setVariable(wattsName, cplex.numVar(0.0, Double.MAX_VALUE, wattsName));
            }
        }
    }

    private String getVariableName(Variable variable, int period, int type)
    {
        return String.format("%s_Type%s_Period%s", variable, type, period);
    }
}
