package hu.mas.core;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.model.agent.Agent;
import hu.mas.core.agent.model.agent.AgentPopulator;
import hu.mas.core.config.Configuration;
import hu.mas.core.config.agent.xml.AgentConverter;
import hu.mas.core.config.agent.xml.ParseAgent;
import hu.mas.core.config.agent.xml.model.AgentConfiguration;
import hu.mas.core.config.net.xml.ParseNet;
import hu.mas.core.config.net.xml.model.Net;
import hu.mas.core.config.route.xml.ParseRoutesConfig;
import hu.mas.core.config.route.xml.model.RoutesConfig;
import hu.mas.core.config.sumo.xml.ParseSumoConfiguration;
import hu.mas.core.config.sumo.xml.model.SumoConfiguration;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.Mas;
import hu.mas.core.mas.MasController;
import hu.mas.core.mas.converter.Converter;
import hu.mas.core.mas.intention.routing.speed.RoutingSpeedIntentionMas;
import hu.mas.core.mas.intention.simple.SimpleIntentionMas;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.nointention.base.BaseMas;
import hu.mas.core.mas.nointention.sumodelegate.SumoDelegatedMas;
import hu.mas.core.mas.path.AbstractPathFinder;
import hu.mas.core.mas.path.KShortestSimplePathsFinder;
import hu.mas.core.mas.path.PathFinder;
import it.polito.appeal.traci.SumoTraciConnection;

public class Main {

	private static final Logger logger = LogManager.getLogger();

	private static final String INPUT_ARG_LIST_DELIMETER = ",";

	private static final int MAS_THREAD_POOL_SIZE = 1;

	private static final int AGENT_POPULATOR_THREAD_POOL_SIZE = 10;

	private static final int AGENT_THREAD_POOL_SIZE = 20;

	private static final ExecutorService MAS_EXECUTER = Executors.newFixedThreadPool(MAS_THREAD_POOL_SIZE);

	private static final ExecutorService AGENT_POPULATOR_EXECUTER = Executors
			.newFixedThreadPool(AGENT_POPULATOR_THREAD_POOL_SIZE);

	private static final ExecutorService AGENT_EXECUTER = Executors.newFixedThreadPool(AGENT_THREAD_POOL_SIZE);

