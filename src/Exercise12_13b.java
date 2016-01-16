import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;

public class Exercise12_13b extends Exercise12_13 {
    @Override
    public void setUpModel(IloCplex cplex) throws IloException{
        super.setUpModel(cplex);
        //variable a minimizar, el desvio maximo de todos
        setVariable("maxDesvio", cplex.numVar(0, Float.MAX_VALUE, "maxDesvio"));
        IloLinearNumExpr maxDesvioTerm = cplex.linearNumExpr();
        maxDesvioTerm.addTerm(1,getVariable("maxDesvio"));

        // maximo desvio es mas grande que los desvios de delivery
        IloLinearNumExpr deliveryPercents = cplex.linearNumExpr();
        deliveryPercents.addTerm(100 / GetPercent(deliveryPoints, 1), getVariable("plusDelivery"));
        deliveryPercents.addTerm(100 / GetPercent(deliveryPoints, 1), getVariable("minusDelivery"));

        cplex.addLe(deliveryPercents, maxDesvioTerm);
        // maximo desvio es mas grande que los desvios de spirits
        IloLinearNumExpr spiritPercents = cplex.linearNumExpr();
        spiritPercents.addTerm(100 / GetPercent(spiritMarkets,1),getVariable("plusSpirit"));
        spiritPercents.addTerm(100 / GetPercent(spiritMarkets,1),getVariable("minusSpirit"));
        cplex.addLe(spiritPercents,maxDesvioTerm);
        // maximo desvio es mas grande que los desvios de oil1
        IloLinearNumExpr firstOilPercents = cplex.linearNumExpr();
        //firstOilPercents.addTerm(100 / totalOilRegion1,getVariable("plusOil1"));
        firstOilPercents.addTerm(100 / totalOilRegion1,getVariable("minusOil1"));
        cplex.addLe(firstOilPercents,maxDesvioTerm);
        // maximo desvio es mas grande que los desvios de oil2
        IloLinearNumExpr secondOilPercents = cplex.linearNumExpr();
        secondOilPercents.addTerm(100 / totalOilRegion2,getVariable("plusOil2"));
        secondOilPercents.addTerm(100 / totalOilRegion2,getVariable("minusOil2"));
        cplex.addLe(secondOilPercents,maxDesvioTerm);
        // maximo desvio es mas grande que los desvios de oil3
        IloLinearNumExpr thirdOilPercents = cplex.linearNumExpr();
        thirdOilPercents.addTerm(100 / totalOilRegion3,getVariable("plusOil3"));
        thirdOilPercents.addTerm(100 / totalOilRegion3,getVariable("minusOil3"));
        cplex.addLe(thirdOilPercents,maxDesvioTerm);

        IloLinearNumExpr objective2 = cplex.linearNumExpr();
        objective2.addTerm(1,getVariable("maxDesvio"));
        cplex.addObjective(IloObjectiveSense.Minimize, objective2);

    }
}

