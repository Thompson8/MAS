package hu.mas.core.config.agent.xml;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hu.mas.core.agent.SimpleAgent;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.config.agent.xml.model.AgentConfiguration;
import hu.mas.core.config.route.xml.model.RoutesConfig;
import hu.mas.core.config.route.xml.model.VehicleType;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.mas.model.Vertex;
import it.polito.appeal.traci.SumoTraciConnection;

public class AgentConverter {

	public static List<SimpleAgent> toSimpleAgents(AgentConfiguration configuration, MasGraph graph,
			SumoTraciConnection connection, RoutesConfig routes) {
		return configuration.getAgents().stream()
				.map(e -> toSimpleAgent(e, graph.getVertexes(), connection, routes))
				.collect(Collectors.toList());
	}

	private static SimpleAgent toSimpleAgent(hu.mas.core.config.agent.xml.model.Agent agent, Collection<Vertex> nodes,
			SumoTraciConnection connection, RoutesConfig routes) {
		VehicleType type = findVehicleType(agent.getVehicle().getTypeId(), routes).orElseThrow();

		SimpleAgent result = new SimpleAgent(agent.getId(),
				new Vehicle(agent.getVehicle().getId(), type.getId(), type.getMaxSpeed(), type.getLength()),
				nodes.stream().filter(e -> e.getId().equals(agent.getFrom())).findFirst().orElseThrow(),
				nodes.stream().filter(e -> e.getId().equals(agent.getTo())).findFirst().orElseThrow(), null,
				connection);
		result.setSleepTime(agent.getSleepTime());
		result.setAgentStartInterval(agent.getAgentStartInterval());
		return result;
	}

	private static Optional<VehicleType> findVehicleType(String id, RoutesConfig routes) {
		return routes.getVehicleTypes().stream().filter(e -> e.getId().equals(id)).findFirst();
	}

	private AgentConverter() {
	}

}
