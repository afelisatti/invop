import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;

import java.util.HashMap;

public class Exercise12_13 extends Exercise {

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
        String div1 = "D1_";
        String oil = "oil";
        int[] oilGalons = new int[] {9,13,14,17,18,19,23,21,9,11,17,18,18,17,22,24,36,43,6,15,15,25,39};
        String delivery = "delivery";
        int[] deliveryPoints = new int[] {11,47,44,25,10,26,26,54,18,51,20,105,7,16,34,100,50,21,11,19,14,10,11};
        String spiritMarket = "spirit";
        int[] spiritMarkets = new int[] {34,411,82,157,5,183,14,215,102,21,54,0,6,96,118,112,535,8,53,28,69,65,27};
        String[] categoryA = new String[] {"1","2","3","5","6","10","15","20"};
        String[] categoryB = new String[] {"4","7","8","9","11","12","13","14","16","17","18","19","21","22","23"};


        //Variables and their fixed values
        for (int i = 1 ; i<24 ; i++) {
            String index = String.format("%s",i);
            //Bool variables: D1_x
            String varName = div1 + index;
            setVariable(varName, cplex.intVar(0, 1, varName));
        }

        //35-45 percent control of D1 in Category A
        IloLinearNumExpr categARestrictionForD1 = cplex.linearNumExpr();
        for(String retailerA : categoryA)
        {
            String varName = div1 + retailerA;
            categARestrictionForD1.addTerm(1,getVariable(varName));
        }
        cplex.addEq(categARestrictionForD1,3);

        //35-45 percent control of D1 in Category B
        IloLinearNumExpr categBRestrictionForD1 = cplex.linearNumExpr();
        for(String retailerB : categoryB)
        {
            String varName = div1 + retailerB;
            categBRestrictionForD1.addTerm(1,getVariable(varName));
        }
        cplex.addEq(categBRestrictionForD1,6);

        //35-45 percent control of D1 in total deliveryPoints
        IloLinearNumExpr deliveryPointsLowerRestrictionForD1 = cplex.linearNumExpr();
        //IloLinearNumExpr deliveryPointsUpperRestrictionForD1 = cplex.linearNumExpr();
        for(int i = 1; i<24; i++)
        {
            String index = String.format("%s",i);
            String d1VarName = "D1_" + index;
            deliveryPointsLowerRestrictionForD1.addTerm(deliveryPoints[i-1],getVariable(d1VarName));
            //deliveryPointsUpperRestrictionForD1.addTerm(deliveryPoints[i-1],getVariable(d1VarName));
        }
        setVariable("plusDelivery",cplex.numVar(0.0,GetPercent(deliveryPoints,0.05),"plusDelivery"));
        deliveryPointsLowerRestrictionForD1.addTerm(1,getVariable("plusDelivery"));
        setVariable("minusDelivery",cplex.numVar(0.0,GetPercent(deliveryPoints,0.05),"minusDelivery"));
        deliveryPointsLowerRestrictionForD1.addTerm(-1,getVariable("minusDelivery"));
        cplex.addEq(deliveryPointsLowerRestrictionForD1,GetPercent(deliveryPoints, 0.40));
        //cplex.addGe(deliveryPointsLowerRestrictionForD1,GetPercent(deliveryPoints, 0.35));
        //cplex.addLe(deliveryPointsUpperRestrictionForD1, GetPercent(deliveryPoints, 0.45));

        //35-45 percent control of D1 in total alcohol
        IloLinearNumExpr spiritMarketsLowerRestrictionForD1 = cplex.linearNumExpr();
        //IloLinearNumExpr spiritMarketsUpperRestrictionForD1 = cplex.linearNumExpr();
        for(int i = 1; i<24; i++)
        {
            String index = String.format("%s",i);
            String d1VarName = "D1_" + index;
            spiritMarketsLowerRestrictionForD1.addTerm(spiritMarkets[i-1],getVariable(d1VarName));
            //spiritMarketsUpperRestrictionForD1.addTerm(spiritMarkets[i-1],getVariable(d1VarName));
        }
        setVariable("plusSpirit",cplex.numVar(0.0,GetPercent(spiritMarkets,0.05),"plusSpirit"));
        spiritMarketsLowerRestrictionForD1.addTerm(1,getVariable("plusSpirit"));
        setVariable("minusSpirit",cplex.numVar(0.0,GetPercent(spiritMarkets,0.05),"minusSpirit"));
        spiritMarketsLowerRestrictionForD1.addTerm(-1,getVariable("minusSpirit"));
        cplex.addEq(spiritMarketsLowerRestrictionForD1, GetPercent(spiritMarkets, 0.40));
        //cplex.addGe(spiritMarketsLowerRestrictionForD1, GetPercent(spiritMarkets, 0.35));
        //cplex.addLe(spiritMarketsUpperRestrictionForD1, GetPercent(spiritMarkets, 0.45));

        //35-45 percent control of D1 in oil per region
        IloLinearNumExpr firstOilRegionLowerRestrictionForD1 = cplex.linearNumExpr();
        //IloLinearNumExpr firstOilRegionUpperRestrictionForD1 = cplex.linearNumExpr();
        double totalOilRegion1 = 0.0;
        for(int i = 1; i<9; i++)
        {
            String index = String.format("%s",i);
            String d1VarName = "D1_" + index;
            firstOilRegionLowerRestrictionForD1.addTerm(oilGalons[i-1],getVariable(d1VarName));
            //firstOilRegionUpperRestrictionForD1.addTerm(oilGalons[i-1],getVariable(d1VarName));
            totalOilRegion1 += oilGalons[i-1];
        }
        setVariable("plusOil1",cplex.numVar(0.0,(totalOilRegion1 * 0.05),"plusOil1"));
        firstOilRegionLowerRestrictionForD1.addTerm(1,getVariable("plusOil1"));
        setVariable("minusOil1",cplex.numVar(0.0,(totalOilRegion1* 0.05),"minusOil1"));
        firstOilRegionLowerRestrictionForD1.addTerm(-1,getVariable("minusOil1"));
        cplex.addEq(firstOilRegionLowerRestrictionForD1,  totalOilRegion1 * 0.40);
        //cplex.addGe(firstOilRegionLowerRestrictionForD1,  totalOilRegion1 * 0.35);
        //cplex.addLe(firstOilRegionUpperRestrictionForD1, totalOilRegion1 *  0.45);

