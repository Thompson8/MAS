package hu.mas.core.config.agent.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import hu.mas.core.config.agent.xml.model.AgentConfiguration;

public class ParseAgent {

	public static AgentConfiguration parseAgentConfiguration(String agent) throws JAXBException {
		return parseAgentConfiguration(new File(agent));
	}

	public static AgentConfiguration parseAgentConfiguration(File agent) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(AgentConfiguration.class);
		return (AgentConfiguration) jaxbContext.createUnmarshaller().unmarshal(agent);
	}

	private ParseAgent() {
	}

}
