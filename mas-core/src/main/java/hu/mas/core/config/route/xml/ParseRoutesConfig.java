package hu.mas.core.config.route.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import hu.mas.core.config.route.xml.model.RoutesConfig;

public class ParseRoutesConfig {

	public static RoutesConfig parseRoutes(String config) throws JAXBException {
		return parseRoutes(new File(config));
	}

	public static RoutesConfig parseRoutes(File config) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(RoutesConfig.class);
		return (RoutesConfig) jaxbContext.createUnmarshaller().unmarshal(config);
	}

	private ParseRoutesConfig() {
	}

}
