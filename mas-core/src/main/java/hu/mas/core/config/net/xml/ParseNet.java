package hu.mas.core.config.net.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import hu.mas.core.config.net.xml.model.Net;

public class ParseNet {

	public static Net parseNet(String net) throws JAXBException {
		return parseNet(new File(net));
	}

	public static Net parseNet(File net) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Net.class);
		return (Net) jaxbContext.createUnmarshaller().unmarshal(net);
	}

	private ParseNet() {
	}

}
