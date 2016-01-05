/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

import java.util.HashMap;
import java.util.Map;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public abstract class Exercise
{
    private Map<String, IloNumVar> variableMap = new HashMap<>();

    public void showResults(IloCplex cplex) throws IloException
    {
        IloCplex.Status status = cplex.getStatus();
        System.out.println("Status = " + status);

        if(status.equals(IloCplex.Status.Optimal) || status.equals(IloCplex.Status.Feasible))
        {
            System.out.println("Objective = " + cplex.getObjValue());
            System.out.println("Variables:");
            for (String variable : variableMap.keySet())
            {
                System.out.println(String.format("%s = %s", variable, cplex.getValue(getVariable(variable))));
            }
        }
    }

    protected IloNumVar getVariable(String name)
    {
        return variableMap.get(name);
    }

    protected void setVariable(String name, IloNumVar variable)
    {
        variableMap.put(name, variable);
    }

    public abstract void setUpModel(IloCplex cplex) throws IloException;
}

