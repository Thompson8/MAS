package hu.mas.core.mas.intention.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.graph.Road;
import hu.mas.core.mas.model.graph.Vertex;
import hu.mas.core.util.Pair;

public class RoutePredictionHolderImpl implements RoutePredictionHolder {

	private final Map<Pair<Vertex, Vertex>, List<Route>> holder;

	public RoutePredictionHolderImpl() {
		this.holder = new ConcurrentHashMap<>();
	}

	@Override
	public List<Route> getRoutes() {
		return holder.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
	}

	@Override
	public Optional<List<Route>> getRoutes(Vertex from, Vertex to) {
		return Optional.ofNullable(holder.get(new Pair<>(from, to)));
	}

	@Override
	public void appendToRoadArrivalList(Road road, Vehicle vehicle, double start, double finish) {
		road.getArrivalList().add(new Pair<>(vehicle, new Pair<>(start, finish)));
	}

	@Override
	public void addRoute(Vertex from, Vertex to, Route route) {
		Optional<List<Route>> routes = getRoutes(from, to);
		if (routes.isPresent()) {
			routes.get().add(route);
		} else {
			List<Route> list = new ArrayList<>();
			list.add(route);
			holder.put(new Pair<>(from, to), list);
		}
	}

	@Override
	public String toString() {
		return "RoutePredictionHolderImpl [holder=" + holder + "]";
	}

}
