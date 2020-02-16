package hu.mas.core.path.dijkstra;

import java.util.HashSet;
import java.util.Set;

public class DijkstraGraph {

    private Set<DijkstraNode> nodes = new HashSet<>();
    
    public void addNode(DijkstraNode nodeA) {
        nodes.add(nodeA);
    }

    public Set<DijkstraNode> getNodes() {
        return nodes;
    }

    public void setNodes(Set<DijkstraNode> nodes) {
        this.nodes = nodes;
    }

	@Override
	public String toString() {
		return "DijkstraGraph [nodes=" + nodes + "]";
	}

}
