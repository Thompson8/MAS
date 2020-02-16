package hu.mas.core.path.dijkstra;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class DijkstraNode {

    private String name;

    private List<DijkstraNode> shortestPath = new LinkedList<>();

    private Double distance = Double.MAX_VALUE;

    private Map<DijkstraNode, Double> adjacentNodes = new HashMap<>();

    public DijkstraNode(String name) {
        this.name = name;
    }

    public void addDestination(DijkstraNode destination, double distance) {
        adjacentNodes.put(destination, distance);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<DijkstraNode, Double> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(Map<DijkstraNode, Double> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public List<DijkstraNode> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(List<DijkstraNode> shortestPath) {
        this.shortestPath = shortestPath;
    }

	@Override
	public String toString() {
		return "DijkstraNode [name=" + name + ", shortestPath=" + shortestPath + ", distance=" + distance + ", adjacentNodes="
				+ adjacentNodes.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName(), Entry::getValue)) + "]";
	}

}
