package hu.mas.core.mas;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import hu.mas.core.mas.model.MasException;
import hu.mas.core.mas.model.Node;

public class MasController implements Runnable {

	private static final Logger logger = LogManager.getLogger();

	private final Mas mas;

	private final int simulationIterationLimit;

	private final Queue<Message> incomingAgentMessageQueue;

	public MasController(Mas mas, int simulationIterationLimit) {
		this.mas = mas;
		this.simulationIterationLimit = simulationIterationLimit;
		this.incomingAgentMessageQueue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void run() {
		try {
			mas.runServer();

			for (int i = 0; i < simulationIterationLimit; i++) {
				mas.updateData(i);

				if (i % 100 == 0) {
					logger.info("Mas iteration: {}", i);
				}
				while (!incomingAgentMessageQueue.isEmpty()) {
					Message message = incomingAgentMessageQueue.remove();
					logger.info("Recived message {}, from agent: {}", message.getType(), message.getAgentId());
					processMessage(message, i);
					logger.info("Processed message {}, from agent: {}", message.getType(), message.getAgentId());
				}

				mas.doTimeStep();
				Thread.sleep(100);
			}

			logger.info("Vehicle data: {}", mas.getTrackedVehicles());

		} catch (Exception e) {
			logger.error("Exception during Mas execution", e);
			throw new MasException(e);
		} finally {
			logger.info("Simulation over");
			mas.close();
		}
	}

	private void processMessage(Message message, int iteration) throws Exception {
		Message answer = null;

		switch (message.getType()) {
		case ROUTE_INFO_REQUEST:
			RouteInfoRequest request = (RouteInfoRequest) message.getBody();
			answer = new Message(message.getAgentId(), MessageType.ROUTE_RECOMMENDATION,
					new RouteInfoAnswer(mas.getShortestPath(request.getFrom(), request.getTo())));
			break;
		case ROUTE_SELECTION_REQUEST:
			RouteSelectionRequest selection = (RouteSelectionRequest) message.getBody();
			answer = new Message(message.getAgentId(), MessageType.ROUTE_SELECTION_ANSWER,
					new RouteSelectionAnswer(mas.createRoute(selection.getRoute().getId(),
							selection.getRoute().getNodes(), selection.getVehicle(), iteration)));
			break;
		case LOCATION_INFO_REQUEST:
			LocationInfoRequest locationInfoRequest = (LocationInfoRequest) message.getBody();
			answer = new Message(message.getAgentId(), MessageType.LOCATION_INFO_ANSWER,
					new LocationInfoAnswer(mas.getCurrentEdgeByVehicle(locationInfoRequest.getVehicle())));
			break;
		case RE_ROUTE_REQUEST:
			ReRouteRequest reRouteRequest = (ReRouteRequest) message.getBody();
			answer = new Message(message.getAgentId(), MessageType.RE_ROUTE_ANSWER,
					new ReRouteAnswer(mas.createReRoute(reRouteRequest.getRoute().getId(),
							reRouteRequest.getRoute().getNodes(), reRouteRequest.getVehicle())));
			break;
		default:
			break;
		}

		message.getConnection().put(answer);
	}

	public void sendMessage(Message message) {
		this.incomingAgentMessageQueue.add(message);
	}

	public Optional<Node> findNode(String id) {
		return mas.findNode(id);
	}

}
