package hu.mas.core.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.message.Message;
import hu.mas.core.agent.message.MessageType;
import hu.mas.core.agent.message.ReRouteStartedRequest;
import hu.mas.core.agent.message.RouteInfoAnswer;
import hu.mas.core.agent.message.RouteInfoRequest;
import hu.mas.core.agent.message.RouteSelectionRequest;
import hu.mas.core.agent.message.RouteStartedRequest;
import hu.mas.core.mas.MasController;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Node;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public class SimpleRePlanAgent extends RePlanAgent {

	private static final Logger logger = LogManager.getLogger();

	public SimpleRePlanAgent(Vehicle vehicle, Node from, Node to, MasController masController,
			SumoTraciConnection connection) {
		super(vehicle, from, to, masController, connection);
	}

	public SimpleRePlanAgent(String id, Vehicle vehicle, Node from, Node to, MasController masController,
			SumoTraciConnection connection) {
		super(id, vehicle, from, to, masController, connection);
	}

	@Override
	public void selectRoute(List<Pair<Double, Pair<List<Node>, List<Edge>>>> routes) {
		Optional<Pair<Double, Pair<List<Node>, List<Edge>>>> route = getOptimalRoute(routes);
		if (route.isPresent()) {
			List<Node> nodes = route.get().getRigth().getLeft();
			if (!nodes.contains(this.to)) {
				nodes.add(to);
			}
			this.route = new Route(nodes, route.get().getRigth().getRigth());
		}
	}

	protected Optional<Pair<Double, Pair<List<Node>, List<Edge>>>> getOptimalRoute(
			List<Pair<Double, Pair<List<Node>, List<Edge>>>> routes) {
		return routes.stream().min((a, b) -> a.getLeft().compareTo(b.getLeft()));
	}

	@Override
	public boolean selectReRoute(List<Pair<Double, Pair<List<Node>, List<Edge>>>> routes, Edge location) {
		Optional<Pair<Double, Pair<List<Node>, List<Edge>>>> route = getOptimalRoute(routes);
		if (route.isPresent()) {
			Optional<Node> startNodeOpt = this.route.getNodes().stream()
					.filter(e -> e.getOutgoingEdges().contains(location)).findFirst();
			if (startNodeOpt.isPresent()) {
				Node startNode = startNodeOpt.get();
				List<Node> nodes = route.get().getRigth().getLeft();
				if (!nodes.contains(this.to)) {
					nodes.add(to);
				}

				if (nodes.get(0).equals(startNode)) {
					List<Node> tmp = new ArrayList<>();
					tmp.add(startNode);
					tmp.addAll(nodes);
					nodes = tmp;
				}

				List<Edge> edges = route.get().getRigth().getRigth();
				if (!edges.get(0).equals(location)) {
					List<Edge> tmp = new ArrayList<>();
					tmp.add(location);
					tmp.addAll(edges);
					edges = tmp;
				}

				int index = this.route.getEdges().indexOf(location);
				List<Edge> subEdges = this.route.getEdges().subList(index, this.route.getEdges().size());
				if (!subEdges.equals(edges)) {
					logger.info("Agent: {} old route: {}, , will be changed", this.id, this.route);
					this.route = new Route(nodes, edges);
					return true;
				} else {
					logger.info("Agent: {} old and new edges match so no re route will take place", this.id);
					return false;
				}
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
			logger.info("Agent: {} route selection answer: {}", this.id, routeSelectionAnswer);

			Message routeStartedRequest = new Message(this.id, MessageType.ROUTE_STARTED_REQUEST,
					new RouteStartedRequest(this.vehicle));
			logger.info("Agent: {} sent route started signal", this.id);
			masController.sendMessage(routeStartedRequest);
			Message routeStartedAnswer = routeStartedRequest.getConnection().take();
			logger.info("Agent: {} recived acknowledgement signal: {}", this.id, routeStartedAnswer);

			startRoute();
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
					Thread.sleep(RE_PLAN_INTERVAL_TIME);
				}

				finishIteration = getFinishIteration();

				if (finishIteration == null) {
					Optional<Edge> locationOpt = getLocation();
					if (locationOpt.isPresent()) {
						final Edge location = locationOpt.get();
						logger.info("Agent: {} is still executing it's route, location: {}", this.id, location.getId());

						Node from = this.route.getNodes().stream().filter(e -> e.getIncomingEdges().contains(location))
								.findFirst().orElseThrow();
						if (!from.equals(this.to)) {
							infoRequest = new Message(this.id, MessageType.ROUTE_INFO_REQUEST,
									new RouteInfoRequest(from, to));
							masController.sendMessage(infoRequest);

							infoRequestMessageAnswer = infoRequest.getConnection().take();
							infoRequestMessageAnswerBody = (RouteInfoAnswer) infoRequestMessageAnswer.getBody();
							if (selectReRoute(infoRequestMessageAnswerBody.getRoute(), location)) {
								logger.info("Agent: {} determined that re route is needed", this.id);

								Message reRouteMessage = new Message(this.id, MessageType.RE_ROUTE_STARTED_REQUEST,
										new ReRouteStartedRequest(this.route, this.vehicle));
								masController.sendMessage(reRouteMessage);
								Message reRouteAnswer = reRouteMessage.getConnection().take();
								logger.info("Agent: {} recived re route answer: {}", this.id, reRouteAnswer.getBody());

								updateRoute();
								logger.info("Agent: {} started travelling on re route: {}", this.id, this.route);
							} else {
								logger.info("Agent: {} determined that re route is not needed", this.id);
							}
						} else {
							logger.info("Agent: {} re route not needed, we are on direct route to finish", this.id);
						}

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
		return "SimpleRePlanAgent [id=" + id + ", vehicle=" + vehicle + ", from=" + from + ", to=" + to + ", route="
				+ route + ", masController=" + masController + ", sleepTime=" + sleepTime + "]";
	}

}
