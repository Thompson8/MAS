package hu.mas.core.mas.converter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hu.mas.core.config.net.xml.model.Lane;
import hu.mas.core.config.net.xml.model.Net;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;

public class Converter {

	public static Graph fromNetToGraph(Net net) {
		List<Node> nodes = net.getJunctions().stream().map(e -> new Node(e.getId(), e.getType()))
				.collect(Collectors.toList());

		net.getEdges().stream().filter(e -> e.getFrom() != null && e.getTo() != null)
				.filter(e -> e.getFunction() == null).forEach(e -> {
					Optional<Node> from = nodes.stream().filter(n -> n.getId().equals(e.getFrom())).findFirst();
					Optional<Node> to = nodes.stream().filter(n -> n.getId().equals(e.getTo())).findFirst();
					Double speed = e.getLanes().stream().map(Lane::getSpeed).min((a, b) -> a.compareTo(b))
							.orElseThrow();
					Double length = e.getLanes().stream().map(Lane::getLength).max((a, b) -> a.compareTo(b))
							.orElseThrow();

					Edge edge = new Edge(e.getId(), from.get(), to.get(), speed, length);
					from.get().getEdges().add(edge);
					to.get().getEdges().add(edge);
				});

		return new Graph(nodes);
	}

	private Converter() {
	}

}
