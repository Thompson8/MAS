package hu.mas.core.agent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AgentPopulator implements Runnable {

	private static final Logger logger = LogManager.getLogger();

	private final Agent templateAgent;

	private final Integer interval;

	private final AtomicBoolean stopFlag;

	private final ExecutorService agentExecuter;

	public AgentPopulator(Agent templateAgent, Integer interval, ExecutorService agentExecuter) {
		this.templateAgent = templateAgent;
		this.interval = interval;
		this.stopFlag = new AtomicBoolean(false);
		this.agentExecuter = agentExecuter;
	}

	@Override
	public void run() {
		while (!stopFlag.get()) {
			try {
				logger.info("Populator will sleep for {}ms", interval);
				Thread.sleep(interval);
				Agent newAgent = copy(templateAgent);
				logger.info("Populator created new agent: {}", newAgent);
				agentExecuter.execute(newAgent);
				logger.info("Populator submitted agent");
			} catch (Exception e) {
				logger.error("Error during agent populator execution, will terminate population", e);
				break;
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
		Vehicle vehicle = new Vehicle(toCopy.getTypeId(), toCopy.getMaxSpeed(), toCopy.getLength());
		vehicle.setCaculateEdgeImpact(toCopy.getCaculateEdgeImpact());
		return vehicle;
	}

	public void stop() {
		stopFlag.set(true);
	}

}
