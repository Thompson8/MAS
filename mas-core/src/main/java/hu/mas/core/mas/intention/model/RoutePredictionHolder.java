package hu.mas.core.mas.intention.model;

import java.util.List;
import java.util.Optional;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.graph.Vertex;

public interface RoutePredictionHolder {

	public List<Route> getRoutes();

	public Optional<List<Route>> getRoutes(Vertex from, Vertex to);
	
	public void appendToRoadArrivalList(Road road, Vehicle vehicle, double start, double finish);
	
	public void addRoute(Vertex from, Vertex to, Route route);
	
}
