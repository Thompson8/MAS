package hu.mas.core.mas;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudresden.ws.container.SumoStringList;
import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.mas.model.Vertex;
import hu.mas.core.path.AbstractPathFinder;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public abstract class AbstractMas {

	private static final Logger logger = LogManager.getLogger();

	protected final SumoTraciConnection connection;

	protected final MasGraph graph;

	protected final VehiclesData vehiclesData;

	protected final AbstractPathFinder pathFinder;

	public AbstractMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder) {
		if (graph == null || connection == null || pathFinder == null) {
			throw new IllegalArgumentException();
		}
		this.graph = graph;
		this.connection = connection;
		this.vehiclesData = new VehiclesData();
		this.pathFinder = pathFinder;
	}

	protected void updateTravelWeigthMatrix(double currentTime) throws Exception {
		for (Edge edge : graph.getEdges()) {
			graph.updateEdgeWeight(edge, getValueForWeigthUpdate(edge, currentTime));
		}
	}

	protected abstract double getValueForWeigthUpdate(Edge edge, double currentTime) throws Exception;

	public List<Pair<Double, Route>> getShortestPath(String from, String to, Vehicle vehicle, double currentTime) {
		return pathFinder.getShortestPaths(from, to, vehicle, currentTime, graph, this::calculateTravelTimeForEdges);
	}

	public List<Pair<Double, Route>> getShortestPath(Vertex from, Vertex to, Vehicle vehicle, double currentTime) {
		return pathFinder.getShortestPaths(from, to, vehicle, currentTime, graph, this::calculateTravelTimeForEdges);
	}

	protected abstract Map<Edge, Pair<Double, Double>> calculateTravelTimeForEdges(List<Edge> edges, Vehicle vehicle,
			double currentTime);

	public void updateData(double currentTime) throws Exception {
		updateVehicleData(currentTime);
		updateTravelWeigthMatrix(currentTime);
	}

	protected void updateVehicleData(double currentTime) throws Exception {
		SumoStringList vehicles = (SumoStringList) connection.do_job_get(de.tudresden.sumo.cmd.Vehicle.getIDList());
		vehiclesData.getData().entrySet().stream().filter(e -> e.getValue().getStatistics().getActualFinish() == null)
				.forEach(e -> {
					try {
						handleVehicleDataUpdate(e, vehicles, currentTime);
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
				});
	}

	public void handleVehicleDataUpdate(Entry<Vehicle, VehicleData> vehicleData, SumoStringList vehicles,
			double currentTime) throws Exception {
		Optional<String> contains = vehicles.stream().filter(e -> vehicleData.getKey().getId().equals(e)).findFirst();

		if (contains.isPresent()) {
			if (vehicleData.getValue().getStatistics().getActualStart() == null) {
				registerActualVehicleStart(vehicleData, currentTime);
			}
			String edgeId = (String) connection
					.do_job_get(de.tudresden.sumo.cmd.Vehicle.getRoadID(vehicleData.getKey().getId()));
			Optional<Edge> edge = graph.findEdge(edgeId);

			if (edge.isPresent()) {
				logger.trace("Vehicle: {} current edge is: {}", vehicleData.getKey().getId(), edgeId);
				vehicleData.getValue().setCurrentEdge(edge.get());
			} else {
				logger.trace("Unknown edge id: {} for vehicle: {}, must be a junction", edgeId,
						vehicleData.getKey().getId());
			}
		} else if (vehicleData.getValue().getStatistics().getActualStart() != null) {
			logger.info("Vehicle: {} finished it's route, finish: {}", vehicleData.getKey().getId(), currentTime);
			vehicleData.getValue().getStatistics().setActualFinish(currentTime);
		}
	}

	public void registerActualVehicleStart(Entry<Vehicle, VehicleData> data, double currentTime) {
		data.getValue().getStatistics().setActualStart(currentTime);
		data.getValue().setCurrentEdge(data.getValue().getRoute().getEdges().get(0));
	}

	public void registerVehicleStart(Vehicle vehicle, double currentTime) {
		VehicleData entry = vehiclesData.get(vehicle);
		entry.getStatistics().setAgentStart(currentTime);
	}

	public void registerRoute(Vehicle vehicle, Route route) {
		VehicleData data = new VehicleData();
		data.setRoute(route);
		data.setCurrentEdge(route.getEdges().get(0));
		vehiclesData.put(vehicle, data);
		registerRouteOperations(vehicle, route);
	}

	public List<Vehicle> vehiclesCurrentlyOnEdge(Edge edge) {
		return currentyOnEdgeVehicleData(edge).map(Map.Entry::getKey).collect(Collectors.toList());
	}

	protected List<Map.Entry<Vehicle, VehicleData>> vehicleDataCurrentlyOnEdge(Edge edge) {
		return currentyOnEdgeVehicleData(edge).collect(Collectors.toList());
	}

	protected Stream<Map.Entry<Vehicle, VehicleData>> currentyOnEdgeVehicleData(Edge edge) {
		return vehiclesData.getData().entrySet().stream().filter(e -> edge.equals(e.getValue().getCurrentEdge()));
	}

	protected abstract void registerRouteOperations(Vehicle vehicle, Route route);

	public void doTimeStep() throws Exception {
		connection.do_timestep();
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

	public Optional<Edge> get(String id) {
		return graph.findEdge(id);
	}

}