        IloLinearNumExpr secondOilRegionLowerRestrictionForD1 = cplex.linearNumExpr();
        //IloLinearNumExpr secondOilRegionUpperRestrictionForD1 = cplex.linearNumExpr();
        double totalOilRegion2 = 0.0;
        for(int i = 9; i<19; i++)
        {
            String index = String.format("%s",i);
            String d1VarName = "D1_" + index;
            secondOilRegionLowerRestrictionForD1.addTerm(oilGalons[i-1],getVariable(d1VarName));
            //secondOilRegionUpperRestrictionForD1.addTerm(oilGalons[i-1],getVariable(d1VarName));
            totalOilRegion2 += oilGalons[i-1];
        }
        setVariable("plusOil2",cplex.numVar(0.0,(totalOilRegion2 * 0.05),"plusOil2"));
        secondOilRegionLowerRestrictionForD1.addTerm(1,getVariable("plusOil2"));
        setVariable("minusOil2",cplex.numVar(0.0,(totalOilRegion2* 0.05),"minusOil2"));
        secondOilRegionLowerRestrictionForD1.addTerm(-1,getVariable("minusOil2"));
        cplex.addEq(secondOilRegionLowerRestrictionForD1,  totalOilRegion2 * 0.40);
        //cplex.addGe(secondOilRegionLowerRestrictionForD1,  totalOilRegion2 * 0.35);
        //cplex.addLe(secondOilRegionUpperRestrictionForD1, totalOilRegion2 * 0.45);

        IloLinearNumExpr thirdOilRegionLowerRestrictionForD1 = cplex.linearNumExpr();
        //IloLinearNumExpr thirdOilRegionUpperRestrictionForD1 = cplex.linearNumExpr();
        double totalOilRegion3 = 0;
        for(int i = 19; i<24; i++)
        {
            String index = String.format("%s",i);
            String d1VarName = "D1_" + index;
            thirdOilRegionLowerRestrictionForD1.addTerm(oilGalons[i-1],getVariable(d1VarName));
            //thirdOilRegionUpperRestrictionForD1.addTerm(oilGalons[i-1],getVariable(d1VarName));
            totalOilRegion3 += oilGalons[i-1];
        }
        setVariable("plusOil3",cplex.numVar(0.0,(totalOilRegion3 * 0.05),"plusOil3"));
        thirdOilRegionLowerRestrictionForD1.addTerm(1,getVariable("plusOil3"));
        setVariable("minusOil3",cplex.numVar(0.0,(totalOilRegion3* 0.05),"minusOil3"));
        thirdOilRegionLowerRestrictionForD1.addTerm(-1,getVariable("minusOil3"));
        cplex.addEq(thirdOilRegionLowerRestrictionForD1,  totalOilRegion3 * 0.40);
        //cplex.addGe(thirdOilRegionLowerRestrictionForD1, totalOilRegion3 * 0.35);
        //cplex.addLe(thirdOilRegionUpperRestrictionForD1, totalOilRegion3 *  0.45);

        IloLinearNumExpr objective = cplex.linearNumExpr();
/*
        for(int i = 1; i<24; i++)
        {
            String index = String.format("%s",i);
            String d1VarName = "D1_" + index;
            objective.addTerm((deliveryPoints[i-1]*100) / (GetPercent(deliveryPoints,0.4)), getVariable(d1VarName));
            objective.addTerm((spiritMarkets[i-1]*100) / (GetPercent(spiritMarkets,0.4)), getVariable(d1VarName));
            if(i<9)
                objective.addTerm((oilGalons[i-1]*100) / (totalOilRegion1 * 0.4), getVariable(d1VarName));
            if (i >= 9 && i<19)
                objective.addTerm((oilGalons[i-1]*100) / (totalOilRegion2 * 0.4), getVariable(d1VarName));
            if (i >= 9 && i>=19)
                objective.addTerm((oilGalons[i-1]*100) / (totalOilRegion3 * 0.4), getVariable(d1VarName));
        }
*/
        objective.addTerm(1,getVariable("plusDelivery"));
        objective.addTerm(1,getVariable("minusDelivery"));
        objective.addTerm(1,getVariable("plusSpirit"));
        objective.addTerm(1,getVariable("minusSpirit"));
        objective.addTerm(1,getVariable("plusOil1"));
        objective.addTerm(1,getVariable("minusOil1"));
        objective.addTerm(1,getVariable("plusOil2"));
        objective.addTerm(1,getVariable("minusOil2"));
        objective.addTerm(1,getVariable("plusOil3"));
        objective.addTerm(1,getVariable("minusOil3"));
        cplex.addObjective(IloObjectiveSense.Minimize, objective);
/*
        double total = 0.0;
        GetPercent(oilGalons,1);
        System.out.println("Restar al resultado de cplex el total esperado: ");
        System.out.print(total);
*/
    }

    protected double GetPercent(int[] integerList,double percent)
    {
        double total = 0.0;
        for (int elem : integerList)
            total += elem;
        total *= percent;
        return total;
    }
}
