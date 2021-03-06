package hu.mas.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.model.agent.AbstractAgent;
import hu.mas.core.agent.model.agent.populator.AgentPopulator;
import hu.mas.core.config.Configuration;
import hu.mas.core.config.agent.xml.AgentConverter;
import hu.mas.core.config.agent.xml.ParseAgent;
import hu.mas.core.config.net.xml.ParseNet;
import hu.mas.core.config.net.xml.model.Net;
import hu.mas.core.config.route.xml.ParseRoutesConfig;
import hu.mas.core.config.route.xml.model.RoutesConfig;
import hu.mas.core.config.sumo.xml.ParseSumoConfiguration;
import hu.mas.core.config.sumo.xml.model.SumoConfiguration;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.Mas;
import hu.mas.core.mas.calculations.TravelTimeUtil;
import hu.mas.core.mas.controller.MasController;
import hu.mas.core.mas.converter.Converter;
import hu.mas.core.mas.intention.prediction.detailed.routing.speed.RoutingSpeedDetailedPredictionIntentionMas;
import hu.mas.core.mas.intention.prediction.detailed.travel.time.TravelTimeDetailedPredictionIntentionMas;
import hu.mas.core.mas.intention.prediction.simple.routing.speed.RoutingSpeedSimplePredictionIntentionMas;
import hu.mas.core.mas.intention.prediction.simple.travel.time.TravelTimeSimplePredictionIntentionMas;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.nointention.sumo.delegate.SumoDelegatedNoIntentionMas;
import hu.mas.core.mas.nointention.travel.time.TravelTimeNoIntentionMas;
import hu.mas.core.mas.pathfinder.AbstractPathFinder;
import hu.mas.core.mas.pathfinder.KShortestSimplePathsFinder;
import hu.mas.core.mas.pathfinder.PathFinder;
import hu.mas.core.util.Pair;
import hu.mas.core.util.concurrency.CustomThreadFactory;
import it.polito.appeal.traci.SumoTraciConnection;

public class Main {

	private static final Logger logger = LogManager.getLogger(Main.class);

	private static final String INPUT_ARG_LIST_DELIMETER = ",";

	private static final int MAS_THREAD_POOL_SIZE = 1;

	private static final int AGENT_THREAD_POOL_SIZE = 1024;

	private static final ExecutorService MAS_EXECUTER = Executors.newFixedThreadPool(MAS_THREAD_POOL_SIZE,
			new CustomThreadFactory("mas-pool"));

	private static final ExecutorService AGENT_EXECUTER = Executors.newFixedThreadPool(AGENT_THREAD_POOL_SIZE,
			new CustomThreadFactory("agent-pool"));

	public static void main(String[] args) throws JAXBException, IOException {
		try {
			Configuration configuration = parseArgs(args);

			if (validateConfiguration(configuration)) {
				logger.info("Simulation parameters: {}", configuration);
				Optional<Pair<MasController, SumoTraciConnection>> controllerAndConnectionOpt = startSimulation(
						configuration);
				if (controllerAndConnectionOpt.isPresent()) {
					Pair<MasController, SumoTraciConnection> controllerAndConnection = controllerAndConnectionOpt.get();
					logger.info("Output file: {}, start writing", configuration.getOutputFile());
					controllerAndConnection.getLeft().writeStatisticsToFile(new File(configuration.getOutputFile()));
					logger.info("Output witing finished");
					closeSumo(controllerAndConnection.getRigth());
				}
			} else {
				logger.error("Invalid configuration, failed to start simulation!");
			}
		} catch (JAXBException e) {
			logger.error("Exception during xml parsing", e);
		} catch (IOException e) {
			logger.error("Exception during read or write", e);
		} catch (Exception e) {
			logger.error("Exception during simulation", e);
		}
	}

