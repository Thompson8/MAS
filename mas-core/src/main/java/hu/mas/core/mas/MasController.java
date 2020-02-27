package hu.mas.core.mas;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.Vehicle;
import hu.mas.core.agent.message.Message;
import hu.mas.core.agent.message.MessageType;
import hu.mas.core.agent.message.ReRouteStartedAnswer;
import hu.mas.core.agent.message.ReRouteStartedRequest;
import hu.mas.core.agent.message.RouteInfoAnswer;
import hu.mas.core.agent.message.RouteInfoRequest;
import hu.mas.core.agent.message.RouteSelectionAnswer;
import hu.mas.core.agent.message.RouteSelectionRequest;
import hu.mas.core.agent.message.RouteStartedAnswer;
import hu.mas.core.agent.message.RouteStartedRequest;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasException;
import hu.mas.core.mas.model.Node;

public class MasController implements Runnable {

	private static final Logger logger = LogManager.getLogger();

	private final AbstractMas mas;

	private final int simulationIterationLimit;

	private final Queue<Message> incomingAgentMessageQueue;

	private final double stepLength;

	public MasController(AbstractMas mas, int simulationIterationLimit, double stepLength) {
		this.mas = mas;
		this.simulationIterationLimit = simulationIterationLimit;
		this.incomingAgentMessageQueue = new ConcurrentLinkedQueue<>();
		this.stepLength = stepLength;
	}

	@Override
	public void run() {
		try {
			mas.runServer();

			for (int i = 0; i < simulationIterationLimit; i++) {
				mas.updateData(i);

				if (i % 100 == 0) {
					logger.info("Mas iteration: {}, current time: {}", i, i * stepLength);
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
		} catch (Exception e) {
			logger.error("Exception during Mas execution", e);
			throw new MasException(e);
		} finally {
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
			mas.registerRoute(selection.getVehicle(), selection.getRoute());
			answer = new Message(message.getAgentId(), MessageType.ROUTE_SELECTION_ANSWER,
					new RouteSelectionAnswer("Route registered in iteration: " + iteration));
			mas.updateData(iteration);
			break;
		case ROUTE_STARTED_REQUEST:
			RouteStartedRequest startedRequest = (RouteStartedRequest) message.getBody();
			mas.registerVehicleStart(startedRequest.getVehicle(), iteration);
			answer = new Message(message.getAgentId(), MessageType.ROUTE_STARTED_ANSWER,
					new RouteStartedAnswer("Start request registered in iteration: " + iteration));
			break;
		case RE_ROUTE_STARTED_REQUEST:
			ReRouteStartedRequest reRouteStartedRequest = (ReRouteStartedRequest) message.getBody();
			mas.registerReRoute(reRouteStartedRequest.getVehicle(), reRouteStartedRequest.getRoute());
			answer = new Message(message.getAgentId(), MessageType.RE_ROUTE_STARTED_ANSWER,
					new ReRouteStartedAnswer("Start re route request registered in iteration: " + iteration));
			mas.updateData(iteration);
			break;
		default:
			break;
		}

		message.getConnection().put(answer);
	}

	public Optional<Edge> findCurrentLocation(Vehicle vehicle) {
		Edge location = mas.getVehiclesData().get(vehicle).getCurrentEdge();
		if (location != null) {
			return Optional.of(location);
		} else {
			return Optional.of(null);
		}
	}

	public boolean isFinished(Vehicle vehicle) {
		return mas.getVehiclesData().get(vehicle).getStatistics().getFinish() != null;
	}

	public boolean isStarted(Vehicle vehicle) {
		return mas.getVehiclesData().get(vehicle).getStatistics().getStart() != null;
	}

	public Integer getStartIteration(Vehicle vehicle) {
		return mas.getVehiclesData().get(vehicle).getStatistics().getStart();
	}

	public Integer getFinishIteration(Vehicle vehicle) {
		return mas.getVehiclesData().get(vehicle).getStatistics().getFinish();
	}

	public void sendMessage(Message message) {
		this.incomingAgentMessageQueue.add(message);
	}

	public Optional<Node> findNode(String id) {
		return mas.findNode(id);
	}

	public void writeStatisticsToFile(File file) throws IOException {
		try (FileWriter fileWriter = new FileWriter(file)) {
			try (PrintWriter printWriter = new PrintWriter(fileWriter)) {
				printWriter.println("Statistics");
				printWriter.println(getStatictics());

				printWriter.println("Tracked vehicle data:");
				mas.getVehiclesData().getData().entrySet().stream()
						.map(e -> "Vehicle: " + e.getKey().getId() + ", start: "
								+ e.getValue().getStatistics().getStart() + ", finish: "
								+ e.getValue().getStatistics().getFinish())
						.forEach(printWriter::println);

				printWriter.println();
				printWriter.println("Historical travel weigth matrixes:");

				mas.getHistoricalTravelWeigthMatrix().entrySet().stream()
						.sorted((a, b) -> a.getKey().compareTo(b.getKey()))
						.map(e -> e.getKey() + " : "
								+ e.getValue().stream().map(Arrays::deepToString).collect(Collectors.toList()))
						.forEach(printWriter::println);
			}
		}
	}

	public String getStatictics() {
		StringBuilder builder = new StringBuilder();
		Integer vehicleCount = mas.getVehiclesData().getData().size();
		long finishedVehicles = mas.getVehiclesData().getData().values().stream()
				.filter(e -> e.getStatistics().getStart() != null && e.getStatistics().getFinish() != null).count();
		builder.append("Finished vehicles: " + finishedVehicles + "/" + vehicleCount + "\n");
		List<Integer> times = mas.getVehiclesData().getData().values().stream()
				.filter(e -> e.getStatistics().getStart() != null && e.getStatistics().getFinish() != null)
				.map(e -> e.getStatistics().getFinish() - e.getStatistics().getStart()).collect(Collectors.toList());
		if (!times.isEmpty()) {
			builder.append("Avg time: " + times.stream().reduce(0, (a, b) -> a + b) / times.size() + "\n");
			builder.append("Min time: " + times.stream().min((a, b) -> a.compareTo(b)).orElseThrow() + "\n");
			builder.append("Max time: " + times.stream().max((a, b) -> a.compareTo(b)).orElseThrow() + "\n");
		} else {
			builder.append("No time data to process\n");
		}
		return builder.toString();
	}

}
