import java.util.Map;
import java.util.TreeMap;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public abstract class Exercise
{
    private Map<String, IloNumVar> variables = new TreeMap<>();

    public void showResults(IloCplex cplex) throws IloException
    {
        IloCplex.Status status = cplex.getStatus();
        System.out.println("Status = " + status);

        if(status.equals(IloCplex.Status.Optimal) || status.equals(IloCplex.Status.Feasible))
        {
            System.out.println("Objective = " + cplex.getObjValue());
            System.out.println("Variables:");
            for (String variable : variables.keySet())
            {
                System.out.println(String.format("%s = %s", variable, cplex.getValue(getVariable(variable))));
            }
            showDuals(cplex);
            getPostExecutionData(cplex);
        }
    }

    protected IloNumVar getVariable(String name)
    {
        return variables.get(name);
    }

    protected IloNumVar setVariable(String name, IloNumVar variable)
    {
        variables.put(name, variable);
        return variable;
    }

    public abstract void setUpModel(IloCplex cplex) throws IloException;

    public void showDuals(IloCplex cplex) throws IloException
    {
        return;
    }
    public void getPostExecutionData(IloCplex cplex) throws IloException    {    }

}

