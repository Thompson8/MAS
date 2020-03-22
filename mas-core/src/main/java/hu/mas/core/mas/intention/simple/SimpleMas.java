package hu.mas.core.mas.intention.simple;

import java.util.List;
import java.util.Optional;

import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.path.PathFinder;
import hu.mas.core.simulation.SimulationEdgeImpactCalculatorType;
import it.polito.appeal.traci.SumoTraciConnection;

public class SimpleMas extends AbstractMas {

	protected final Intentions intentions;

	public SimpleMas(Graph graph, SumoTraciConnection connection, PathFinder pathFinder,
			SimulationEdgeImpactCalculatorType edgeWeightCalculator) {
		super(graph, connection, pathFinder, edgeWeightCalculator);
		this.intentions = new Intentions();
	}

	@Override
	protected double getValueForTravelWeigthMatrixUpdate(int x, int y, Edge edge, double currentTime) throws Exception {
		double edgeAvgTravelTime = getEdgeWeigth(edge);
		Optional<Double> lastVehicleFinishTime;
		Optional<List<Intention>> intentionsForEdge = intentions.findIntentions(edge);
		if (intentionsForEdge.isPresent()) {
			lastVehicleFinishTime = intentionsForEdge.get().stream().filter(e -> e.getStart() > currentTime)
					.map(Intention::getFinish).max((a, b) -> a.compareTo(b));
		} else {
			lastVehicleFinishTime = Optional.empty();
		}

		if (lastVehicleFinishTime.isPresent()) {
			double vehiclesTravelTime = lastVehicleFinishTime.get() - currentTime;
			return vehiclesTravelTime > edgeAvgTravelTime ? vehiclesTravelTime : edgeAvgTravelTime;
		} else {
			return edgeAvgTravelTime;
		}
	}

	@Override
	protected void registerRouteOperations(Vehicle vehicle, Route route) {
		route.getTravelTimeForEdges().entrySet().stream().forEach(e -> intentions.add(e.getKey(),
				new Intention(vehicle, e.getValue().getLeft(), e.getValue().getRigth())));
	}

}
