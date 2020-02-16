package hu.mas.core.config.agent.xml;

import java.util.List;
import java.util.stream.Collectors;

import hu.mas.core.agent.SimpleAgent;
import hu.mas.core.agent.SimpleRePlanAgent;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.config.agent.xml.model.AgentConfiguration;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;

public class AgentConverter {

	public static List<SimpleAgent> toSimpleAgents(AgentConfiguration configuration, Graph graph) {
		return configuration.getAgents().stream().map(e -> toSimpleAgent(e, graph.getNodes()))
				.collect(Collectors.toList());
	}

	public static List<SimpleRePlanAgent> toSimpleRePlanAgent(AgentConfiguration configuration, Graph graph) {
		return configuration.getAgents().stream().map(e -> toSimpleRePlanAgent(e, graph.getNodes()))
				.collect(Collectors.toList());
	}

	private static SimpleAgent toSimpleAgent(hu.mas.core.config.agent.xml.model.Agent agent, List<Node> nodes) {
		SimpleAgent result = new SimpleAgent(agent.getId(),
				new Vehicle(agent.getVehicle().getId(), agent.getVehicle().getTypeId(), agent.getVehicle().getSpeed()),
				nodes.stream().filter(e -> e.getId().equals(agent.getFrom())).findFirst().orElseThrow(),
				nodes.stream().filter(e -> e.getId().equals(agent.getTo())).findFirst().orElseThrow(), null);
		result.setSleepTime(agent.getSleepTime());
		return result;
	}

	private static SimpleRePlanAgent toSimpleRePlanAgent(hu.mas.core.config.agent.xml.model.Agent agent,
			List<Node> nodes) {
		SimpleRePlanAgent result = new SimpleRePlanAgent(agent.getId(),
				new Vehicle(agent.getVehicle().getId(), agent.getVehicle().getTypeId(), agent.getVehicle().getSpeed()),
				nodes.stream().filter(e -> e.getId().equals(agent.getFrom())).findFirst().orElseThrow(),
				nodes.stream().filter(e -> e.getId().equals(agent.getTo())).findFirst().orElseThrow(), null);
		result.setSleepTime(agent.getSleepTime());
		result.setRePlanIntervalTime(agent.getRePlanIntervalTime());
		return result;
	}

	private AgentConverter() {
	}

}
