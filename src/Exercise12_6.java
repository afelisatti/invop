import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

public class Exercise12_6 extends Exercise
{

    private String[] names = new String[]
            {
                    "C1", "C2",// (crude 1 and 2)
                    "LN", "MN", "HN",// (light, medium and heavy naphthas)
                    "LNR", "MNR", "HNR",// (light, medium and heavy naphthas reformed)
                    "RG",// (reformed gasoline)
                    "LNP", "MNP", "HNP",// (light, medium and heavy naphta petrol)
                    "LNRe", "MNRe", "HNRe",// (light, medium and heavy naphta regular)
                    "RGP", "RGRe",// (reformed gasoline petrol and regular)
                    "LiO", "HO",// (light and heavy oils)
                    "LiOJF", "LiOFO", "LiOC",// (light oil jet fuel, fuel oil and cracked oil)
                    "HOJF", "HOFO", "HOC",// (heavy oil jet fuel, fuel oil and cracked oil)
                    "R",// (residuum)
                    "RJF", "RFO", "RLO",// (residuum jet fuel, fuel oil and lube oil)
                    "CG", "CO",// (cracked gasoline and oil)
                    "COJF", "COFO",// (cracked oil jet fuel and fuel oil)
                    "CGP", "CGRe",// (cracked gasoline petrol and regular)
                    "PP", "RP", "JF", "FO", "LO"// (premium petrol, regular petrol, jet fuel, fuel oil, lube oil)
            };

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
        for (String name : names)
        {
            setVariable(name, cplex.numVar(0.0, Double.MAX_VALUE, name));
        }
        //Crude restriccions
        IloLinearNumExpr crude1Limit = cplex.linearNumExpr();
        crude1Limit.addTerm(1, getVariable("C1"));
        cplex.addLe(crude1Limit, 20000);

        IloLinearNumExpr crude2Limit = cplex.linearNumExpr();
        crude2Limit.addTerm(1, getVariable("C2"));
        cplex.addLe(crude2Limit, 30000);

        IloLinearNumExpr crudeLimit = cplex.linearNumExpr();
        crudeLimit.addTerm(1, getVariable("C1"));
        crudeLimit.addTerm(1, getVariable("C2"));
        cplex.addLe(crudeLimit, 45000);
        //Distillation restrictions
        IloLinearNumExpr crude1Dist = cplex.linearNumExpr();
        crude1Dist.addTerm(0.95, getVariable("C1"));
        crude1Dist.addTerm(-0.1, getVariable("LN"));
        crude1Dist.addTerm(-0.2, getVariable("MN"));
        crude1Dist.addTerm(-0.2, getVariable("HN"));
        crude1Dist.addTerm(-0.12, getVariable("LiO"));
        crude1Dist.addTerm(-0.2, getVariable("HO"));
        crude1Dist.addTerm(-0.13, getVariable("R"));
        cplex.addEq(crude1Dist, 0);

        IloLinearNumExpr crude2Dist = cplex.linearNumExpr();
        crude2Dist.addTerm(0.97, getVariable("C2"));
        crude2Dist.addTerm(-0.15, getVariable("LN"));
        crude2Dist.addTerm(-0.25, getVariable("MN"));
        crude2Dist.addTerm(-0.18, getVariable("HN"));
        crude2Dist.addTerm(-0.08, getVariable("LiO"));
        crude2Dist.addTerm(-0.19, getVariable("HO"));
        crude2Dist.addTerm(-0.12, getVariable("R"));
        cplex.addEq(crude2Dist, 0);
        //Naphtha restrictions
        IloLinearNumExpr lightNaphtha = cplex.linearNumExpr();
        lightNaphtha.addTerm(1, getVariable("LN"));
        lightNaphtha.addTerm(-1, getVariable("LNR"));
        lightNaphtha.addTerm(-1, getVariable("LNP"));
        lightNaphtha.addTerm(-1, getVariable("LNRe"));
        cplex.addEq(lightNaphtha, 0);

        IloLinearNumExpr mediumNaphtha = cplex.linearNumExpr();
        mediumNaphtha.addTerm(1, getVariable("MN"));
        mediumNaphtha.addTerm(-1, getVariable("MNR"));
        mediumNaphtha.addTerm(-1, getVariable("MNP"));
        mediumNaphtha.addTerm(-1, getVariable("MNRe"));
        cplex.addEq(mediumNaphtha, 0);

