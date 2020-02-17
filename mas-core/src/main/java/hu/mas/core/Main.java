package hu.mas.core;

import java.util.List;
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
import hu.mas.core.mas.Mas;
import hu.mas.core.mas.MasController;
import hu.mas.core.mas.converter.Converter;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.path.dft.DFT;
import it.polito.appeal.traci.SumoTraciConnection;

public class Main {

	public static final Logger logger = LogManager.getLogger();

	public static final String SUMO_BIN = "sumo-gui";
	public static final double STEP_LENGTH = 0.1;
	public static final int ITERATION_COUNT = 1000;
	public static final boolean RE_PLAN_MODE = true;

	public static final int MAS_THREAD_POOL_SIZE = 1;

	public static final int AGENT_THREAD_POOL_SIZE = 20;

	public static final ExecutorService MAS_EXECUTER = Executors.newFixedThreadPool(MAS_THREAD_POOL_SIZE);

	public static final ExecutorService AGENT_EXECUTER = Executors.newFixedThreadPool(AGENT_THREAD_POOL_SIZE);

	public static void main(String[] args) throws JAXBException {
		String sumoStartCommand = SUMO_BIN;
		boolean rePlanMode = RE_PLAN_MODE;
		int iterationCount = ITERATION_COUNT;
		double stepLength = STEP_LENGTH;
		String sumoConfigPath = null;
		String sumoConfigFile = null;
		String agentConfigFile = null;

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
			} else if (arg.startsWith("--replan_agent_mode")) {
				rePlanMode = Boolean.parseBoolean(arg.replace("--replan_agent_mode", "").trim());
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

		if (validConfig) {
			startSimulation(sumoConfigFile, sumoConfigPath, agentConfigFile, sumoStartCommand, stepLength,
					iterationCount, rePlanMode);
		} else {
			logger.error("Invalid configuration, failed to start simulation!");
		}
	}

	private static void startSimulation(String sumoConfigFile, String sumoConfigPath, String agentConfigFile,
			String sumoStartCommand, double stepLength, int iterationCount, boolean rePlanMode) throws JAXBException {
		SumoConfiguration configuration = ParseSumoConfiguration.parseConfiguation(sumoConfigFile);
		Net net = ParseNet.parseNet(sumoConfigPath + configuration.getInput().getNetFile().getValue());
		Graph graph = Converter.fromNetToGraph(net);
		List<Agent> agents = rePlanMode ? loadRePlanAgents(agentConfigFile, graph) : loadAgents(agentConfigFile, graph);

		SumoTraciConnection conn = new SumoTraciConnection(sumoStartCommand, sumoConfigFile);
		conn.addOption("step-length", Double.toString(stepLength));
		conn.addOption("start", "true");

		Mas mas = new Mas(graph, conn, new DFT());
		MasController controller = new MasController(mas, iterationCount);
		linkAgentsWithController(agents, controller);

		try {
			Future<?> controllerTask = MAS_EXECUTER.submit(controller);
			List<Future<?>> agentTasks = agents.stream().map(AGENT_EXECUTER::submit).collect(Collectors.toList());

			controllerTask.get();
			for (Future<?> agentTask : agentTasks) {
				agentTask.get();
			}
		} catch (Exception e) {
			logger.error("Exception during simulation", e);
		} finally {
			MAS_EXECUTER.shutdown();
			AGENT_EXECUTER.shutdown();
		}
	}

	private static List<Agent> loadAgents(String configFile, Graph graph) throws JAXBException {
		AgentConfiguration agentConfiguration = ParseAgent.parseAgentConfiguration(configFile);
		return AgentConverter.toSimpleAgents(agentConfiguration, graph).stream().map(e -> (Agent) e)
				.collect(Collectors.toList());
	}

	private static List<Agent> loadRePlanAgents(String configFile, Graph graph) throws JAXBException {
		AgentConfiguration agentConfiguration = ParseAgent.parseAgentConfiguration(configFile);
		return AgentConverter.toSimpleRePlanAgent(agentConfiguration, graph).stream().map(e -> (Agent) e)
				.collect(Collectors.toList());
	}

	private static void linkAgentsWithController(List<Agent> agents, MasController controller) {
		agents.forEach(e -> e.setMasController(controller));
	}

}