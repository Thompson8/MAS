package hu.mas.core.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.message.LocationInfoAnswer;
import hu.mas.core.agent.message.LocationInfoRequest;
import hu.mas.core.agent.message.Message;
import hu.mas.core.agent.message.MessageType;
import hu.mas.core.agent.message.ReRouteAnswer;
import hu.mas.core.agent.message.ReRouteRequest;
import hu.mas.core.agent.message.RouteInfoAnswer;
import hu.mas.core.agent.message.RouteInfoRequest;
import hu.mas.core.agent.message.RouteSelectionRequest;
import hu.mas.core.agent.message.RouteSelectionAnswer;
import hu.mas.core.mas.MasController;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Node;
import hu.mas.core.util.Pair;

public class SimpleRePlanAgent extends SimpleAgent {

	private static final Logger logger = LogManager.getLogger();

	public SimpleRePlanAgent(Vehicle vehicle, Node from, Node to, MasController masController) {
		super(vehicle, from, to, masController);
	}

	public SimpleRePlanAgent(String id, Vehicle vehicle, Node from, Node to, MasController masController) {
		super(id, vehicle, from, to, masController);
	}

	@Override
	public void selectRoute(List<Pair<Double, List<Node>>> routes) {
		Optional<List<Node>> route = getOptimalRoute(routes);
		if (route.isPresent()) {
			this.route = new Route(route.get());
		}
	}

	public Optional<List<Node>> getOptimalRoute(List<Pair<Double, List<Node>>> routes) {
		Optional<Pair<Double, List<Node>>> route = routes.stream().min((a, b) -> a.getLeft().compareTo(b.getLeft()));
		if (route.isPresent()) {
			List<Node> nodes = route.get().getRigth();
			if (!nodes.contains(this.to)) {
				nodes.add(this.to);
			}
			return Optional.of(nodes);
		} else {
			return Optional.empty();
		}
	}

	public boolean selectReRoute(List<Pair<Double, List<Node>>> routes, Edge currentEdge) {
		Optional<List<Node>> route = getOptimalRoute(routes);
		if (route.isPresent()) {
			List<Node> nodes = route.get();
			List<Node> routeNodes = new ArrayList<>();
			routeNodes.add(currentEdge.getFrom());
			routeNodes.addAll(nodes);

			int index = this.route.getNodes().indexOf(routeNodes.get(0));
			if (index == -1 || (index == 0 ? !this.route.getNodes().equals(routeNodes)
					: !this.route.getNodes().subList(index - 1, this.route.getNodes().size() - 1).equals(routeNodes))) {
				this.route = new Route(routeNodes);

				return true;
			} else {
				return false;
			}
		} else {
			return false;
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
				Thread.sleep(rePlanIntervalTime);
				Message currentLocation = new Message(this.id, MessageType.LOCATION_INFO_REQUEST,
						new LocationInfoRequest(this.vehicle));
				masController.sendMessage(currentLocation);
				Message currentLocationAnswer = currentLocation.getConnection().take();
				Optional<Edge> currentEdge = ((LocationInfoAnswer) currentLocationAnswer.getBody()).getCurrentEdge();

				if (currentEdge.isPresent()) {
					logger.info("Agent: {} current edge: {}", this.id, currentEdge.get());

					Node reRouteFrom = selectNextStop(currentEdge.get());
					Message reRoteInfoRequest = new Message(this.id, MessageType.ROUTE_INFO_REQUEST,
							new RouteInfoRequest(reRouteFrom, to));
					masController.sendMessage(reRoteInfoRequest);

					Message reRoteInfoRequestMessageAnswer = reRoteInfoRequest.getConnection().take();
					RouteInfoAnswer reRoteInfoRequestMessageAnswerBody = (RouteInfoAnswer) reRoteInfoRequestMessageAnswer
							.getBody();

					logger.info("Agent: {} recived routes to choose from for re routing: {}", this.id,
							reRoteInfoRequestMessageAnswerBody.getRoute());
					if (selectReRoute(reRoteInfoRequestMessageAnswerBody.getRoute(), currentEdge.get())) {
						logger.info("Agent: {} chosen {} route for re routing", this.id, this.route);

						Message retRouteSelection = new Message(this.id, MessageType.RE_ROUTE_REQUEST,
								new ReRouteRequest(this.route, this.vehicle));
						masController.sendMessage(retRouteSelection);
						logger.info("Agent: {} sent chosen route to Mas for re routing", this.id);

						Message reRouteSelectionAnswer = retRouteSelection.getConnection().take();
						this.route.setId(((ReRouteAnswer) reRouteSelectionAnswer.getBody()).getRouteId());
						logger.info("Agent: {} started travelling on re route: {}", this.id, this.route);
					} else {
						logger.info("Agent: {} re route not needed", this.id);
					}
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

	protected Node selectNextStop(Edge edge) {
		return this.route.getNodes().stream().filter(e -> e.getIncomingEdges().contains(edge)).findFirst()
				.orElseThrow();
	}

	@Override
	public String toString() {
		return "SimpleRePlanAgent [id=" + id + ", vehicle=" + vehicle + ", from=" + from + ", to=" + to + ", route="
				+ route + ", masController=" + masController + ", sleepTime=" + sleepTime + ", rePlanIntervalTime="
				+ rePlanIntervalTime + "]";
	}

}