        IloLinearNumExpr heavyNaphtha = cplex.linearNumExpr();
        heavyNaphtha.addTerm(1, getVariable("HN"));
        heavyNaphtha.addTerm(-1, getVariable("HNR"));
        heavyNaphtha.addTerm(-1, getVariable("HNP"));
        heavyNaphtha.addTerm(-1, getVariable("HNRe"));
        cplex.addEq(heavyNaphtha, 0);
        //Reformed naphtha limit
        IloLinearNumExpr naphthaLimit = cplex.linearNumExpr();
        naphthaLimit.addTerm(1, getVariable("LNR"));
        naphthaLimit.addTerm(1, getVariable("MNR"));
        naphthaLimit.addTerm(1, getVariable("HNR"));
        cplex.addLe(naphthaLimit, 10000);
        //Reformed gasoline composition and destination
        IloLinearNumExpr reformedGasolineOrigin = cplex.linearNumExpr();
        reformedGasolineOrigin.addTerm(1, getVariable("RG"));
        reformedGasolineOrigin.addTerm(-0.6, getVariable("LNR"));
        reformedGasolineOrigin.addTerm(-0.52, getVariable("MNR"));
        reformedGasolineOrigin.addTerm(-0.45, getVariable("HNR"));
        cplex.addEq(reformedGasolineOrigin, 0);

        IloLinearNumExpr reformedGasolineDestination = cplex.linearNumExpr();
        reformedGasolineDestination.addTerm(1, getVariable("RG"));
        reformedGasolineDestination.addTerm(-1, getVariable("RGP"));
        reformedGasolineDestination.addTerm(-1, getVariable("RGRe"));
        cplex.addEq(reformedGasolineDestination, 0);
        //Oil restrictions
        IloLinearNumExpr lightOil = cplex.linearNumExpr();
        lightOil.addTerm(1, getVariable("LiO"));
        lightOil.addTerm(-1, getVariable("LiOJF"));
        lightOil.addTerm(-1, getVariable("LiOFO"));
        lightOil.addTerm(-1, getVariable("LiOC"));
        cplex.addEq(lightOil, 0);

        IloLinearNumExpr heavyOil = cplex.linearNumExpr();
        heavyOil.addTerm(1, getVariable("HO"));
        heavyOil.addTerm(-1, getVariable("HOJF"));
        heavyOil.addTerm(-1, getVariable("HOFO"));
        heavyOil.addTerm(-1, getVariable("HOC"));
        cplex.addEq(heavyOil, 0);

        IloLinearNumExpr oilLimit = cplex.linearNumExpr();
        oilLimit.addTerm(1, getVariable("LiO"));
        oilLimit.addTerm(1, getVariable("HO"));
        cplex.addLe(oilLimit, 8000);
        //Cracking restrictions
        IloLinearNumExpr crackedOilOrigin = cplex.linearNumExpr();
        crackedOilOrigin.addTerm(1, getVariable("CO"));
        crackedOilOrigin.addTerm(-0.68, getVariable("LiOC"));
        crackedOilOrigin.addTerm(-0.75, getVariable("HOC"));
        cplex.addEq(crackedOilOrigin, 0);

        IloLinearNumExpr crackedOilDestination = cplex.linearNumExpr();
        crackedOilDestination.addTerm(1, getVariable("CO"));
        crackedOilDestination.addTerm(-1, getVariable("COJF"));
        crackedOilDestination.addTerm(-1, getVariable("COFO"));
        cplex.addEq(crackedOilDestination, 0);

        IloLinearNumExpr crackedGasolineOrigin = cplex.linearNumExpr();
        crackedGasolineOrigin.addTerm(1, getVariable("CG"));
        crackedGasolineOrigin.addTerm(-0.28, getVariable("LiOC"));
        crackedGasolineOrigin.addTerm(-0.2, getVariable("HOC"));
        cplex.addEq(crackedGasolineOrigin, 0);

        IloLinearNumExpr crackedGasolineDestination = cplex.linearNumExpr();
        crackedGasolineDestination.addTerm(1, getVariable("CG"));
        crackedGasolineDestination.addTerm(-1, getVariable("CGP"));
        crackedGasolineDestination.addTerm(-1, getVariable("CGRe"));
        cplex.addEq(crackedGasolineDestination, 0);
        //Residuum restriction
        IloLinearNumExpr residuumDestination = cplex.linearNumExpr();
        residuumDestination.addTerm(1, getVariable("R"));
        residuumDestination.addTerm(-1, getVariable("RJF"));
        residuumDestination.addTerm(-1, getVariable("RFO"));
        residuumDestination.addTerm(-1, getVariable("RLO"));
        cplex.addEq(residuumDestination, 0);


    }
}
