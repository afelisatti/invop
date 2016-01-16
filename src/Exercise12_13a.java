import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;

public class Exercise12_13a extends Exercise12_13 {
    @Override
    public void setUpModel(IloCplex cplex) throws IloException{
        super.setUpModel(cplex);
        IloLinearNumExpr objective = cplex.linearNumExpr();
        objective.addTerm(100 / GetPercent(deliveryPoints,1),getVariable("plusDelivery"));
        objective.addTerm(100 / GetPercent(deliveryPoints,1),getVariable("minusDelivery"));
        objective.addTerm(100 / GetPercent(spiritMarkets,1),getVariable("plusSpirit"));
        objective.addTerm(100 / GetPercent(spiritMarkets,1),getVariable("minusSpirit"));
        objective.addTerm(100 / totalOilRegion1, getVariable("plusOil1"));
        objective.addTerm(100 / totalOilRegion1, getVariable("minusOil1"));
        objective.addTerm(100 / totalOilRegion2, getVariable("plusOil2"));
        objective.addTerm(100 / totalOilRegion2, getVariable("minusOil2"));
        objective.addTerm(100 / totalOilRegion3, getVariable("plusOil3"));
        objective.addTerm(100 / totalOilRegion3, getVariable("minusOil3"));
        cplex.addObjective(IloObjectiveSense.Minimize, objective);

    }
}
