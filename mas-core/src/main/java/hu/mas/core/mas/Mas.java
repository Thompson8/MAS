package hu.mas.core.mas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.tudresden.ws.container.SumoStringList;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.agent.VehicleStatistics;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;
import hu.mas.core.path.PathFinder;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public class Mas {

	private final Graph graph;

	private final SumoTraciConnection connection;

	private final double[][] travelWeigthMatrix;

	private final Edge[][] edgeMatrix;

	private final PathFinder pathFinder;

	private final Map<Vehicle, VehicleStatistics> trackedVehicles;

	public Mas(Graph graph, SumoTraciConnection connection, PathFinder pathFinder) {
		if (graph == null || connection == null || pathFinder == null) {
			throw new IllegalArgumentException();
		}
		this.graph = graph;
		this.connection = connection;
		this.travelWeigthMatrix = new double[this.graph.getNodes().size()][this.graph.getNodes().size()];
		this.edgeMatrix = new Edge[this.graph.getNodes().size()][this.graph.getNodes().size()];
		this.pathFinder = pathFinder;
		this.trackedVehicles = new HashMap<>();
		init();
	}

	protected void init() {
		for (int i = 0; i < travelWeigthMatrix.length; i++) {
			for (int j = 0; j < travelWeigthMatrix[i].length; j++) {
				if (i != j) {
					Node from = graph.getNodes().get(i);
					Node to = graph.getNodes().get(j);
					Optional<Edge> edge = from.getOutgoingEdges().stream()
							.filter(e -> to.getIncomingEdges().contains(e)).findFirst();
					if (edge.isPresent()) {
						travelWeigthMatrix[i][j] = 1;
						edgeMatrix[i][j] = edge.get();
					} else {
						travelWeigthMatrix[i][j] = 0;
					}
				}
			}
		}
	}

	protected void updateTravelWeigthMatrix() throws Exception {
		for (int i = 0; i < edgeMatrix.length; i++) {
			for (int j = 0; j < edgeMatrix[i].length; j++) {
				Edge edge = edgeMatrix[i][j];
				if (edge != null) {
					travelWeigthMatrix[i][j] = (Double) connection
							.do_job_get(de.tudresden.sumo.cmd.Edge.getTraveltime(edge.getId()));
				}
			}
		}
	}

	protected void updateTrackedVehicles(int iteration) throws Exception {
		SumoStringList vehicles = (SumoStringList) connection.do_job_get(de.tudresden.sumo.cmd.Vehicle.getIDList());
		for (Map.Entry<Vehicle, VehicleStatistics> e : trackedVehicles.entrySet()) {
			if (e.getValue().getFinish() == null
					&& vehicles.stream().filter(a -> e.getKey().getId().equals(a)).findFirst().isEmpty()) {
				e.getValue().setFinish(iteration);
			}
		}
	}

	public List<Pair<Double, List<Node>>> getShortestPath(Node nodeFrom, Node nodeTo) {
		return pathFinder.getShortestPaths(graph, nodeFrom, nodeTo, travelWeigthMatrix, edgeMatrix);
	}

	public Optional<Node> findNode(String id) {
		return graph.getNodes().stream().filter(e -> e.getId().equals(id)).findFirst();
	}

	public List<Edge> getEdgesToUser(List<Node> route) {
		List<Edge> edgesToUse = new ArrayList<>();
		for (int j = 0; j < (route.size() - 1); j++) {
			Node from = route.get(j);
			Node to = route.get(j + 1);
			Optional<Edge> edge = from.getOutgoingEdges().stream().filter(e -> e.getTo().equals(to)).findFirst();
			if (edge.isPresent()) {
				edgesToUse.add(edge.get());
			}
		}
		return edgesToUse;
	}

	public String createRoute(String routeId, List<Node> route, Vehicle vehicle, int iteration) throws Exception {
		List<Edge> edgesToUse = getEdgesToUser(route);

		SumoStringList edges = new SumoStringList(edgesToUse.stream().map(Edge::getId).collect(Collectors.toList()));
		connection.do_job_set(de.tudresden.sumo.cmd.Route.add(routeId, edges));
		connection.do_job_set(de.tudresden.sumo.cmd.Vehicle.add(vehicle.getId(), vehicle.getTypeId(), routeId, 0, 0.0,
				vehicle.getSpeed(), Byte.valueOf("0")));

		trackedVehicles.put(vehicle, new VehicleStatistics(iteration));

		return routeId;
	}

	public String createReRoute(String routeId, List<Node> route, Vehicle vehicle) throws Exception {
		List<Edge> edgesToUse = getEdgesToUser(route);

		SumoStringList edges = new SumoStringList(edgesToUse.stream().map(Edge::getId).collect(Collectors.toList()));
		connection.do_job_set(de.tudresden.sumo.cmd.Route.add(routeId, edges));
		connection.do_job_set(de.tudresden.sumo.cmd.Vehicle.setRouteID(vehicle.getId(), routeId));

		return routeId;
	}

	public Optional<Edge> getCurrentEdgeByVehicle(Vehicle vehicle) throws Exception {
		VehicleStatistics vehicleStatistics = trackedVehicles.get(vehicle);
		if (vehicleStatistics != null && vehicleStatistics.getFinish() == null) {
			String edgeId = (String) connection.do_job_get(de.tudresden.sumo.cmd.Vehicle.getRoadID(vehicle.getId()));
			return findEdge(edgeId);
		} else {
			return Optional.empty();
		}
	}

	public Optional<Edge> findEdge(String edgeId) {
		for (int i = 0; i < edgeMatrix.length; i++) {
			for (int j = 0; j < edgeMatrix[i].length; j++) {
				if (edgeMatrix[i][j] != null && edgeMatrix[i][j].getId().equals(edgeId)) {
					return Optional.of(edgeMatrix[i][j]);
				}
			}
		}
		return Optional.empty();
	}

	public void updateData(int iteration) throws Exception {
		updateTravelWeigthMatrix();
		updateTrackedVehicles(iteration);
	}

	public void doTimeStep() throws Exception {
		connection.do_timestep();
	}

	public String getTravelWeigthMatrixAsString() {
		return Arrays.deepToString(travelWeigthMatrix);
	}

	public String getEdgeMatrixAsString() {
		return Arrays.deepToString(edgeMatrix);
	}

	public void runServer() throws IOException {
		connection.runServer();
	}

	public void close() {
		connection.close();
	}

	public Map<Vehicle, VehicleStatistics> getTrackedVehicles() {
		return trackedVehicles;
	}

}
