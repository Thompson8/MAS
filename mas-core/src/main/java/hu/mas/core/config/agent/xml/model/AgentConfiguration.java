package hu.mas.core.config.agent.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
public class AgentConfiguration {

	private List<Agent> agents;

	@XmlElement(name = "agent")
	public List<Agent> getAgents() {
		return agents;
	}

	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}

	@Override
	public String toString() {
		return "Configuration [agents=" + agents + "]";
	}
	
}
