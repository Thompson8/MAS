package hu.mas.core.mas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudresden.ws.container.SumoStringList;
import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;
import hu.mas.core.path.PathFinder;
import hu.mas.core.simulation.SimulationEdgeImpactCalculatorType;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public abstract class AbstractMas {

	private static final Logger logger = LogManager.getLogger();

	protected final Graph graph;

	protected final SimulationEdgeImpactCalculatorType edgeWeightCalculator;

	protected final SumoTraciConnection connection;

	protected final double[][] travelWeigthMatrix;

	protected final Edge[][] edgeMatrix;

	protected final VehiclesData vehiclesData;

	protected final PathFinder pathFinder;

	protected final Map<Integer, List<double[][]>> historicalTravelWeigthMatrix;

	protected Double maxLength = null;

	public AbstractMas(Graph graph, SumoTraciConnection connection, PathFinder pathFinder,
			SimulationEdgeImpactCalculatorType edgeWeightCalculator) {
		if (graph == null || connection == null || pathFinder == null) {
			throw new IllegalArgumentException();
		}
		this.graph = graph;
		this.edgeWeightCalculator = edgeWeightCalculator;
		this.connection = connection;
		this.travelWeigthMatrix = new double[this.graph.getNodes().size()][this.graph.getNodes().size()];
		this.edgeMatrix = new Edge[this.graph.getNodes().size()][this.graph.getNodes().size()];
		this.vehiclesData = new VehiclesData();
		this.pathFinder = pathFinder;
		this.historicalTravelWeigthMatrix = new HashMap<>();
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
						Edge foundEdge = edge.get();
						travelWeigthMatrix[i][j] = getEdgeWeigth(foundEdge);
						edgeMatrix[i][j] = foundEdge;
					} else {
						travelWeigthMatrix[i][j] = 0.0;
					}
				}
			}
		}
	}

	protected void updateTravelWeigthMatrix(int iteration) throws Exception {
		for (int i = 0; i < edgeMatrix.length; i++) {
			for (int j = 0; j < edgeMatrix[i].length; j++) {
				Edge edge = edgeMatrix[i][j];
				if (edge != null) {
					travelWeigthMatrix[i][j] = getValueForTravelWeigthMatrixUpdate(i, j, edge, iteration);
				}
			}
		}
	}

	protected abstract double getValueForTravelWeigthMatrixUpdate(int x, int y, Edge edge, int iteration)
			throws Exception;

	public List<Pair<Double, Pair<List<Node>, List<Edge>>>> getShortestPath(Node nodeFrom, Node nodeTo) {
		return pathFinder.getShortestPaths(graph, nodeFrom, nodeTo, travelWeigthMatrix, edgeMatrix);
	}

	public void updateData(int iteration) throws Exception {
		updateVehicleData(iteration);
		updateTravelWeigthMatrix(iteration);
		updateHistoricalTravelWeigthMatrix(iteration);
	}

	protected void updateHistoricalTravelWeigthMatrix(int iteration) {
		double[][] copy = new double[travelWeigthMatrix.length][travelWeigthMatrix[0].length];
		for (int i = 0; i < copy.length; i++) {
			copy[i] = Arrays.copyOf(travelWeigthMatrix[i], travelWeigthMatrix[i].length);
		}

		List<double[][]> list = this.historicalTravelWeigthMatrix.get(iteration);
		if (list == null) {
			list = new ArrayList<>();
			list.add(copy);
			this.historicalTravelWeigthMatrix.put(iteration, list);
		} else {
			list.add(copy);
		}
	}

	protected void updateVehicleData(int iteration) throws Exception {
		SumoStringList vehicles = (SumoStringList) connection.do_job_get(de.tudresden.sumo.cmd.Vehicle.getIDList());
		vehiclesData.getData().entrySet().stream().forEach(e -> {
			Optional<String> contains = vehicles.stream().filter(a -> e.getKey().getId().equals(a)).findFirst();
			if (e.getValue().getStatistics().getStart() != null && e.getValue().getStatistics().getFinish() == null
					&& contains.isEmpty()) {
				logger.info("Vehicle: {} finished it's route, finish: {}", e.getKey().getId(), iteration);
				e.getValue().getStatistics().setFinish(iteration);
			}

			if (e.getValue().getStatistics().getStart() == null && contains.isPresent()) {
				registerActualVehicleStart(e, iteration);
			}

			if (e.getValue().getStatistics().getStart() != null && e.getValue().getStatistics().getFinish() == null) {
				try {
					String edgeId = (String) connection
							.do_job_get(de.tudresden.sumo.cmd.Vehicle.getRoadID(e.getKey().getId()));
					Optional<Edge> edge = findEdge(edgeId);
					if (edge.isPresent()) {
						logger.trace("Vehicle: {} current edge is: {}", e.getKey().getId(), edgeId);
						e.getValue().setCurrentEdge(edge.get());
					} else {
						logger.trace("Unknown edge id: {} for vehicle: {}, must be a junction", edgeId,
								e.getKey().getId());
					}
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}

		});
	}

	protected Double getEdgeWeigth(Edge edge) {
		Double result = null;
		switch (edgeWeightCalculator) {
		case CONSTANT:
			result = 1.0;
			break;
		case TRAVEL_TIME:
			result = edge.getLength() / edge.getSpeed();
			break;
		default:
			break;
		}
		return result;
	}

	public void registerActualVehicleStart(Entry<Vehicle, VehicleData> data, int iteration) {
		data.getValue().getStatistics().setStart(iteration);
		data.getValue().setCurrentEdge(data.getValue().getRoute().getEdges().get(0));
	}

	public void registerVehicleStart(Vehicle vehicle, int iteration) {
		VehicleData entry = vehiclesData.get(vehicle);
		entry.getStatistics().setAgentStartMessage(iteration);
	}

	public void registerRoute(Vehicle vehicle, Route route) {
		VehicleData data = new VehicleData();
		data.setRoute(route);
		data.setCurrentEdge(route.getEdges().get(0));
		vehiclesData.put(vehicle, data);
	}

	public void registerReRoute(Vehicle vehicle, Route route) {
		vehiclesData.get(vehicle).setRoute(route);
	}

	protected Optional<Node> findNode(String id) {
		return graph.getNodes().stream().filter(e -> e.getId().equals(id)).findFirst();
	}

	protected Optional<Edge> findEdge(String edgeId) {
		for (int i = 0; i < edgeMatrix.length; i++) {
			for (int j = 0; j < edgeMatrix[i].length; j++) {
				if (edgeMatrix[i][j] != null && edgeMatrix[i][j].getId().equals(edgeId)) {
					return Optional.of(edgeMatrix[i][j]);
				}
			}
		}
		return Optional.empty();
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

	public VehiclesData getVehiclesData() {
		return vehiclesData;
	}

	public Map<Integer, List<double[][]>> getHistoricalTravelWeigthMatrix() {
		return historicalTravelWeigthMatrix;
	}

}
