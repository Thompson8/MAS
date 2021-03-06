package hu.mas.core.mas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudresden.ws.container.SumoStringList;
import hu.mas.core.agent.model.route.MasRoute;
import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.exception.MasException;
import hu.mas.core.mas.model.exception.MasRuntimeException;
import hu.mas.core.mas.model.graph.AbstractEdge;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.InternalEdge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.model.graph.Vertex;
import hu.mas.core.mas.model.vehicle.VehicleData;
import hu.mas.core.mas.model.vehicle.VehiclesData;
import hu.mas.core.mas.pathfinder.AbstractPathFinder;
import hu.mas.core.util.MutablePair;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public abstract class AbstractMas {

	private static final Logger logger = LogManager.getLogger(AbstractMas.class);

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

	protected abstract double calculateTravelTime(MasRoute route, Vehicle vehicle, double time);

	protected abstract double getValueForWeigthUpdate(Edge edge, double currentTime) throws MasException;

	protected void updateTravelWeigthMatrix(double previousTime, double currentTime) throws MasException {
		beforeUpdateTravelWeigthMatrix(previousTime, currentTime);
		for (Edge edge : graph.getEdges()) {
			graph.updateEdgeWeight(edge, getValueForWeigthUpdate(edge, currentTime));
		}
	}

	protected void beforeUpdateTravelWeigthMatrix(double previousTime, double currentTime) {
	}

	public List<Pair<Double, MasRoute>> getShortestPath(String from, String to, Vehicle vehicle, double currentTime) {
		return pathFinder.getShortestPaths(from, to, vehicle, currentTime, graph, this::calculateTravelTime);
	}

	public List<Pair<Double, MasRoute>> getShortestPath(Vertex from, Vertex to, Vehicle vehicle, double currentTime) {
		return pathFinder.getShortestPaths(from, to, vehicle, currentTime, graph, this::calculateTravelTime);
	}

	public void updateData(double previousTime, double currentTime) throws Exception {
		updateVehicleData(currentTime);
		updateTravelWeigthMatrix(previousTime, currentTime);
	}

	protected void updateVehicleData(double currentTime) throws Exception {
		SumoStringList vehicles = (SumoStringList) connection.do_job_get(de.tudresden.sumo.cmd.Vehicle.getIDList());
		vehiclesData.getData().entrySet().stream().filter(e -> e.getValue().getStatistics().getActualFinish() == null)
				.forEach(e -> handleVehicleDataUpdate(e, vehicles, currentTime));
	}

	protected void handleVehicleDataUpdate(Entry<Vehicle, VehicleData> vehicleData, SumoStringList vehicles,
			double currentTime) {
		try {
			Optional<String> contains = vehicles.stream().filter(e -> vehicleData.getKey().getId().equals(e))
					.findFirst();

			if (contains.isPresent()) {
				if (vehicleData.getValue().getStatistics().getActualStart() == null) {
					registerActualVehicleStart(vehicleData, currentTime);
				}
				String edgeId = (String) connection
						.do_job_get(de.tudresden.sumo.cmd.Vehicle.getRoadID(vehicleData.getKey().getId()));

				handleVehicleEdgeUpdate(vehicleData, edgeId, currentTime);

				vehicleData.getValue().setCurrentSpeed((Double) connection
						.do_job_get(de.tudresden.sumo.cmd.Vehicle.getSpeed(vehicleData.getKey().getId())));
			} else if (vehicleData.getValue().getStatistics().getActualStart() != null) {
				logger.info("Vehicle: {} finished it's route, finish: {}", vehicleData.getKey().getId(), currentTime);
				vehicleData.getValue().getStatistics().setActualFinish(currentTime);
			}
		} catch (Exception e) {
			logger.error("Exception during vehicle date update", e);
			throw new MasRuntimeException(e);
		}
	}

	protected void handleVehicleEdgeUpdate(Entry<Vehicle, VehicleData> vehicleData, String edgeId, Double time) {
		AbstractEdge currentEdge = vehicleData.getValue().getCurrentEdge();
		Optional<Edge> optEdge = graph.findEdge(edgeId);
		logger.trace("Vehicle: {} current edge is: {}", vehicleData.getKey().getId(), edgeId);

		if (optEdge.isPresent()) {
			handleVehicleEdgeDataUpdate(vehicleData, optEdge.get(), currentEdge, time);
		} else {
			Optional<InternalEdge> internalEdge = graph.findInternalEdge(edgeId);
			if (internalEdge.isPresent()) {
				handleVehicleEdgeDataUpdate(vehicleData, internalEdge.get(), currentEdge, time);
			} else {
				logger.warn("Unknown edge id: {} for vehicle: {}, must be a junction", edgeId,
						vehicleData.getKey().getId());
			}
		}
	}

	protected void handleVehicleEdgeDataUpdate(Entry<Vehicle, VehicleData> vehicleData, AbstractEdge edge,
			AbstractEdge currentEdge, Double time) {
		if (currentEdge == null) {
			edge.getRoad().getVehiclesOnRoad().add(new Pair<>(vehicleData.getKey(), new MutablePair<>(time, null)));
			vehicleData.getValue().setCurrentEdge(edge);
		} else if (!currentEdge.equals(edge)) {
			Optional<MutablePair<Double, Double>> storedOnRoadData = currentEdge.getRoad().getVehiclesOnRoad().stream()
					.filter(e -> e.getLeft().equals(vehicleData.getKey())).findFirst().map(Pair::getRigth);

			if (storedOnRoadData.isPresent()) {
				storedOnRoadData.get().setRigth(time);
			} else {
				logger.warn("Stored on road data is missing! {}", currentEdge.getId());
			}

			edge.getRoad().getVehiclesOnRoad().add(new Pair<>(vehicleData.getKey(), new MutablePair<>(time, null)));
			vehicleData.getValue().setCurrentEdge(edge);
		}
	}

	public void registerActualVehicleStart(Entry<Vehicle, VehicleData> data, double currentTime) {
		data.getValue().getStatistics().setActualStart(currentTime);
	}

	public void registerVehicleStart(Vehicle vehicle, double currentTime) {
		VehicleData entry = vehiclesData.get(vehicle);
		entry.getStatistics().setAgentStart(currentTime);
	}

	public void registerRoute(Vehicle vehicle, MasRoute route, double currentTime) {
		VehicleData data = new VehicleData();
		data.setRoute(route);
		vehiclesData.put(vehicle, data);
		registerRouteOperations(vehicle, route, currentTime);
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

	protected abstract void registerRouteOperations(Vehicle vehicle, MasRoute route, double currentTime);

	protected List<Double> getVehiclesSpeedsCurrentlyOnEdge(Edge edge) {
		return vehiclesData.getData().values().stream()
				.filter(e -> e.getStatistics().getActualStart() != null && e.getStatistics().getActualFinish() == null)
				.filter(e -> e.getCurrentEdge() != null && e.getCurrentEdge().equals(edge))
				.map(VehicleData::getCurrentSpeed).collect(Collectors.toList());
	}

	protected List<AbstractEdge> getEdgesWihtInternalEdgesIncluded(MasRoute route) {
		List<AbstractEdge> result = new ArrayList<>();

		Iterator<Edge> iterator = route.getEdges().iterator();
		Edge current = iterator.next();
		Edge next = null;
		while (iterator.hasNext()) {
			result.add(current);
			next = iterator.next();

			if (current.getTo().isJunction()) {
				Optional<InternalEdge> internalEdge = current.getTo().getJunction().getInternalEdge(current, next);
				if (internalEdge.isPresent()) {
					result.add(internalEdge.get());
				}
			}

			current = next;
		}

		result.add(current);

		return result;
	}

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
