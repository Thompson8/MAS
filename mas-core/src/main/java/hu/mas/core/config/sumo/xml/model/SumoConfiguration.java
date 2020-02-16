package hu.mas.core.config.sumo.xml.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
public class SumoConfiguration {

	private Input input;

	@XmlElement
	public Input getInput() {
		return input;
	}

	public void setInput(Input input) {
		this.input = input;
	}

	@Override
	public String toString() {
		return "Configuration [input=" + input + "]";
	}

}
