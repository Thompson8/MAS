package hu.mas.core.config.net.xml.model;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "net")
public class Net {

	private List<Edge> edges;

	private List<Connection> connections;

	private List<Junction> junctions;

	@XmlElement(name = "edge")
	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	@XmlElement(name = "connection")
	public List<Connection> getConnections() {
		return connections;
	}

	public void setConnections(List<Connection> connection) {
		this.connections = connection;
	}

	@XmlElement(name = "junction")
	public List<Junction> getJunctions() {
		return junctions;
	}

	public void setJunctions(List<Junction> junctions) {
		this.junctions = junctions;
	}

	@Override
	public String toString() {
		return "Net [edges=" + edges + ", connections=" + connections + ", junctions=" + junctions + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(connections, edges, junctions);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Net)) {
			return false;
		}
		Net other = (Net) obj;
		return Objects.equals(connections, other.connections) && Objects.equals(edges, other.edges)
				&& Objects.equals(junctions, other.junctions);
	}	

}
