import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class Runner
{
    private static Exercise[] exercises = new Exercise[]{new Exercise12_1(), new Exercise12_6()};

    public static void main(String[] args) throws IloException
    {
        showDelimiter();
        for (Exercise exercise : exercises)
        {
            System.out.println(exercise.getClass().getSimpleName());
            showDelimiter();
            //CPLEX instance
            IloCplex cplex = new IloCplex();
            //Model
            exercise.setUpModel(cplex);
            //Solution
            cplex.solve();
            //Results
            System.out.println("Results:");
            exercise.showResults(cplex);
            showDelimiter();
        }

    }

    private static void showDelimiter()
    {
        System.out.println("====================================");
    }
}