	public static void main(String[] args) throws JAXBException {
		Configuration configuration = parseArgs(args);

		if (validateConfiguration(configuration)) {
			Optional<MasController> controller = startSimulation(configuration);
			if (controller.isPresent()) {
				try {
					logger.info("Output file: {}, start writing", configuration.getOutputFile());
					controller.get().writeStatisticsToFile(new File(configuration.getOutputFile()));
					logger.info("Output witing finished");
				} catch (Exception e) {
					logger.error("Exception during simulation output file creation", e);
				}
			}
		} else {
			logger.error("Invalid configuration, failed to start simulation!");
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
			} else if (arg.startsWith("--path_finder_alg=")) {
				configuration.setPathFinderAlgorithm(PathFinder.valueOf(arg.replace("--path_finder_alg=", "").trim()));
			} else if (arg.startsWith("--mas=")) {
				configuration.setMasToUse(Mas.valueOf(arg.replace("--mas=", "").trim()));
			} else if (arg.startsWith("--road_types_to_include=")) {
				configuration.setRoadTypesToInclude(Arrays
						.asList(arg.replace("--road_types_to_include=", "").trim().split(INPUT_ARG_LIST_DELIMETER)));
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
		if (configuration.getOutputFile() == null) {
			logger.error("--output_file parameter cannot be null!");
			validConfig = false;
		}

		return validConfig;
	}

	private static Optional<MasController> startSimulation(Configuration config) throws JAXBException {
		SumoConfiguration configuration = ParseSumoConfiguration.parseConfiguation(config.getSumoConfigFile());
		Net net = ParseNet.parseNet(config.getSumoConfigPath() + configuration.getInput().getNetFile().getValue());
		RoutesConfig routes = ParseRoutesConfig
				.parseRoutes(config.getSumoConfigPath() + configuration.getInput().getRouteFiles().getValue());

		MasGraph graph = Converter.fromNetToGraph(net, Arrays.asList("highway.residential", "highway.tertiary"));
		SumoTraciConnection conn = new SumoTraciConnection(config.getSumoStartCommand(), config.getSumoConfigFile());
		conn.addOption("step-length", Double.toString(config.getStepLength()));
		conn.addOption("start", "true");
		conn.addOption("tripinfo-output", "/home/tom/Dokumentumok/SUMO/test.xml");
		List<Agent> agents = loadAgents(config.getAgentConfigFile(), graph, conn, routes);

		AbstractMas mas = createMas(config.getMasToUse(), graph, conn, config.getPathFinderAlgorithm());

		MasController controller = new MasController(mas, config.getIterationCount(), config.getStepLength());
		linkAgentsWithController(agents, controller);

		try {
			Future<?> controllerTask = MAS_EXECUTER.submit(controller);
			agents.stream().forEach(e -> {
				if (e.getAgentStartInterval() == null) {
					AGENT_EXECUTER.submit(e);
				} else {
					AGENT_POPULATOR_EXECUTER.submit(new AgentPopulator(e, e.getAgentStartInterval(), AGENT_EXECUTER));
				}
			});

			controllerTask.get();

			logger.info("Simulation over, shutting down unfinished agents");

			return Optional.of(controller);
		} catch (Exception e) {
			logger.error("Exception during simulation", e);
			return Optional.empty();
		} finally {
			try {
				MAS_EXECUTER.shutdownNow();
			} catch (Exception e) {
				logger.error("Error during mass executor shutdown", e);
			}
			try {
				AGENT_EXECUTER.shutdownNow();
			} catch (Exception e) {
				logger.error("Error during agent executor shutdown", e);
			}
			try {
				AGENT_POPULATOR_EXECUTER.shutdownNow();
			} catch (Exception e) {
				logger.error("Error during agent populator executor shutdown", e);
			}
		}
	}

	private static AbstractMas createMas(Mas masToUse, MasGraph graph, SumoTraciConnection conn,
			PathFinder pathFinderAlgorithm) {
		switch (masToUse) {
		case SIMPLE_INTENTION_MAS:
			return new SimpleIntentionMas(graph, conn, getPathFinder(pathFinderAlgorithm));
		case SUMO_DELEGATED_MAS:
			return new SumoDelegatedMas(graph, conn, getPathFinder(pathFinderAlgorithm));
		case BASE_MAS:
			return new BaseMas(graph, conn, getPathFinder(pathFinderAlgorithm));
		case ROUTING_SPEED_INTENTION_MAS:
			return new RoutingSpeedIntentionMas(graph, conn, getPathFinder(pathFinderAlgorithm));
		default:
			throw new UnsupportedOperationException();
		}
	}

	private static AbstractPathFinder getPathFinder(PathFinder pathFinderAlgorithm) {
		switch (pathFinderAlgorithm) {
		case K_SHORTEST_SIMPLE_PATHS:
			return new KShortestSimplePathsFinder();
		default:
			throw new UnsupportedOperationException();
		}
	}

	private static List<Agent> loadAgents(String configFile, MasGraph graph, SumoTraciConnection connection,
			RoutesConfig routes) throws JAXBException {
		AgentConfiguration agentConfiguration = ParseAgent.parseAgentConfiguration(configFile);
		return AgentConverter.toSimpleAgents(agentConfiguration, graph, connection, routes).stream().map(e -> (Agent) e)
				.collect(Collectors.toList());
	}

	private static void linkAgentsWithController(List<Agent> agents, MasController controller) {
		agents.forEach(e -> e.setMasController(controller));
	}

}