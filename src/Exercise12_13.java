import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;

import java.util.HashMap;

public abstract class Exercise12_13 extends Exercise {

    protected String div1 = "D1_";
    protected int[] oilGalons = new int[] {9,13,14,17,18,19,23,21,9,11,17,18,18,17,22,24,36,43,6,15,15,25,39};
    protected int[] deliveryPoints = new int[] {11,47,44,25,10,26,26,54,18,51,20,105,7,16,34,100,50,21,11,19,14,10,11};
    protected int[] spiritMarkets = new int[] {34,411,82,157,5,183,14,215,102,21,54,0,6,96,118,112,535,8,53,28,69,65,27};
    protected String[] categoryA = new String[] {"1","2","3","5","6","10","15","20"};
    protected String[] categoryB = new String[] {"4","7","8","9","11","12","13","14","16","17","18","19","21","22","23"};
    protected double totalOilRegion1;
    protected double totalOilRegion2;
    protected double totalOilRegion3;
    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
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
        IloLinearNumExpr deliveryPointsRestrictionForD1 = cplex.linearNumExpr();
        for(int i = 1; i<24; i++)
        {
            String index = String.format("%s",i);
            String d1VarName = "D1_" + index;
            deliveryPointsRestrictionForD1.addTerm(deliveryPoints[i-1],getVariable(d1VarName));
        }
        setVariable("plusDelivery",cplex.numVar(0.0,GetPercent(deliveryPoints,0.05),"plusDelivery"));
        deliveryPointsRestrictionForD1.addTerm(1,getVariable("plusDelivery"));
        setVariable("minusDelivery",cplex.numVar(0.0,GetPercent(deliveryPoints,0.05),"minusDelivery"));
        deliveryPointsRestrictionForD1.addTerm(-1,getVariable("minusDelivery"));
        cplex.addEq(deliveryPointsRestrictionForD1,GetPercent(deliveryPoints, 0.40));
        
        //35-45 percent control of D1 in total alcohol
        IloLinearNumExpr spiritMarketsRestrictionForD1 = cplex.linearNumExpr();
        for(int i = 1; i<24; i++)
        {
            String index = String.format("%s",i);
            String d1VarName = "D1_" + index;
            spiritMarketsRestrictionForD1.addTerm(spiritMarkets[i-1],getVariable(d1VarName));
        }
        setVariable("plusSpirit",cplex.numVar(0.0,GetPercent(spiritMarkets,0.05),"plusSpirit"));
        spiritMarketsRestrictionForD1.addTerm(1,getVariable("plusSpirit"));
        setVariable("minusSpirit",cplex.numVar(0.0,GetPercent(spiritMarkets,0.05),"minusSpirit"));
        spiritMarketsRestrictionForD1.addTerm(-1,getVariable("minusSpirit"));
        cplex.addEq(spiritMarketsRestrictionForD1, GetPercent(spiritMarkets, 0.40));

        //35-45 percent control of D1 in oil per region
        IloLinearNumExpr firstOilRegionRestrictionForD1 = cplex.linearNumExpr();
        totalOilRegion1 = 0.0;
        for(int i = 1; i<9; i++)
        {
            String index = String.format("%s",i);
            String d1VarName = "D1_" + index;
            firstOilRegionRestrictionForD1.addTerm(oilGalons[i-1],getVariable(d1VarName));
            totalOilRegion1 += oilGalons[i-1];
        }
        setVariable("plusOil1",cplex.numVar(0.0,(totalOilRegion1 * 0.05),"plusOil1"));
        firstOilRegionRestrictionForD1.addTerm(1,getVariable("plusOil1"));
        setVariable("minusOil1",cplex.numVar(0.0,(totalOilRegion1* 0.05),"minusOil1"));
        firstOilRegionRestrictionForD1.addTerm(-1,getVariable("minusOil1"));
        cplex.addEq(firstOilRegionRestrictionForD1,  totalOilRegion1 * 0.40);
        
        IloLinearNumExpr secondOilRegionRestrictionForD1 = cplex.linearNumExpr();
        totalOilRegion2 = 0.0;
        for(int i = 9; i<19; i++)
        {
            String index = String.format("%s",i);
            String d1VarName = "D1_" + index;
            secondOilRegionRestrictionForD1.addTerm(oilGalons[i-1],getVariable(d1VarName));
            totalOilRegion2 += oilGalons[i-1];
        }
        setVariable("plusOil2",cplex.numVar(0.0,(totalOilRegion2 * 0.05),"plusOil2"));
        secondOilRegionRestrictionForD1.addTerm(1,getVariable("plusOil2"));
        setVariable("minusOil2",cplex.numVar(0.0,(totalOilRegion2* 0.05),"minusOil2"));
        secondOilRegionRestrictionForD1.addTerm(-1,getVariable("minusOil2"));
        cplex.addEq(secondOilRegionRestrictionForD1,  totalOilRegion2 * 0.40);


        IloLinearNumExpr thirdOilRegionRestrictionForD1 = cplex.linearNumExpr();

        totalOilRegion3 = 0;
        for(int i = 19; i<24; i++)
        {
            String index = String.format("%s",i);
            String d1VarName = "D1_" + index;
            thirdOilRegionRestrictionForD1.addTerm(oilGalons[i-1],getVariable(d1VarName));
            totalOilRegion3 += oilGalons[i-1];
        }
        setVariable("plusOil3",cplex.numVar(0.0,(totalOilRegion3 * 0.05),"plusOil3"));
        thirdOilRegionRestrictionForD1.addTerm(1,getVariable("plusOil3"));
        setVariable("minusOil3",cplex.numVar(0.0,(totalOilRegion3* 0.05),"minusOil3"));
        thirdOilRegionRestrictionForD1.addTerm(-1,getVariable("minusOil3"));
        cplex.addEq(thirdOilRegionRestrictionForD1,  totalOilRegion3 * 0.40);

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
