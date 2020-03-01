package hu.mas.core.config.agent.xml;

import java.util.List;
import java.util.Optional;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;

import hu.mas.core.agent.SimpleAgent;
import hu.mas.core.agent.SimpleRePlanAgent;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.config.agent.xml.model.AgentConfiguration;
import hu.mas.core.config.route.xml.model.RoutesConfig;
import hu.mas.core.config.route.xml.model.VehicleType;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;
import hu.mas.core.simulation.SimulationEdgeImpactCalculatorType;
import it.polito.appeal.traci.SumoTraciConnection;

public class AgentConverter {

	public static List<SimpleAgent> toSimpleAgents(AgentConfiguration configuration, Graph graph,
			SumoTraciConnection connection, SimulationEdgeImpactCalculatorType impactCalculator, RoutesConfig routes) {
		return configuration.getAgents().stream()
				.map(e -> toSimpleAgent(e, graph.getNodes(), connection, impactCalculator, routes))
				.collect(Collectors.toList());
	}

	public static List<SimpleRePlanAgent> toSimpleRePlanAgents(AgentConfiguration configuration, Graph graph,
			SumoTraciConnection connection, SimulationEdgeImpactCalculatorType impactCalculator, RoutesConfig routes) {
		return configuration.getAgents().stream()
				.map(e -> toSimpleRePlanAgent(e, graph.getNodes(), connection, impactCalculator, routes))
				.collect(Collectors.toList());
	}

	private static SimpleAgent toSimpleAgent(hu.mas.core.config.agent.xml.model.Agent agent, List<Node> nodes,
			SumoTraciConnection connection, SimulationEdgeImpactCalculatorType impactCalculator, RoutesConfig routes) {
		VehicleType type = findVehicleType(agent.getVehicle().getTypeId(), routes).orElseThrow();

		SimpleAgent result = new SimpleAgent(agent.getId(),
				new Vehicle(agent.getVehicle().getId(), type.getId(), type.getMaxSpeed(), type.getLength()),
				nodes.stream().filter(e -> e.getId().equals(agent.getFrom())).findFirst().orElseThrow(),
				nodes.stream().filter(e -> e.getId().equals(agent.getTo())).findFirst().orElseThrow(), null,
				connection);
		result.setSleepTime(agent.getSleepTime());
		result.getVehicle().setCaculateEdgeImpact(getImpactCalculator(impactCalculator));
		return result;
	}

	private static SimpleRePlanAgent toSimpleRePlanAgent(hu.mas.core.config.agent.xml.model.Agent agent,
			List<Node> nodes, SumoTraciConnection connection, SimulationEdgeImpactCalculatorType impactCalculator,
			RoutesConfig routes) {
		VehicleType type = findVehicleType(agent.getVehicle().getTypeId(), routes).orElseThrow();

		SimpleRePlanAgent result = new SimpleRePlanAgent(agent.getId(),
				new Vehicle(agent.getVehicle().getId(), type.getId(), type.getMaxSpeed(), type.getLength()),
				nodes.stream().filter(e -> e.getId().equals(agent.getFrom())).findFirst().orElseThrow(),
				nodes.stream().filter(e -> e.getId().equals(agent.getTo())).findFirst().orElseThrow(), null,
				connection);
		result.setSleepTime(agent.getSleepTime());
		result.getVehicle().setCaculateEdgeImpact(getImpactCalculator(impactCalculator));
		return result;
	}

	private static Optional<VehicleType> findVehicleType(String id, RoutesConfig routes) {
		return routes.getVehicleTypes().stream().filter(e -> e.getId().equals(id)).findFirst();
	}

	private static ToDoubleBiFunction<Edge, Vehicle> getImpactCalculator(
			SimulationEdgeImpactCalculatorType impactCalculator) {
		ToDoubleBiFunction<Edge, Vehicle> result = null;
		switch (impactCalculator) {
		case CONSTANT:
			result = (a, b) -> 1.0;
			break;
		case TRAVEL_TIME:
			result = (a, b) -> b.calculateTravelTime(a);
			break;
		default:
			break;
		}

		return result;
	}

	private AgentConverter() {
	}

}
