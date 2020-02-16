package hu.mas.core.mas.model;

import java.util.List;

public class Graph {

	private final List<Node> nodes;

	public Graph(List<Node> nodes) {
		if (nodes == null) {
			throw new IllegalArgumentException();
		}
		this.nodes = nodes;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	@Override
	public String toString() {
		return "Graph [nodes=" + nodes + "]";
	}

}
