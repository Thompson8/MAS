package hu.mas.core;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.Agent;
import hu.mas.core.config.agent.xml.AgentConverter;
import hu.mas.core.config.agent.xml.ParseAgent;
import hu.mas.core.config.agent.xml.model.AgentConfiguration;
import hu.mas.core.config.net.xml.ParseNet;
import hu.mas.core.config.net.xml.model.Net;
import hu.mas.core.config.sumo.xml.ParseSumoConfiguration;
import hu.mas.core.config.sumo.xml.model.SumoConfiguration;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.MasController;
import hu.mas.core.mas.SimpleMas;
import hu.mas.core.mas.converter.Converter;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.path.PathFinder;
import hu.mas.core.path.dft.DFT;
import hu.mas.core.path.dijkstra.Dijkstra;
import it.polito.appeal.traci.SumoTraciConnection;

public class Main {

	private static final Logger logger = LogManager.getLogger();

	private static final String SUMO_BIN = "sumo-gui";
	private static final double STEP_LENGTH = 0.1;
	private static final int ITERATION_COUNT = 500;
	private static final String PATH_FINDER_ALGORITHM = "DFT";
	private static final boolean REP_PLAN_AGENT = false;

	private static final int MAS_THREAD_POOL_SIZE = 1;

	private static final int AGENT_THREAD_POOL_SIZE = 20;

	private static final ExecutorService MAS_EXECUTER = Executors.newFixedThreadPool(MAS_THREAD_POOL_SIZE);

	private static final ExecutorService AGENT_EXECUTER = Executors.newFixedThreadPool(AGENT_THREAD_POOL_SIZE);

	public static void main(String[] args) throws JAXBException {
		String sumoStartCommand = SUMO_BIN;
		int iterationCount = ITERATION_COUNT;
		double stepLength = STEP_LENGTH;
		String pathFinderAlgorithm = PATH_FINDER_ALGORITHM;
		boolean replanAgents = REP_PLAN_AGENT;
		String sumoConfigPath = null;
		String sumoConfigFile = null;
		String agentConfigFile = null;
		String outputFile = null;

		for (String arg : args) {
			if (arg.startsWith("--sumo_start_command=")) {
				sumoStartCommand = arg.replace("--sumo_start_command=", "").trim();
			} else if (arg.startsWith("--sumo_config_file=")) {
				sumoConfigFile = arg.replace("--sumo_config_file=", "").trim();
				sumoConfigPath = sumoConfigFile.substring(0, sumoConfigFile.lastIndexOf('/') + 1);
			} else if (arg.startsWith("--agent_config_file=")) {
				agentConfigFile = arg.replace("--agent_config_file=", "").trim();
			} else if (arg.startsWith("--simulation-iteration=")) {
				iterationCount = Integer.parseInt(arg.replace("--simulation-iteration=", "").trim());
			} else if (arg.startsWith("--step-length=")) {
				stepLength = Double.parseDouble(arg.replace("--step-length=", "").trim());
			} else if (arg.startsWith("--output_file=")) {
				outputFile = arg.replace("--output_file=", "").trim();
			} else if (arg.startsWith("--path_finder_alg=")) {
				pathFinderAlgorithm = arg.replace("--path_finder_alg=", "").trim();
			} else if (arg.startsWith("--re_plan_agent=")) {
				replanAgents = Boolean.parseBoolean(arg.replace("--re_plan_agent=", "").trim());
			}
		}

		boolean validConfig = true;
		if (sumoConfigFile == null) {
			logger.error("--sumo_config_file parameter cannot be null!");
			validConfig = false;
		}
		if (agentConfigFile == null) {
			logger.error("--agent_config_file parameter cannot be null!");
			validConfig = false;
		}
		if (outputFile == null) {
			logger.error("--output_file parameter cannot be null!");
			validConfig = false;
		}

		if (validConfig) {
			Optional<MasController> controller = startSimulation(sumoConfigFile, sumoConfigPath, agentConfigFile,
					sumoStartCommand, stepLength, iterationCount, pathFinderAlgorithm, replanAgents);
			if (controller.isPresent()) {
				try {
					logger.info("Output file: {}, start writing", outputFile);
					controller.get().writeStatisticsToFile(new File(outputFile));
					logger.info("Output witing finished");
				} catch (Exception e) {
					logger.error("Exception during simulation output file creation", e);
				}
			}
		} else {
			logger.error("Invalid configuration, failed to start simulation!");
		}
	}

	private static Optional<MasController> startSimulation(String sumoConfigFile, String sumoConfigPath,
			String agentConfigFile, String sumoStartCommand, double stepLength, int iterationCount,
			String pathFinderAlgorithm, boolean replanAgents) throws JAXBException {
		SumoConfiguration configuration = ParseSumoConfiguration.parseConfiguation(sumoConfigFile);
		Net net = ParseNet.parseNet(sumoConfigPath + configuration.getInput().getNetFile().getValue());
		Graph graph = Converter.fromNetToGraph(net);
		SumoTraciConnection conn = new SumoTraciConnection(sumoStartCommand, sumoConfigFile);
		conn.addOption("step-length", Double.toString(stepLength));
		conn.addOption("start", "true");
		List<Agent> agents = replanAgents ? loadRePlanAgents(agentConfigFile, graph, conn)
				: loadAgents(agentConfigFile, graph, conn);

		AbstractMas mas = new SimpleMas(graph, conn, getPathFinder(pathFinderAlgorithm));
		MasController controller = new MasController(mas, iterationCount, stepLength);
		linkAgentsWithController(agents, controller);

		try {
			Future<?> controllerTask = MAS_EXECUTER.submit(controller);
			agents.stream().map(AGENT_EXECUTER::submit).collect(Collectors.toList());
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
		}
	}

	private static PathFinder getPathFinder(String algorithm) {
		if ("DFT".equals(algorithm)) {
			return new DFT();
		} else if ("Dijkstra".equals(algorithm)) {
			return new Dijkstra();
		} else {
			throw new IllegalArgumentException("Unsupported algorithm for path finding!");
		}
	}

	private static List<Agent> loadAgents(String configFile, Graph graph, SumoTraciConnection connection)
			throws JAXBException {
		AgentConfiguration agentConfiguration = ParseAgent.parseAgentConfiguration(configFile);
		return AgentConverter.toSimpleAgents(agentConfiguration, graph, connection).stream().map(e -> (Agent) e)
				.collect(Collectors.toList());
	}

	private static List<Agent> loadRePlanAgents(String configFile, Graph graph, SumoTraciConnection connection)
			throws JAXBException {
		AgentConfiguration agentConfiguration = ParseAgent.parseAgentConfiguration(configFile);
		return AgentConverter.toSimpleRePlanAgents(agentConfiguration, graph, connection).stream().map(e -> (Agent) e)
				.collect(Collectors.toList());
	}

	private static void linkAgentsWithController(List<Agent> agents, MasController controller) {
		agents.forEach(e -> e.setMasController(controller));
	}

}