package hu.mas.core.agent.model.agent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import de.tudresden.ws.container.SumoStringList;
import hu.mas.core.agent.model.route.Route;
import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.MasController;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.Vertex;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public abstract class Agent implements Runnable {

	protected static final int LOCATION_POOL_INTERVAL_TIME = 1000;

	protected static final AtomicInteger SEQUENCE = new AtomicInteger(1);

	protected final SumoTraciConnection connection;

	protected String id;

	protected Vehicle vehicle;

	protected Vertex from;

	protected Vertex to;

	protected Route route;

	protected MasController masController;

	protected Integer sleepTime;

	protected Integer agentStartInterval;

	public Agent(Vehicle vehicle, Vertex from, Vertex to, MasController masController, SumoTraciConnection connection) {
		this(generateId(), vehicle, from, to, masController, connection);
	}

	public Agent(String id, Vehicle vehicle, Vertex from, Vertex to, MasController masController,
			SumoTraciConnection connection) {
		this.id = id;
		this.vehicle = vehicle;
		this.from = from;
		this.to = to;
		this.masController = masController;
		this.sleepTime = null;
		this.agentStartInterval = null;
		this.connection = connection;
	}

	public static String generateId() {
		return "Agent_" + SEQUENCE.getAndIncrement();
	}

	protected abstract void selectRoute(List<Pair<Double, Route>> routes);

	protected void startRoute() throws Exception {
		SumoStringList edges = new SumoStringList(
				route.getEdges().stream().map(Edge::getId).collect(Collectors.toList()));
		connection.do_job_set(de.tudresden.sumo.cmd.Route.add(route.getId(), edges));
		connection.do_job_set(de.tudresden.sumo.cmd.Vehicle.add(vehicle.getId(), vehicle.getTypeId(), route.getId(), 0,
				0.0, 1, Byte.valueOf("0")));
	}

	protected boolean isFinished() {
		return masController.isFinished(vehicle);
	}

	protected Double getFinish() {
		return masController.getFinish(vehicle);
	}

	protected boolean isStarted() {
		return masController.isStarted(vehicle);
	}

	protected Double getStart() {
		return masController.getStart(vehicle);
	}

	protected Optional<Edge> getLocation() {
		return masController.findCurrentLocation(vehicle);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Vertex getFrom() {
		return from;
	}

	public void setFrom(Vertex from) {
		this.from = from;
	}

	public Vertex getTo() {
		return to;
	}

	public void setTo(Vertex to) {
		this.to = to;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public Integer getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(Integer sleepTime) {
		this.sleepTime = sleepTime;
	}

	public MasController getMasController() {
		return masController;
	}

	public void setMasController(MasController masController) {
		this.masController = masController;
	}

	public Integer getAgentStartInterval() {
		return agentStartInterval;
	}

	public void setAgentStartInterval(Integer agentStartInterval) {
		this.agentStartInterval = agentStartInterval;
	}

	@Override
	public String toString() {
		return "Agent [id=" + id + ", vehicle=" + vehicle + ", from=" + from + ", to=" + to + ", route=" + route
				+ ", masController=" + masController + ", sleepTime=" + sleepTime + ", agentStartInterval="
				+ agentStartInterval + "]";
	}

}
