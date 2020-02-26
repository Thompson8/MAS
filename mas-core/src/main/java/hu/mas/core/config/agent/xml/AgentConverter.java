package hu.mas.core.config.agent.xml;

import java.util.List;
import java.util.stream.Collectors;

import hu.mas.core.agent.SimpleAgent;
import hu.mas.core.agent.SimpleRePlanAgent;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.config.agent.xml.model.AgentConfiguration;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;
import it.polito.appeal.traci.SumoTraciConnection;

public class AgentConverter {

	public static List<SimpleAgent> toSimpleAgents(AgentConfiguration configuration, Graph graph,
			SumoTraciConnection connection) {
		return configuration.getAgents().stream().map(e -> toSimpleAgent(e, graph.getNodes(), connection))
				.collect(Collectors.toList());
	}

	public static List<SimpleRePlanAgent> toSimpleRePlanAgents(AgentConfiguration configuration, Graph graph,
			SumoTraciConnection connection) {
		return configuration.getAgents().stream().map(e -> toSimpleRePlanAgent(e, graph.getNodes(), connection))
				.collect(Collectors.toList());
	}

	private static SimpleAgent toSimpleAgent(hu.mas.core.config.agent.xml.model.Agent agent, List<Node> nodes,
			SumoTraciConnection connection) {
		SimpleAgent result = new SimpleAgent(agent.getId(),
				new Vehicle(agent.getVehicle().getId(), agent.getVehicle().getTypeId(), agent.getVehicle().getSpeed()),
				nodes.stream().filter(e -> e.getId().equals(agent.getFrom())).findFirst().orElseThrow(),
				nodes.stream().filter(e -> e.getId().equals(agent.getTo())).findFirst().orElseThrow(), null,
				connection);
		result.setSleepTime(agent.getSleepTime());
		return result;
	}

	private static SimpleRePlanAgent toSimpleRePlanAgent(hu.mas.core.config.agent.xml.model.Agent agent,
			List<Node> nodes, SumoTraciConnection connection) {
		SimpleRePlanAgent result = new SimpleRePlanAgent(agent.getId(),
				new Vehicle(agent.getVehicle().getId(), agent.getVehicle().getTypeId(), agent.getVehicle().getSpeed()),
				nodes.stream().filter(e -> e.getId().equals(agent.getFrom())).findFirst().orElseThrow(),
				nodes.stream().filter(e -> e.getId().equals(agent.getTo())).findFirst().orElseThrow(), null,
				connection);
		result.setSleepTime(agent.getSleepTime());
		return result;
	}

	private AgentConverter() {
	}

}
