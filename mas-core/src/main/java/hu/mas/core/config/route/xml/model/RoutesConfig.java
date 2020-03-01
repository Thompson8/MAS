package hu.mas.core.config.route.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "routes")
public class RoutesConfig {

	private List<VehicleType> vehicleTypes;

	@XmlElement(name = "vType")
	public List<VehicleType> getVehicleTypes() {
		return vehicleTypes;
	}

	public void setVehicleTypes(List<VehicleType> vehicleTypes) {
		this.vehicleTypes = vehicleTypes;
	}
	
}
