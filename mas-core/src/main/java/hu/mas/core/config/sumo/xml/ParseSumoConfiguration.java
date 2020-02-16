package hu.mas.core.config.sumo.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import hu.mas.core.config.sumo.xml.model.SumoConfiguration;

public class ParseSumoConfiguration {

	public static SumoConfiguration parseConfiguation(String config) throws JAXBException {
		return parseConfiguation(new File(config));
	}

	public static SumoConfiguration parseConfiguation(File config) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(SumoConfiguration.class);
		return (SumoConfiguration) jaxbContext.createUnmarshaller().unmarshal(config);
	}

	private ParseSumoConfiguration() {
	}

}
