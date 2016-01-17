/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

public class Exercise12_16 extends Exercise12_15
{

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
        //add new variables and their constraints
        super.setUpModel(cplex);
    }

    @Override
    protected void addObjective(IloCplex cplex, IloLinearNumExpr objective) throws IloException
    {
        //add new terms to the objective before adding it
        super.addObjective(cplex, objective);
    }

    @Override
    protected void addDemandConstraints(IloCplex cplex, int demand, IloLinearNumExpr satisfyDemand, IloLinearNumExpr satisfyExtendedDemand) throws IloException
    {
        //add new terms to both demand satisfaction constraints before addoing them
        super.addDemandConstraints(cplex, demand, satisfyDemand, satisfyExtendedDemand);
    }
}
