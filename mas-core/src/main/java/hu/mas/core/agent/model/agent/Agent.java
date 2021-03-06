package hu.mas.core.agent.model.agent;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.model.exception.AgentException;
import hu.mas.core.agent.model.route.MasRoute;
import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.controller.MasController;
import hu.mas.core.mas.model.graph.AbstractEdge;
import hu.mas.core.mas.model.graph.Vertex;
import hu.mas.core.mas.model.message.Message;
import hu.mas.core.mas.model.message.MessageType;
import hu.mas.core.mas.model.message.RouteInfoAnswer;
import hu.mas.core.mas.model.message.RouteInfoRequest;
import hu.mas.core.mas.model.message.RouteSelectionRequest;
import hu.mas.core.mas.model.message.RouteStartedRequest;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public class Agent extends AbstractAgent {

	private static final Logger logger = LogManager.getLogger(Agent.class);

	public Agent(Vehicle vehicle, Vertex from, Vertex to, MasController masController, SumoTraciConnection connection) {
		super(vehicle, from, to, masController, connection);
	}

	public Agent(String id, Vehicle vehicle, Vertex from, Vertex to, MasController masController,
			SumoTraciConnection connection) {
		super(id, vehicle, from, to, masController, connection);
	}

	@Override
	public void selectRoute(List<Pair<Double, MasRoute>> routes) {
		Optional<Pair<Double, MasRoute>> route = routes.stream().min((a, b) -> a.getLeft().compareTo(b.getLeft()));
		if (route.isPresent()) {
			logger.info("Predicted time for route: {}", route.get().getLeft());
			this.route = route.get().getRigth();
		}
	}

	@Override
	public void run() {
		try {
			sleepIfNeeded();

			agentInitialMassCommunication();

			startRoute();
			logger.info("Agent: {} sent request to SUMO to start route", this.id);

			Double start = monitorStartTime();
			logger.info("Agent: {} started travelling on route: {}, start time: {}", this.id, this.route, start);

			Double finish = monitorFinishTime();
			logger.info("Agent: {} finished it's route, finish time: {}", this.id, finish);

			logger.info("Agent: {} total travel time: {}", this.id, finish - start);
		} catch (InterruptedException e) {
			// Ignore this error since it only means that the agent should be terminated
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			logger.error("Exception during agent execution", e);
			throw new AgentException(e);
		}
	}

	protected void sleepIfNeeded() throws InterruptedException {
		logger.info("Agent: {} started", this.id);
		if (this.sleepTime != null && this.sleepTime > 0) {
			logger.info("Agent: {} will sleep for: {}ms", this.id, this.sleepTime);
			Thread.sleep(sleepTime);
		}
	}

	protected void agentInitialMassCommunication() throws InterruptedException {
		Message infoRequest = new Message(this.id, MessageType.ROUTE_INFO_REQUEST,
				new RouteInfoRequest(this.from, this.to, this.vehicle));
		masController.sendMessage(infoRequest);

		Message infoRequestMessageAnswer = checkIfMasIsTerminated(infoRequest.getConnection().take());
		RouteInfoAnswer infoRequestMessageAnswerBody = (RouteInfoAnswer) infoRequestMessageAnswer.getBody();

		logger.info("Agent: {} recived routes to choose from: {}", this.id, infoRequestMessageAnswerBody.getRoutes());
		selectRoute(infoRequestMessageAnswerBody.getRoutes());
		logger.info("Agent: {} chosen {} route", this.id, this.route);

		Message routeSelection = new Message(this.id, MessageType.ROUTE_SELECTION_REQUEST,
				new RouteSelectionRequest(this.route, this.vehicle));
		masController.sendMessage(routeSelection);
		logger.info("Agent: {} sent chosen route to Mas", this.id);

		Message routeSelectionAnswer = checkIfMasIsTerminated(routeSelection.getConnection().take());
		logger.info("Agent: {} route selection answer: {}", this.id, routeSelectionAnswer);

		Message routeStartedRequest = new Message(this.id, MessageType.ROUTE_STARTED_REQUEST,
				new RouteStartedRequest(this.vehicle));
		logger.info("Agent: {} sent route started signal", this.id);
		masController.sendMessage(routeStartedRequest);
		Message routeStartedAnswer = checkIfMasIsTerminated(routeStartedRequest.getConnection().take());
		logger.info("Agent: {} recived acknowledgement signal: {}", this.id, routeStartedAnswer);
	}

	protected Double monitorStartTime() throws InterruptedException {
		Double start = null;
		while (start == null) {
			if (Thread.currentThread().isInterrupted()) {
				logger.info("Agent: {} was interrupted will terminate further running", this.id);
				break;
			} else {
				Thread.sleep(LOCATION_POOL_INTERVAL_TIME);
			}
			start = getStart();
		}

		return start;
	}

	protected Double monitorFinishTime() throws InterruptedException {
		Double finish = null;
		while (finish == null) {
			if (Thread.currentThread().isInterrupted()) {
				logger.info("Agent: {} was interrupted will terminate further running", this.id);
				break;
			} else {
				Thread.sleep(LOCATION_POOL_INTERVAL_TIME);
			}

			finish = getFinish();

			if (finish == null) {
				Optional<AbstractEdge> edge = getLocation();
				if (edge.isPresent()) {
					logger.info("Agent: {} is still executing it's route, location: {}", this.id, edge.get().getId());
				} else {
					logger.info("Agent: {} edge not found, must be temp sync error", this.id);
				}
			}
		}

		return finish;
	}

	protected Message checkIfMasIsTerminated(Message message) {
		if (MessageType.MAS_TERMINATED.equals(message.getType())) {
			throw new AgentException("Mas is terminated!");
		} else {
			return message;
		}
	}

	@Override
	public String toString() {
		return "SimpleAgent [id=" + id + ", vehicle=" + vehicle + ", from=" + from + ", to=" + to + ", route=" + route
				+ ", masController=" + masController + ", sleepTime=" + sleepTime + "]";
	}

}
