package hu.mas.core.config.sumo.xml.model;

import javax.xml.bind.annotation.XmlElement;

public class Input {

	private NetFile netFile;

	private RouteFiles routeFiles;

	@XmlElement(name = "net-file")
	public NetFile getNetFile() {
		return netFile;
	}

	public void setNetFile(NetFile netFile) {
		this.netFile = netFile;
	}

	@XmlElement(name = "route-files")
	public RouteFiles getRouteFiles() {
		return routeFiles;
	}

	public void setRouteFiles(RouteFiles routeFiles) {
		this.routeFiles = routeFiles;
	}

	@Override
	public String toString() {
		return "Input [netFile=" + netFile + ", routeFiles=" + routeFiles + "]";
	}

}
