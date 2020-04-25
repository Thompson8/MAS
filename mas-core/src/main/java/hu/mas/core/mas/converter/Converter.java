package hu.mas.core.mas.converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hu.mas.core.config.net.xml.model.Connection;
import hu.mas.core.config.net.xml.model.Edge;
import hu.mas.core.config.net.xml.model.Junction;
import hu.mas.core.config.net.xml.model.Lane;
import hu.mas.core.config.net.xml.model.Net;
import hu.mas.core.mas.model.graph.InternalEdge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.model.graph.MasGraphImpl;
import hu.mas.core.mas.model.graph.Vertex;
import hu.mas.core.util.Pair;

public class Converter {

	public static MasGraph fromNetToGraph(Net net, List<String> typeList) {
		List<Edge> edges = getValidEdges(net, typeList);
		MasGraph graph = new MasGraphImpl();

		edges.stream().map(e -> Arrays.asList(e.getFrom(), e.getTo())).flatMap(Collection::stream).distinct()
				.forEach(graph::addVertex);

		edges.forEach(e -> {
			if (!graph.containsEdge(e.getFrom(), e.getTo())) {
				graph.addEdge(e.getId(), e.getFrom(), e.getTo(), getSpeed(e), getLength(e));
			}
		});

		createJunctions(net, graph);

		return graph;
	}

	protected static void createJunctions(Net net, MasGraph graph) {
		List<Pair<Edge, Lane>> lanes = net.getEdges().stream()
				.map(e -> e.getLanes().stream().map(a -> new Pair<>(e, a))).flatMap(x -> x)
				.collect(Collectors.toList());

		for (Junction junction : net.getJunctions()) {
			Optional<Vertex> optVertex = graph.findVertex(junction.getId());
			if (optVertex.isPresent()) {
				createJunction(optVertex.get(), lanes, net, graph);
			}
		}
	}

	protected static void createJunction(Vertex vertex, List<Pair<Edge, Lane>> lanes, Net net, MasGraph graph) {
		hu.mas.core.mas.model.graph.Junction internalJunction = new hu.mas.core.mas.model.graph.Junction(vertex);

		for (hu.mas.core.mas.model.graph.Edge incomingEdge : vertex.getIncomingEdges()) {
			List<Connection> connections = net.getConnections().stream().filter(e -> e.getVia() != null)
					.filter(e -> e.getFrom().equals(incomingEdge.getId())).collect(Collectors.toList());

			for (Connection connection : connections) {
				Optional<Pair<Edge, Lane>> optLane = lanes.stream()
						.filter(e -> e.getRigth().getId().equals(connection.getVia())).findFirst();

				if (optLane.isPresent()) {
					Pair<Edge, Lane> lane = optLane.get();
					Optional<hu.mas.core.mas.model.graph.Edge> to = graph.findEdge(connection.getTo());
					if (to.isPresent()) {
						InternalEdge internalEdge = new InternalEdge(lane.getLeft().getId(), incomingEdge, to.get(),
								lane.getRigth().getSpeed(), lane.getRigth().getLength());
						internalJunction.getInternalEdges().add(internalEdge);
						graph.addInternalEdge(internalEdge);
					}
				}
			}
		}
	}

	protected static List<Edge> getValidEdges(Net net, List<String> allowList) {
		return net.getEdges().stream().filter(e -> e.getFrom() != null && e.getTo() != null)
				.filter(e -> isAllowed(e, allowList)).collect(Collectors.toList());
	}

	protected static double getSpeed(Edge edge) {
		return edge.getLanes().stream().map(Lane::getSpeed).min((a, b) -> a.compareTo(b)).orElse(0.0);
	}

	protected static double getLength(Edge edge) {
		return edge.getLanes().stream().map(Lane::getLength).max((a, b) -> a.compareTo(b)).orElse(0.0);
	}

	protected static boolean isAllowed(Edge edge, List<String> typeList) {
		return typeList.contains(edge.getType());
	}

	private Converter() {
	}

}
