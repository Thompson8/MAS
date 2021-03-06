package hu.mas.core.mas.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.model.agent.populator.AgentPopulator;
import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.model.exception.MasRuntimeException;
import hu.mas.core.mas.model.graph.AbstractEdge;
import hu.mas.core.mas.model.message.Message;
import hu.mas.core.mas.model.message.MessageType;
import hu.mas.core.mas.model.message.RouteInfoAnswer;
import hu.mas.core.mas.model.message.RouteInfoRequest;
import hu.mas.core.mas.model.message.RouteSelectionAnswer;
import hu.mas.core.mas.model.message.RouteSelectionRequest;
import hu.mas.core.mas.model.message.RouteStartedAnswer;
import hu.mas.core.mas.model.message.RouteStartedRequest;
import hu.mas.core.mas.model.vehicle.VehicleData;
import hu.mas.core.mas.util.TimeCalculator;

public class MasController implements Runnable {

	private static final Logger logger = LogManager.getLogger(MasController.class);

	private static final int PROPAGATION_SKIP_THRESHOLD = 10;

	private final AbstractMas mas;

	private final int simulationIterationLimit;

	private final Queue<Message> incomingAgentMessageQueue;

	private final double stepLength;

	private final List<AgentPopulator> agentPopulators;

	private LocalDateTime started;

	private LocalDateTime finished;

	public MasController(AbstractMas mas, int simulationIterationLimit, double stepLength) {
		this.mas = mas;
		this.simulationIterationLimit = simulationIterationLimit;
		this.incomingAgentMessageQueue = new ConcurrentLinkedQueue<>();
		this.stepLength = stepLength;
		this.agentPopulators = new ArrayList<>();
	}

	@Override
	public void run() {
		try {
			mas.runServer();
			double endTime = TimeCalculator.calculateTime(simulationIterationLimit, stepLength);
			double previousTime = 0;
			started = LocalDateTime.now();

			for (int i = 0; i < simulationIterationLimit; i++) {
				double currentTime = TimeCalculator.calculateTime(i, stepLength);
				mas.updateData(previousTime, currentTime);

				if (i % 100 == 0) {
					logger.info("Mas iteration: {}, current time: {}", i, currentTime);
				}
				while (!incomingAgentMessageQueue.isEmpty()) {
					Message message = incomingAgentMessageQueue.remove();
					logger.info("Recived message {}, from agent: {}", message.getType(), message.getAgentId());
					processMessage(message, previousTime, currentTime);
					logger.info("Processed message {}, from agent: {}", message.getType(), message.getAgentId());
				}

				if ((endTime - currentTime) > PROPAGATION_SKIP_THRESHOLD) {
					populate(currentTime);
				}

				previousTime = currentTime;
				mas.doTimeStep();
				Thread.sleep(100);
			}

			finished = LocalDateTime.now();

			mas.updateData(previousTime, endTime);
		} catch (Exception e) {
			logger.error("Exception during Mas execution", e);
			throw new MasRuntimeException(e);
		} finally {
			mas.close();
		}
	}