	private static Configuration parseArgs(String[] args) {
		Configuration configuration = new Configuration();

		for (String arg : args) {
			if (arg.startsWith("--sumo_start_command=")) {
				configuration.setSumoStartCommand(arg.replace("--sumo_start_command=", "").trim());
			} else if (arg.startsWith("--sumo_config_file=")) {
				String sumoConfigFile = arg.replace("--sumo_config_file=", "").trim();
				configuration.setSumoConfigFile(sumoConfigFile);
				configuration.setSumoConfigPath(sumoConfigFile.substring(0, sumoConfigFile.lastIndexOf('/') + 1));
			} else if (arg.startsWith("--agent_config_file=")) {
				configuration.setAgentConfigFile(arg.replace("--agent_config_file=", "").trim());
			} else if (arg.startsWith("--simulation_iteration=")) {
				configuration.setIterationCount(Integer.parseInt(arg.replace("--simulation_iteration=", "").trim()));
			} else if (arg.startsWith("--step_length=")) {
				configuration.setStepLength(Double.parseDouble(arg.replace("--step_length=", "").trim()));
			} else if (arg.startsWith("--output_file=")) {
				configuration.setOutputFile(arg.replace("--output_file=", "").trim());
			} else if (arg.startsWith("--mas=")) {
				configuration.setMasToUse(Mas.valueOf(arg.replace("--mas=", "").trim()));
			} else if (arg.startsWith("--road_types_to_include=")) {
				configuration.setRoadTypesToInclude(Arrays
						.asList(arg.replace("--road_types_to_include=", "").trim().split(INPUT_ARG_LIST_DELIMETER)));
			} else if (arg.startsWith("--trip_info_output_file=")) {
				configuration.setTripInfoOutputFile(arg.replace("--trip_info_output_file=", "").trim());
			} else if (arg.startsWith("--time_frame_for_flow_calculation=")) {
				configuration.setTimeFrameForFlowCalculation(
						Double.parseDouble(arg.replace("--time_frame_for_flow_calculation=", "").trim()));
			}
		}

		return configuration;
	}

	private static boolean validateConfiguration(Configuration configuration) {
		boolean validConfig = true;
		if (configuration.getSumoConfigFile() == null) {
			logger.error("--sumo_config_file parameter cannot be null!");
			validConfig = false;
		}
		if (configuration.getAgentConfigFile() == null) {
			logger.error("--agent_config_file parameter cannot be null!");
			validConfig = false;
		}
		if (configuration.getStepLength() == null) {
			logger.error("--step_length parameter cannot be null!");
			validConfig = false;
		}
		if (configuration.getIterationCount() == null) {
			logger.error("--simulation_iteration parameter cannot be null!");
			validConfig = false;
		}
		if (configuration.getMasToUse() == null) {
			logger.error("--mas parameter cannot be null!");
			validConfig = false;
		}
		if (configuration.getOutputFile() == null) {
			logger.error("--output_file parameter cannot be null!");
			validConfig = false;
		}

		return validConfig;
	}

	private static Optional<Pair<MasController, SumoTraciConnection>> startSimulation(Configuration config)
			throws JAXBException, InterruptedException, ExecutionException {
		SumoConfiguration configuration = ParseSumoConfiguration.parseConfiguation(config.getSumoConfigFile());
		Net net = ParseNet.parseNet(config.getSumoConfigPath() + configuration.getInput().getNetFile().getValue());
		RoutesConfig routes = ParseRoutesConfig
				.parseRoutes(config.getSumoConfigPath() + configuration.getInput().getRouteFiles().getValue());

		MasGraph graph = Converter.fromNetToGraph(net, Arrays.asList("highway.residential", "highway.tertiary"));
		SumoTraciConnection conn = new SumoTraciConnection(config.getSumoStartCommand(), config.getSumoConfigFile());
		conn.addOption("step-length", Double.toString(config.getStepLength()));
		conn.addOption("start", "true");
		if (config.getTripInfoOutputFile() != null) {
			conn.addOption("tripinfo-output", config.getTripInfoOutputFile());
		}

		AbstractMas mas = createMas(config.getMasToUse(), graph, conn, config.getPathFinderAlgorithm(), config);

		MasController controller = new MasController(mas, config.getIterationCount(), config.getStepLength());

		List<AbstractAgent> agents = loadAgents(config.getAgentConfigFile(), graph, conn, routes, controller);

		try {
			List<AbstractAgent> toSubmit = new ArrayList<>();
			for (AbstractAgent agent : agents) {
				if (agent.getAgentStartInterval() == null) {
					toSubmit.add(agent);
				} else {
					controller.addAgentPopulator(
							new AgentPopulator(agent, agent.getAgentStartInterval(), AGENT_EXECUTER));
				}
			}

			Future<?> controllerTask = MAS_EXECUTER.submit(controller);
			toSubmit.forEach(AGENT_EXECUTER::submit);

			controllerTask.get();

			logger.info("Simulation over, shutting down unfinished agents");

			return Optional.of(new Pair<>(controller, conn));
		} finally {
			cleanUpThreadsAndConnection(conn);
		}
	}

