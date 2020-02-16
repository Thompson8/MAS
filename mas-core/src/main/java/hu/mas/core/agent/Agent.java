package hu.mas.core.agent;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import hu.mas.core.mas.MasController;
import hu.mas.core.mas.model.Node;
import hu.mas.core.util.Pair;

public abstract class Agent implements Runnable {

	protected static final AtomicInteger SEQUENCE = new AtomicInteger(0);

	protected String id;

	protected Vehicle vehicle;

	protected Node from;

	protected Node to;

	protected Route route;

	protected MasController masController;

	protected Integer sleepTime;
	
	protected Integer rePlanIntervalTime;

	public Agent(Vehicle vehicle, Node from, Node to, MasController masController) {
		this(generateId(), vehicle, from, to, masController);
	}

	public Agent(String id, Vehicle vehicle, Node from, Node to, MasController masController) {
		this.id = id;
		this.vehicle = vehicle;
		this.from = from;
		this.to = to;
		this.masController = masController;
		this.sleepTime = null;
		this.rePlanIntervalTime = null;
	}

	public static String generateId() {
		return "Agent_" + SEQUENCE.getAndIncrement();
	}

	public abstract void selectRoute(List<Pair<Double, List<Node>>> routes);

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

	public Node getFrom() {
		return from;
	}

	public void setFrom(Node from) {
		this.from = from;
	}

	public Node getTo() {
		return to;
	}

	public void setTo(Node to) {
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

	public Integer getRePlanIntervalTime() {
		return rePlanIntervalTime;
	}

	public void setRePlanIntervalTime(Integer rePlanIntervalTime) {
		this.rePlanIntervalTime = rePlanIntervalTime;
	}

	@Override
	public String toString() {
		return "Agent [id=" + id + ", vehicle=" + vehicle + ", from=" + from + ", to=" + to + ", route=" + route
				+ ", masController=" + masController + ", sleepTime=" + sleepTime + ", rePlanIntervalTime="
				+ rePlanIntervalTime + "]";
	}

}
