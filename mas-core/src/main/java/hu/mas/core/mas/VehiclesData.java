package hu.mas.core.mas;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import hu.mas.core.agent.model.vehicle.Vehicle;

public class VehiclesData {

	private final Map<Vehicle, VehicleData> data;

	public VehiclesData() {
		this.data = new ConcurrentHashMap<>();
	}

	public VehicleData get(Vehicle vehicle) {
		return data.get(vehicle);
	}

	public void put(Vehicle vehicle, VehicleData entry) {
		data.put(vehicle, entry);
	}

	public Map<Vehicle, VehicleData> getData() {
		return data;
	}

	@Override
	public String toString() {
		return "VehicleData [data=" + data + "]";
	}

}
