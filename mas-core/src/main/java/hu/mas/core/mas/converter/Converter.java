package hu.mas.core.mas.converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import hu.mas.core.config.net.xml.model.Edge;
import hu.mas.core.config.net.xml.model.Lane;
import hu.mas.core.config.net.xml.model.Net;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.model.graph.MasGraphImpl;

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

		return graph;
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