	private static void cleanUpThreadsAndConnection(SumoTraciConnection connection) {
		try {
			AGENT_EXECUTER.shutdownNow();
		} catch (Exception e) {
			// Not real errors
			logger.debug("Error during agent executor shutdown", e);
		}
		try {
			MAS_EXECUTER.shutdownNow();
		} catch (Exception e) {
			// Not real errors
			logger.debug("Error during mass executor shutdown", e);
		}

		if (!connection.isClosed()) {
			connection.close();
		}
	}

	private static AbstractMas createMas(Mas masToUse, MasGraph graph, SumoTraciConnection conn,
			PathFinder pathFinderAlgorithm, Configuration config) {
		AbstractPathFinder pathFinder = getPathFinder(pathFinderAlgorithm);

		switch (masToUse) {
		case DETAILED_INTENTION_TRAVEL_TIME_MAS:
			return new TravelTimeDetailedPredictionIntentionMas(graph, conn, pathFinder,
					new TravelTimeUtil(config.getTimeFrameForFlowCalculation()));
		case NO_INTENTION_SUMO_DELEGATED_MAS:
			return new SumoDelegatedNoIntentionMas(graph, conn, pathFinder);
		case NO_INTENTION_TRAVEL_TIME_MAS:
			return new TravelTimeNoIntentionMas(graph, conn, pathFinder,
					new TravelTimeUtil(config.getTimeFrameForFlowCalculation()));
		case DETAILED_INTENTION_ROUTING_SPEED_MAS:
			return new RoutingSpeedDetailedPredictionIntentionMas(graph, conn, pathFinder);
		case SIMPLE_INTENTION_TRAVEL_TIME_MAS:
			return new TravelTimeSimplePredictionIntentionMas(graph, conn, pathFinder,
					new TravelTimeUtil(config.getTimeFrameForFlowCalculation()));
		case SIMPLE_INTENTION_ROUTING_SPEED_MAS:
			return new RoutingSpeedSimplePredictionIntentionMas(graph, conn, pathFinder);
		default:
			throw new UnsupportedOperationException("Unsupported MAS!");
		}
	}

	private static AbstractPathFinder getPathFinder(PathFinder pathFinderAlgorithm) {
		switch (pathFinderAlgorithm) {
		case K_SHORTEST_SIMPLE_PATHS:
			return new KShortestSimplePathsFinder();
		default:
			throw new UnsupportedOperationException("Unsupported path finder!");
		}
	}

	private static List<AbstractAgent> loadAgents(String configFile, MasGraph graph, SumoTraciConnection connection,
			RoutesConfig routes, MasController controller) throws JAXBException {
		return AgentConverter
				.toAgents(ParseAgent.parseAgentConfiguration(configFile), graph, connection, routes, controller)
				.stream().map(e -> (AbstractAgent) e).collect(Collectors.toList());
	}

	private static void closeSumo(SumoTraciConnection connection) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String input = null;
			do {
				logger.info("Press enter to close the application and SUMO");
				input = reader.readLine();
			} while (!"".equals(input));
		}

		try {
			Field field = SumoTraciConnection.class.getDeclaredField("sumoProcess");
			field.setAccessible(true);
			Process process = (Process) field.get(connection);
			process.destroy();
		} catch (Exception e) {
			logger.error("Error during SUMO closing", e);
		}
	}

}