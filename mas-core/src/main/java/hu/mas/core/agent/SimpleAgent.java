package hu.mas.core.agent;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.message.Message;
import hu.mas.core.agent.message.MessageType;
import hu.mas.core.agent.message.RouteInfoAnswer;
import hu.mas.core.agent.message.RouteInfoRequest;
import hu.mas.core.agent.message.RouteSelectionRequest;
import hu.mas.core.agent.message.RouteStartedRequest;
import hu.mas.core.mas.MasController;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Node;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public class SimpleAgent extends Agent {

	private static final Logger logger = LogManager.getLogger();

	public SimpleAgent(Vehicle vehicle, Node from, Node to, MasController masController,
			SumoTraciConnection connection) {
		super(vehicle, from, to, masController, connection);
	}

	public SimpleAgent(String id, Vehicle vehicle, Node from, Node to, MasController masController,
			SumoTraciConnection connection) {
		super(id, vehicle, from, to, masController, connection);
	}

	@Override
	public void selectRoute(List<Pair<Double, Pair<List<Node>, List<Edge>>>> routes) {
		Optional<Pair<Double, Pair<List<Node>, List<Edge>>>> route = routes.stream()
				.min((a, b) -> a.getLeft().compareTo(b.getLeft()));
		if (route.isPresent()) {
			List<Node> nodes = route.get().getRigth().getLeft();
			if (!nodes.contains(this.to)) {
				nodes.add(to);
			}
			this.route = new Route(nodes, route.get().getRigth().getRigth());
		}
	}

	@Override
	public void run() {
		try {
			logger.info("Agent: {} started", this.id);
			if (this.sleepTime != null && this.sleepTime > 0) {
				logger.info("Agent: {} will sleep for: {}ms", this.id, this.sleepTime);
				Thread.sleep(sleepTime);
			}

			Message infoRequest = new Message(this.id, MessageType.ROUTE_INFO_REQUEST, new RouteInfoRequest(from, to));
			masController.sendMessage(infoRequest);

			Message infoRequestMessageAnswer = infoRequest.getConnection().take();
			RouteInfoAnswer infoRequestMessageAnswerBody = (RouteInfoAnswer) infoRequestMessageAnswer.getBody();

			logger.info("Agent: {} recived routes to choose from: {}", this.id,
					infoRequestMessageAnswerBody.getRoute());
			selectRoute(infoRequestMessageAnswerBody.getRoute());
			logger.info("Agent: {} chosen {} route", this.id, this.route);

			Message routeSelection = new Message(this.id, MessageType.ROUTE_SELECTION_REQUEST,
					new RouteSelectionRequest(this.route, this.vehicle));
			masController.sendMessage(routeSelection);
			logger.info("Agent: {} sent chosen route to Mas", this.id);

			Message routeSelectionAnswer = routeSelection.getConnection().take();
			logger.info("Agent: {} route selection answer: {}", this.id, routeSelectionAnswer);

			Message routeStartedRequest = new Message(this.id, MessageType.ROUTE_STARTED_REQUEST,
					new RouteStartedRequest(this.vehicle));
			logger.info("Agent: {} sent route started signal", this.id);
			masController.sendMessage(routeStartedRequest);
			Message routeStartedAnswer = routeStartedRequest.getConnection().take();
			logger.info("Agent: {} recived acknowledgement signal: {}", this.id, routeStartedAnswer);

			startRoute();
			logger.info("Agent: {} sent request to SUMO to start route", this.id);

			Integer startIteration = null;
			while (startIteration == null) {
				if (Thread.currentThread().isInterrupted()) {
					logger.info("Agent: {} was interrupted will terminate further running", this.id);
					break;
				} else {
					Thread.sleep(LOCATION_POOL_INTERVAL_TIME);
				}
				startIteration = getStartIteration();
			}

			logger.info("Agent: {} started travelling on route: {}, iteration: {}", this.id, this.route,
					startIteration);

			Integer finishIteration = null;
			while (finishIteration == null) {
				if (Thread.currentThread().isInterrupted()) {
					logger.info("Agent: {} was interrupted will terminate further running", this.id);
					break;
				} else {
					Thread.sleep(LOCATION_POOL_INTERVAL_TIME);
				}

				finishIteration = getFinishIteration();

				if (finishIteration == null) {
					Optional<Edge> edge = getLocation();
					if (edge.isPresent()) {
						logger.info("Agent: {} is still executing it's route, location: {}", this.id,
								edge.get().getId());
					} else {
						logger.info("Agent: {} edge not found, must be temp sync error", this.id);
					}
				}
			}

			logger.info("Agent: {} finished it's route, iteration: {}", this.id, finishIteration);

		} catch (Exception e) {
			logger.error("Exception during agent execution", e);
			throw new AgentException(e);
		}
	}

	@Override
	public String toString() {
		return "SimpleAgent [id=" + id + ", vehicle=" + vehicle + ", from=" + from + ", to=" + to + ", route=" + route
				+ ", masController=" + masController + ", sleepTime=" + sleepTime + "]";
	}

}
