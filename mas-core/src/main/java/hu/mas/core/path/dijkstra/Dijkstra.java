package hu.mas.core.path.dijkstra;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;
import hu.mas.core.path.PathFinder;
import hu.mas.core.util.Pair;

public class Dijkstra implements PathFinder {

	public static DijkstraGraph calculateShortestPathFromSource(DijkstraGraph graph, DijkstraNode source) {
		source.setDistance(0.0);

		Set<DijkstraNode> settledNodes = new HashSet<>();
		Set<DijkstraNode> unsettledNodes = new HashSet<>();
		unsettledNodes.add(source);

		while (!unsettledNodes.isEmpty()) {
			DijkstraNode currentNode = getLowestDistanceNode(unsettledNodes);
			unsettledNodes.remove(currentNode);
			if (currentNode != null && currentNode.getAdjacentNodes() != null) {
				for (Entry<DijkstraNode, Double> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
					DijkstraNode adjacentNode = adjacencyPair.getKey();
					Double edgeWeigh = adjacencyPair.getValue();

					if (!settledNodes.contains(adjacentNode)) {
						calculateMinimumDistance(adjacentNode, edgeWeigh, currentNode);
						unsettledNodes.add(adjacentNode);
					}
				}
			}
			settledNodes.add(currentNode);
		}
		return graph;
	}

	private static void calculateMinimumDistance(DijkstraNode evaluationNode, Double edgeWeigh,
			DijkstraNode sourceNode) {
		Double sourceDistance = sourceNode.getDistance();
		if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
			evaluationNode.setDistance(sourceDistance + edgeWeigh);
			LinkedList<DijkstraNode> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
			shortestPath.add(sourceNode);
			evaluationNode.setShortestPath(shortestPath);
		}
	}

	private static DijkstraNode getLowestDistanceNode(Set<DijkstraNode> unsettledNodes) {
		DijkstraNode lowestDistanceNode = null;
		double lowestDistance = Double.MAX_VALUE;
		for (DijkstraNode node : unsettledNodes) {
			double nodeDistance = node.getDistance();
			if (nodeDistance < lowestDistance) {
				lowestDistance = nodeDistance;
				lowestDistanceNode = node;
			}
		}
		return lowestDistanceNode;
	}

	@Override
	public List<Pair<Double, Pair<List<Node>, List<Edge>>>> getShortestPaths(Graph graph, Node nodeFrom, Node nodeTo,
			double[][] travelWeigthMatrix, Edge[][] edgeMatrix) {
		Pair<Double, List<Node>> route = getShortestPathImp(graph, nodeFrom, travelWeigthMatrix, edgeMatrix)
				.get(nodeTo);
		if (!route.getRigth().contains(nodeTo)) {
			route.getRigth().add(nodeTo);
		}
		return Collections
				.singletonList(new Pair<>(route.getLeft(), new Pair<>(route.getRigth(), toEdges(route.getRigth()))));
	}

	public Map<Node, Pair<Double, List<Node>>> getShortestPathImp(Graph graph, Node nodeFrom,
			double[][] travelWeigthMatrix, Edge[][] edgeMatrix) {
		DijkstraGraph graphToSearch = new DijkstraGraph();
		graph.getNodes().stream().map(e -> new DijkstraNode(e.getId())).forEach(graphToSearch::addNode);

		graphToSearch.getNodes().stream().forEach(e -> {
			Optional<Node> node = graph.getNodes().stream().filter(a -> a.getId().equals(e.getName())).findFirst();
			if (node.isPresent()) {
				node.get().getIncomingEdges().stream().forEach(b -> {
					Optional<DijkstraNode> n = graphToSearch.getNodes().stream()
							.filter(c -> c.getName().equals(b.getFrom().getId())).findFirst();
					Pair<Integer, Integer> p = find(b, edgeMatrix);
					e.addDestination(n.get(), travelWeigthMatrix[p.getLeft()][p.getRigth()]);
				});
				node.get().getOutgoingEdges().stream().forEach(b -> {
					Optional<DijkstraNode> n = graphToSearch.getNodes().stream()
							.filter(c -> c.getName().equals(b.getTo().getId())).findFirst();
					Pair<Integer, Integer> p = find(b, edgeMatrix);
					e.addDestination(n.get(), travelWeigthMatrix[p.getLeft()][p.getRigth()]);
				});
			}
		});

		Optional<DijkstraNode> source = graphToSearch.getNodes().stream()
				.filter(e -> e.getName().equals(nodeFrom.getId())).findFirst();
		if (source.isPresent()) {
			DijkstraGraph resultGrap = Dijkstra.calculateShortestPathFromSource(graphToSearch, source.get());

			return resultGrap.getNodes().stream().collect(Collectors.toMap(
					e -> graph.getNodes().stream().filter(a -> a.getId().equals(e.getName())).findFirst().get(),
					e -> new Pair<>(e.getDistance(), e.getShortestPath().stream()
							.map(b -> graph.getNodes().stream().filter(a -> a.getId().equals(b.getName())).findFirst())
							.map(a -> a.orElse(null)).collect(Collectors.toList()))));
		} else {
			return new HashMap<>();
		}
	}

}
