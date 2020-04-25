package hu.mas.core.mas.model.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Junction {

	private final Vertex vertex;

	private final List<InternalEdge> internalEdges;

	public Junction(Vertex vertex) {
		this.vertex = vertex;
		this.internalEdges = new ArrayList<>();
		this.vertex.setJunction(this);
	}

	public Optional<InternalEdge> getInternalEdge(Edge from, Edge to) {
		return internalEdges.stream().filter(e -> e.getFrom().equals(from) && e.getTo().equals(to)).findFirst();
	}

	public Vertex getVertex() {
		return vertex;
	}

	public List<InternalEdge> getInternalEdges() {
		return internalEdges;
	}

	@Override
	public String toString() {
		return "Junction [vertex=" + vertex + ", internalEdges=" + internalEdges + "]";
	}

}
