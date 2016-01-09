import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

import java.util.Dictionary;
import java.util.Enumeration;

public class Exercise12_13 extends Exercise {

    @Override
    public void setUpModel(IloCplex cplex) throws IloException
    {
        String[] boolClasses = new String[]{"D1","D2"};
        String[] intClasses = new String[] {"oil","delivery","spirit"} ;
        String div2 = "D1_";
        String div1 = "D2_";
        //String retail = "R_";
        String oil = "oil"
        int[] oilGalons = new int[] {9,13,14,17,18,19,23,21,9,11,17,18,18,17,22,24,36,43,6,15,15,25,39};
        String delivery = "delivery";
        int[] deliveryPoints = new int[] {11,47,44,25,10,26,26,54,18,51,20,105,7,16,34,100,50,21,11,19,14,10,11};
        String spiritMarket = "spirit";
        int[] spiritMarkets = new int[] {34,411,82,157,5,183,14,215,102,21,54,0,6,96,118,112,535,8,53,28,69,65,27};
        String[] categoryA = new String[] {"1","2","3","5","6","10","15","20"};
        String[] categoryB = new String[] {"4","7","8","9","11","12","13","14","16","17","18","19","21","22","23"};
        Dictionary<String,int[]> fixedValues = new Dictionary<String,int[]>() ;
        fixedValues.put(delivery,deliveryPoints);
        fixedValues.put(oil,oilGalons);
        fixedValues.put(spiritMarket,spiritMarkets);

        //Variables and their fixed values
        for (int i = 1 ; i<24 ; i++) {
            String index = String.format("%s",i);
            //Bool variables: D1_x, D2_x
            for (String className : boolClasses) 
            {
                String varName = className + "_" + index;
                setVariable(varName, cplex.intVar(0, 1, varName));
            }
            
            //Int variables: oil_x, delivery_x, spirit_x
            for (String className : intClasses) 
            {
                String varName = className + "_" + index;
                setVariable(varName, cplex.intVar(0, Integer.MAX_VALUE, varName));
                //set fixed value to Vars
                IloLinearNumExpr expr = cplex.linearNumExpr();
                expr.addTerm(1,getVariable(varName));
                cplex.addEq(expr,fixedValues.get(className)[i-1]);
            }
        }
        
        //Restriction
        //Todas las D1_x + D2_y suman lo 23
        IloLinearNumExpr divisionRestriction = cplex.linearNumExpr();
        for (int i = 1; i<24; i++)
        {
            String index = String.format("%s",i);
            for(String className : boolClasses)
            {
                String varName = className + "_" + index;
                divisionRestriction.addTerm(1,getVariable(varName));
            }

        }
        cplex.addEq(divisionRestriction,23);

        //35-45 percent control of D1 in Category A
        IloLinearNumExpr categARestrictionForD1 = cplex.linearNumExpr();
        for(String retailerA : categoryA)
        {
            String varName = "D1_" + retailerA;
            categARestrictionForD1.addTerm(1,getVariable(varName));
        }
        cplex.addEq(categARestrictionForD1,3);

        //35-45 percent control of D1 in Category B
        IloLinearNumExpr categBRestrictionForD1 = cplex.linearNumExpr();
        for(String retailerB : categoryB)
        {
            String varName = "D1_" + retailerB;
            categBRestrictionForD1.addTerm(1,getVariable(varName));
        }
        cplex.addEq(categBRestrictionForD1,6);

        //35-45 percent control of D1 in total deliveryPoints
        IloLinearNumExpr deliveryPointsLowerRestrictionForD1 = cplex.linearNumExpr();
        IloLinearNumExpr deliveryPointsUpperRestrictionForD1 = cplex.linearNumExpr();
        for(int i = 1; i<24; i++)
        {
            String index = String.format("%s",i);
            String d1VarName = "D1_" + index;
            deliveryPointsLowerRestrictionForD1.addTerm(deliveryPoints[i-1],getVariable(d1VarName));
            deliveryPointsUpperRestrictionForD1.addTerm(deliveryPoints[i-1],getVariable(d1VarName));
        }
        cplex.addGe(deliveryPointsLowerRestrictionForD1,GetPercent(deliveryPoints, 0.35));
        cplex.addLe(deliveryPointsUpperRestrictionForD1, GetPercent(deliveryPoints, 0.45)   
        //FALTAN RESTRICCIONES
    }

    protected int GetPercent(int[] integerList,double percent)
    {
        int total = 0;
        for (int elem : integerList)
            total += elem;
        total *= percent;
        return total;
    }
}
