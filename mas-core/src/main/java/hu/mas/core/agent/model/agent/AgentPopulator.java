package hu.mas.core.agent.model.agent;

import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.mas.core.agent.model.vehicle.Vehicle;

public class AgentPopulator {

	private static final Logger logger = LogManager.getLogger(AgentPopulator.class);

	private final Agent templateAgent;

	private final Integer interval;

	private final ExecutorService agentExecuter;

	public AgentPopulator(Agent templateAgent, Integer interval, ExecutorService agentExecuter) {
		this.templateAgent = templateAgent;
		this.interval = interval;
		this.agentExecuter = agentExecuter;
	}

	public void populate(double time) {
		if (time % interval < 1) {
			try {
				Agent newAgent = copy(templateAgent);
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

	private Agent copy(Agent toCopy) {
		if (SimpleAgent.class.equals(toCopy.getClass())) {
			return copySimpleAgent((SimpleAgent) toCopy);
		} else {
			throw new UnsupportedOperationException("Unsopported agent to copy");
		}
	}

	private SimpleAgent copySimpleAgent(SimpleAgent toCopy) {
		SimpleAgent agent = new SimpleAgent(copyVehicle(toCopy.getVehicle()), toCopy.from, toCopy.to,
				toCopy.masController, toCopy.connection);
		agent.setSleepTime(toCopy.getSleepTime());
		return agent;
	}

	private Vehicle copyVehicle(Vehicle toCopy) {
		return new Vehicle(toCopy.getTypeId(), toCopy.getMaxSpeed(), toCopy.getLength());
	}

}
