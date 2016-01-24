import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;

public class Exercise12_01 extends Exercise
{
    //To set up all the variables, months will go 1 to 6
    //TODO: find a better way to handle all the variables and their references
    public enum Kind {
        Bought, Refined, Saved
    }

    protected String[] oils = new String[]{"Veg1", "Veg2", "Oil1", "Oil2", "Oil3"};

    private static final double[][] PRICES = {
                {110, 120, 130, 110, 115},
                {130, 130, 110, 90, 115},
                {110, 140, 130, 100, 95},
                {120, 110, 120, 120, 125},
                {100, 120, 150, 110, 105},
                {90, 100, 140, 80, 135}
            };

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {

        //Set up of every variable
        for(int i = 1; i <= 6; i++)
        {
            for (String oil : oils)
            {
                for (Kind kind : Kind.values())
                {
                    String varName = getVariableName(oil, kind, i);
                    setVariable(varName, cplex.numVar(0.0, Double.MAX_VALUE, varName));
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
                objectiveExp.addTerm(150, getVariable(getVariableName(oils[oil], Kind.Refined, month)));
                //Storage cost each month for each ton of saved oil
                objectiveExp.addTerm(-5, getVariable(getVariableName(oils[oil], Kind.Saved, month)));
                //Monthly price for each ton of bought oil
                objectiveExp.addTerm(-getPrice(oil, month), getVariable(getVariableName(oils[oil], Kind.Bought, month)));
            }

            //Restrictions
            //Refinement
            IloLinearNumExpr vegetableExp = cplex.linearNumExpr();
            vegetableExp.addTerm(1, getVariable(getVariableName(oils[0], Kind.Refined, month)));
            vegetableExp.addTerm(1, getVariable(getVariableName(oils[1], Kind.Refined, month)));
            cplex.addLe(vegetableExp, 200);
            IloLinearNumExpr oilExp = cplex.linearNumExpr();
            oilExp.addTerm(1, getVariable(getVariableName(oils[2], Kind.Refined, month)));
            oilExp.addTerm(1, getVariable(getVariableName(oils[3], Kind.Refined, month)));
            oilExp.addTerm(1, getVariable(getVariableName(oils[4], Kind.Refined, month)));
            cplex.addLe(oilExp, 250);
            //Storage limit
            for (String oil : oils)
            {
                IloLinearNumExpr maxStorageExp = cplex.linearNumExpr();
                maxStorageExp.addTerm(1, getVariable(getVariableName(oil, Kind.Saved, month)));
                cplex.addLe(maxStorageExp, 1000);
            }
            //Hardness
            IloLinearNumExpr overExp = cplex.linearNumExpr();
            overExp.addTerm(5.8, getVariable(getVariableName(oils[0], Kind.Refined, month)));
            overExp.addTerm(3.1, getVariable(getVariableName(oils[1], Kind.Refined, month)));
            overExp.addTerm(-1, getVariable(getVariableName(oils[2], Kind.Refined, month)));
            overExp.addTerm(1.2, getVariable(getVariableName(oils[3], Kind.Refined, month)));
            overExp.addTerm(2, getVariable(getVariableName(oils[4], Kind.Refined, month)));
            cplex.addGe(overExp, 0);
            IloLinearNumExpr underExp = cplex.linearNumExpr();
            underExp.addTerm(2.8, getVariable(getVariableName(oils[0], Kind.Refined, month)));
            underExp.addTerm(0.1, getVariable(getVariableName(oils[1], Kind.Refined, month)));
            underExp.addTerm(-4, getVariable(getVariableName(oils[2], Kind.Refined, month)));
            underExp.addTerm(-1.8, getVariable(getVariableName(oils[3], Kind.Refined, month)));
            underExp.addTerm(-1, getVariable(getVariableName(oils[4], Kind.Refined, month)));
            cplex.addLe(underExp, 0);
        }
        //Restrictions for oils
        for (String oil : oils)
        {
            //January
            IloLinearNumExpr janExp = cplex.linearNumExpr();
            janExp.addTerm(-1, getVariable(getVariableName(oil, Kind.Refined, 1)));
            janExp.addTerm(1, getVariable(getVariableName(oil, Kind.Bought, 1)));
            janExp.addTerm(-1, getVariable(getVariableName(oil, Kind.Saved, 1)));
            cplex.addEq(janExp, -500);
            //Remaining months
            for (int month = 2; month <= 6; month++)
            {
                IloLinearNumExpr genExp = cplex.linearNumExpr();
                genExp.addTerm(1, getVariable(getVariableName(oil, Kind.Saved, month - 1)));
                genExp.addTerm(-1, getVariable(getVariableName(oil, Kind.Refined, month)));
                genExp.addTerm(1, getVariable(getVariableName(oil, Kind.Bought, month)));
                genExp.addTerm(-1, getVariable(getVariableName(oil, Kind.Saved, month)));
                cplex.addEq(genExp, 0);
            }
            //Fixed june savings
            IloLinearNumExpr fixedFinalSavings = cplex.linearNumExpr();
            fixedFinalSavings.addTerm(1, getVariable(getVariableName(oil, Kind.Saved, 6)));
            cplex.addEq(fixedFinalSavings, 500);
        }

        cplex.addObjective(IloObjectiveSense.Maximize, objectiveExp);
    }

    private static double getPrice(int oil, int month)
    {
        return PRICES[month-1][oil];
    }

    protected static String getVariableName(String oil, Kind kind, int month)
    {
        return String.format("%s_%s_%s", month, oil, kind);
    }
}
