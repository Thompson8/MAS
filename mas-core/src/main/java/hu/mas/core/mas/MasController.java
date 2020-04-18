package hu.mas.core.mas;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.model.message.Message;
import hu.mas.core.agent.model.message.MessageType;
import hu.mas.core.agent.model.message.RouteInfoAnswer;
import hu.mas.core.agent.model.message.RouteInfoRequest;
import hu.mas.core.agent.model.message.RouteSelectionAnswer;
import hu.mas.core.agent.model.message.RouteSelectionRequest;
import hu.mas.core.agent.model.message.RouteStartedAnswer;
import hu.mas.core.agent.model.message.RouteStartedRequest;
import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.exception.MasRuntimeException;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.util.TimeCalculator;

public class MasController implements Runnable {

	private static final Logger logger = LogManager.getLogger(MasController.class);

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
				double currentTime = TimeCalculator.calculateTime(i, stepLength);
				mas.updateData(currentTime);

				if (i % 100 == 0) {
					logger.info("Mas iteration: {}, current time: {}", i, currentTime);
				}
				while (!incomingAgentMessageQueue.isEmpty()) {
					Message message = incomingAgentMessageQueue.remove();
					logger.info("Recived message {}, from agent: {}", message.getType(), message.getAgentId());
					processMessage(message, currentTime);
					logger.info("Processed message {}, from agent: {}", message.getType(), message.getAgentId());
				}

				mas.doTimeStep();
				Thread.sleep(100);
			}

			mas.updateData(TimeCalculator.calculateTime(simulationIterationLimit, stepLength));
		} catch (Exception e) {
			logger.error("Exception during Mas execution", e);
			throw new MasRuntimeException(e);
		} finally {
			mas.close();
		}
	}

	private void processMessage(Message message, double currentTime) throws Exception {
		Message answer = null;

		switch (message.getType()) {
		case ROUTE_INFO_REQUEST:
			RouteInfoRequest request = (RouteInfoRequest) message.getBody();
			answer = new Message(message.getAgentId(), MessageType.ROUTE_RECOMMENDATION,
					new RouteInfoAnswer(mas.getShortestPath(request.getFrom().getId(), request.getTo().getId(),
							request.getVehicle(), currentTime)));
			break;
		case ROUTE_SELECTION_REQUEST:
			RouteSelectionRequest selection = (RouteSelectionRequest) message.getBody();
			mas.registerRoute(selection.getVehicle(), selection.getRoute(), currentTime);
			answer = new Message(message.getAgentId(), MessageType.ROUTE_SELECTION_ANSWER,
					new RouteSelectionAnswer("Route registered in iteration: " + currentTime));
			mas.updateData(currentTime);
			break;
		case ROUTE_STARTED_REQUEST:
			RouteStartedRequest startedRequest = (RouteStartedRequest) message.getBody();
			mas.registerVehicleStart(startedRequest.getVehicle(), currentTime);
			answer = new Message(message.getAgentId(), MessageType.ROUTE_STARTED_ANSWER,
					new RouteStartedAnswer("Start request registered in iteration: " + currentTime));
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
		return mas.getVehiclesData().get(vehicle).getStatistics().getActualFinish() != null;
	}

	public boolean isStarted(Vehicle vehicle) {
		return mas.getVehiclesData().get(vehicle).getStatistics().getActualStart() != null;
	}

	public Double getStart(Vehicle vehicle) {
		return mas.getVehiclesData().get(vehicle).getStatistics().getActualStart();
	}

	public Double getFinish(Vehicle vehicle) {
		return mas.getVehiclesData().get(vehicle).getStatistics().getActualFinish();
	}

	public void sendMessage(Message message) {
		this.incomingAgentMessageQueue.add(message);
	}

	public void writeStatisticsToFile(File file) throws IOException {
		try (FileWriter fileWriter = new FileWriter(file)) {
			try (PrintWriter printWriter = new PrintWriter(fileWriter)) {
				printWriter.println("Statistics");
				printWriter.println(getStatictics());

				printWriter.println("Tracked vehicle data:");
				mas.getVehiclesData().getData().entrySet().stream()
						.map(e -> "Vehicle: " + e.getKey().getId() + ", start: "
								+ e.getValue().getStatistics().getActualStart() + ", finish: "
								+ e.getValue().getStatistics().getActualFinish())
						.forEach(printWriter::println);
			}
		}
	}

	public String getStatictics() {
		StringBuilder builder = new StringBuilder();
		Integer vehicleCount = mas.getVehiclesData().getData().size();
		long finishedVehicles = mas.getVehiclesData().getData().values().stream()
				.filter(e -> e.getStatistics().getActualStart() != null && e.getStatistics().getActualFinish() != null)
				.count();
		builder.append("Finished vehicles: " + finishedVehicles + "/" + vehicleCount + "\n");
		List<Double> times = mas.getVehiclesData().getData().values().stream()
				.filter(e -> e.getStatistics().getActualStart() != null && e.getStatistics().getActualFinish() != null)
				.map(e -> e.getStatistics().getActualFinish() - e.getStatistics().getActualStart())
				.collect(Collectors.toList());
		if (!times.isEmpty()) {
			builder.append("Avg time: " + times.stream().reduce(0.0, (a, b) -> a + b) / times.size() + "\n");
			builder.append("Min time: " + times.stream().min((a, b) -> a.compareTo(b)).orElseThrow() + "\n");
			builder.append("Max time: " + times.stream().max((a, b) -> a.compareTo(b)).orElseThrow() + "\n");
		} else {
			builder.append("No time data to process\n");
		}
		return builder.toString();
	}

}
