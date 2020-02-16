package hu.mas.core.agent;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.message.LocationInfoAnswer;
import hu.mas.core.agent.message.LocationInfoRequest;
import hu.mas.core.agent.message.Message;
import hu.mas.core.agent.message.MessageType;
import hu.mas.core.agent.message.RouteInfoAnswer;
import hu.mas.core.agent.message.RouteInfoRequest;
import hu.mas.core.agent.message.RouteSelectionRequest;
import hu.mas.core.agent.message.RouteSelectionAnswer;
import hu.mas.core.mas.MasController;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Node;
import hu.mas.core.util.Pair;

public class SimpleAgent extends Agent {

	private static final Logger logger = LogManager.getLogger();

	protected static final int ROUTE_INFO_POLL_INTERVAL = 1000;

	public SimpleAgent(Vehicle vehicle, Node from, Node to, MasController masController) {
		super(vehicle, from, to, masController);
	}

	public SimpleAgent(String id, Vehicle vehicle, Node from, Node to, MasController masController) {
		super(id, vehicle, from, to, masController);
	}

	@Override
	public void selectRoute(List<Pair<Double, List<Node>>> routes) {
		Optional<Pair<Double, List<Node>>> route = routes.stream().min((a, b) -> a.getLeft().compareTo(b.getLeft()));
		if (route.isPresent()) {
			List<Node> nodes = route.get().getRigth();
			if (!nodes.contains(this.to)) {
				nodes.add(to);
			}
			this.route = new Route(nodes);
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
			this.route.setId(((RouteSelectionAnswer) routeSelectionAnswer.getBody()).getRouteId());
			logger.info("Agent: {} started travelling on route: {}", this.id, this.route);

			boolean finished = false;
			while (!finished) {
				Thread.sleep(ROUTE_INFO_POLL_INTERVAL);
				Message currentLocation = new Message(this.id, MessageType.LOCATION_INFO_REQUEST,
						new LocationInfoRequest(this.vehicle));
				masController.sendMessage(currentLocation);
				Message currentLocationAnswer = currentLocation.getConnection().take();
				Optional<Edge> currentEdge = ((LocationInfoAnswer) currentLocationAnswer.getBody()).getCurrentEdge();
				
				if (currentEdge.isPresent()) {
					logger.info("Agent: {} current edge: {}", this.id, currentEdge.get());
				} else {
					logger.info("Agent: {} finished it's destination", this.id);
					finished = true;
				}
				
				if (Thread.interrupted()) {
					logger.warn("Agent: {} thread was interrupted, will terminate further processing!", this.id);
					break;
				}
			}
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
