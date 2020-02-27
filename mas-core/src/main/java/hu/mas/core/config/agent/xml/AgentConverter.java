package hu.mas.core.config.agent.xml;

import java.util.List;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;

import hu.mas.core.agent.ImpactCalculatorType;
import hu.mas.core.agent.SimpleAgent;
import hu.mas.core.agent.SimpleRePlanAgent;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.config.agent.xml.model.AgentConfiguration;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;
import it.polito.appeal.traci.SumoTraciConnection;

public class AgentConverter {

	public static List<SimpleAgent> toSimpleAgents(AgentConfiguration configuration, Graph graph,
			SumoTraciConnection connection, ImpactCalculatorType impactCalculator) {
		return configuration.getAgents().stream()
				.map(e -> toSimpleAgent(e, graph.getNodes(), connection, impactCalculator))
				.collect(Collectors.toList());
	}

	public static List<SimpleRePlanAgent> toSimpleRePlanAgents(AgentConfiguration configuration, Graph graph,
			SumoTraciConnection connection, ImpactCalculatorType impactCalculator) {
		return configuration.getAgents().stream()
				.map(e -> toSimpleRePlanAgent(e, graph.getNodes(), connection, impactCalculator))
				.collect(Collectors.toList());
	}

	private static SimpleAgent toSimpleAgent(hu.mas.core.config.agent.xml.model.Agent agent, List<Node> nodes,
			SumoTraciConnection connection, ImpactCalculatorType impactCalculator) {
		SimpleAgent result = new SimpleAgent(agent.getId(),
				new Vehicle(agent.getVehicle().getId(), agent.getVehicle().getTypeId(), agent.getVehicle().getSpeed()),
				nodes.stream().filter(e -> e.getId().equals(agent.getFrom())).findFirst().orElseThrow(),
				nodes.stream().filter(e -> e.getId().equals(agent.getTo())).findFirst().orElseThrow(), null,
				connection);
		result.setSleepTime(agent.getSleepTime());
		result.getVehicle().setCaculateEdgeImpact(getImpactCalculator(impactCalculator));
		result.getVehicle().setImpactEnhancer(getImpactEnhancer(impactCalculator));
		return result;
	}

	private static SimpleRePlanAgent toSimpleRePlanAgent(hu.mas.core.config.agent.xml.model.Agent agent,
			List<Node> nodes, SumoTraciConnection connection, ImpactCalculatorType impactCalculator) {
		SimpleRePlanAgent result = new SimpleRePlanAgent(agent.getId(),
				new Vehicle(agent.getVehicle().getId(), agent.getVehicle().getTypeId(), agent.getVehicle().getSpeed()),
				nodes.stream().filter(e -> e.getId().equals(agent.getFrom())).findFirst().orElseThrow(),
				nodes.stream().filter(e -> e.getId().equals(agent.getTo())).findFirst().orElseThrow(), null,
				connection);
		result.setSleepTime(agent.getSleepTime());
		result.getVehicle().setCaculateEdgeImpact(getImpactCalculator(impactCalculator));
		result.getVehicle().setImpactEnhancer(getImpactEnhancer(impactCalculator));
		return result;
	}

	private static ToDoubleBiFunction<Edge, Vehicle> getImpactCalculator(ImpactCalculatorType impactCalculator) {
		ToDoubleBiFunction<Edge, Vehicle> result = null;
		switch (impactCalculator) {
		case CONSTANT:
			result = (a, b) -> 1.0;
			break;
		case VEHICLE_AND_EDGE_SPEED:
			result = (a, b) -> b.getSpeed() > a.getSpeed() ? 1.0 : a.getSpeed() / b.getSpeed();
			break;
		default:
			break;
		}

		return result;
	}

	private static ToDoubleBiFunction<Double, Double> getImpactEnhancer(ImpactCalculatorType impactCalculator) {
		ToDoubleBiFunction<Double, Double> result = null;
		switch (impactCalculator) {
		case CONSTANT:
			result = (a, b) -> a;
			break;
		case VEHICLE_AND_EDGE_SPEED:
			result = (a, b) -> a * b;
			break;
		default:
			break;
		}

		return result;
	}

	private AgentConverter() {
	}

}
