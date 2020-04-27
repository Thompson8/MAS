package hu.mas.core.agent.model.agent.populator;

import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.model.agent.AbstractAgent;
import hu.mas.core.agent.model.agent.Agent;
import hu.mas.core.agent.model.vehicle.Vehicle;

public class AgentPopulator {

	private static final Logger logger = LogManager.getLogger(AgentPopulator.class);

	private final AbstractAgent templateAgent;

	private final Integer interval;

	private final ExecutorService agentExecuter;

	public AgentPopulator(AbstractAgent templateAgent, Integer interval, ExecutorService agentExecuter) {
		this.templateAgent = templateAgent;
		this.interval = interval;
		this.agentExecuter = agentExecuter;
	}

	public void populate(double time, double stepLength) {
		if (time % interval < stepLength) {
			try {
				AbstractAgent newAgent = copy(templateAgent);
				logger.info("Populator created new agent: {}", newAgent);
				agentExecuter.execute(newAgent);
				logger.info("Populator submitted agent");
				logger.info("Populator will sleep for {}ms", interval);
			} catch (Exception e) {
				logger.error("Error during agent populator execution, will terminate population", e);
				throw e;
			}
		}
	}

	private AbstractAgent copy(AbstractAgent toCopy) {
		if (Agent.class.equals(toCopy.getClass())) {
			return copyAgent((Agent) toCopy);
		} else {
			throw new UnsupportedOperationException("Unsopported agent to copy");
		}
	}

	private Agent copyAgent(Agent toCopy) {
		Agent agent = new Agent(copyVehicle(toCopy.getVehicle()), toCopy.getFrom(), toCopy.getTo(),
				toCopy.getMasController(), toCopy.getConnection());
		agent.setSleepTime(toCopy.getSleepTime());
		return agent;
	}

	private Vehicle copyVehicle(Vehicle toCopy) {
		return new Vehicle(toCopy.getTypeId(), toCopy.getMaxSpeed(), toCopy.getLength());
	}

}