	private void processMessage(Message message, double previousTime, double currentTime) throws Exception {
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
					new RouteSelectionAnswer("Route registered at: " + currentTime));
			mas.updateData(previousTime, currentTime);
			break;
		case ROUTE_STARTED_REQUEST:
			RouteStartedRequest startedRequest = (RouteStartedRequest) message.getBody();
			mas.registerVehicleStart(startedRequest.getVehicle(), currentTime);
			answer = new Message(message.getAgentId(), MessageType.ROUTE_STARTED_ANSWER,
					new RouteStartedAnswer("Start request registered at: " + currentTime));
			break;
		default:
			break;
		}

		message.getConnection().put(answer);
	}

	public Optional<AbstractEdge> findCurrentLocation(Vehicle vehicle) {
		AbstractEdge location = mas.getVehiclesData().get(vehicle).getCurrentEdge();
		if (location != null) {
			return Optional.of(location);
		} else {
			return Optional.of(null);
		}
	}

	public void clearUpIncomingMessages() {
		while (!incomingAgentMessageQueue.isEmpty()) {
			Message message = incomingAgentMessageQueue.remove();
			logger.info("Recived message {}, from agent: {} after termination", message.getType(),
					message.getAgentId());
			message.getConnection().add(new Message(message.getAgentId(), MessageType.MAS_TERMINATED, null));
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
				printWriter.println("Simulation statistics:");
				printWriter.println("Simulation started: " + started + ", finished: " + finished + "\n");
				printWriter.print(getStatictics());

				printWriter.println("Tracked vehicle data:");
				mas.getVehiclesData().getData().entrySet().stream()
						.map(e -> "Vehicle: " + e.getKey().getId() + ", type: " + e.getKey().getTypeId() + ", start: "
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
		builder.append("Finished (all vehicles): " + finishedVehicles + "/" + vehicleCount + "\n");
		List<Double> times = mas.getVehiclesData().getData().values().stream()
				.filter(e -> e.getStatistics().getActualStart() != null && e.getStatistics().getActualFinish() != null)
				.map(e -> e.getStatistics().getActualFinish() - e.getStatistics().getActualStart())
				.collect(Collectors.toList());

		if (!times.isEmpty()) {
			List<String> vehicleTypes = mas.getVehiclesData().getData().keySet().stream().map(Vehicle::getTypeId)
					.distinct().sorted().collect(Collectors.toList());
			vehicleTypes.forEach(v -> {
				List<Entry<Vehicle, VehicleData>> vehiclesByType = mas.getVehiclesData().getData().entrySet().stream()
						.filter(e -> e.getKey().getTypeId().equals(v)).collect(Collectors.toList());
				long finishedVehiclesForType = vehiclesByType.stream().map(Entry::getValue).filter(
						e -> e.getStatistics().getActualStart() != null && e.getStatistics().getActualFinish() != null)
						.count();
				builder.append(
						"Finished " + v + " (type): " + finishedVehiclesForType + "/" + vehiclesByType.size() + "\n");

			});

			builder.append("\n");
			builder.append("Travel time statistics: \n");
			builder.append("Travel times (all vehicles): \n");
			builder.append("Avg time: " + times.stream().reduce(0.0, (a, b) -> a + b) / times.size() + "\n");
			builder.append("Min time: " + times.stream().min((a, b) -> a.compareTo(b)).orElse(0.0) + "\n");
			builder.append("Max time: " + times.stream().max((a, b) -> a.compareTo(b)).orElse(0.0) + "\n");
			builder.append("\n");

			vehicleTypes.forEach(v -> {
				builder.append("Travel times " + v + " (type):\n");
				List<Double> timesForType = mas.getVehiclesData().getData().entrySet().stream()
						.filter(e -> e.getKey().getTypeId().equals(v)).map(Entry::getValue)
						.filter(e -> e.getStatistics().getActualStart() != null
								&& e.getStatistics().getActualFinish() != null)
						.map(e -> e.getStatistics().getActualFinish() - e.getStatistics().getActualStart())
						.collect(Collectors.toList());
				builder.append(
						"Avg time: " + timesForType.stream().reduce(0.0, (a, b) -> a + b) / timesForType.size() + "\n");
				builder.append("Min time: " + timesForType.stream().min((a, b) -> a.compareTo(b)).orElse(0.0) + "\n");
				builder.append("Max time: " + timesForType.stream().max((a, b) -> a.compareTo(b)).orElse(0.0) + "\n");
				builder.append("\n");
			});

		} else {
			builder.append("No time data to process\n");
		}
		return builder.toString();
	}

	public void addAgentPopulator(AgentPopulator agentPopulator) {
		this.agentPopulators.add(agentPopulator);
	}

	private void populate(double currentTime) {
		this.agentPopulators.forEach(e -> e.populate(currentTime, stepLength));
	}

}
