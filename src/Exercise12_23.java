/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class Exercise12_23 extends Exercise
{
    Double[][] distances = new Double[21][21];

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
        //add variables, constraints and objective
    }

    private Double getDistance(int i, int j)
    {
        return distances[i][j];
    }

    private void resolveDistances()
    {
        //here we should take the coordinates array and use it to populate the distances matrix
    }
}
