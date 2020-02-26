package hu.mas.core.agent;

import java.util.List;
import java.util.stream.Collectors;

import de.tudresden.ws.container.SumoStringList;
import hu.mas.core.mas.MasController;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Node;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public abstract class RePlanAgent extends Agent {

	protected static final int RE_PLAN_INTERVAL_TIME = 3000;
	
	public RePlanAgent(Vehicle vehicle, Node from, Node to, MasController masController,
			SumoTraciConnection connection) {
		super(vehicle, from, to, masController, connection);
	}

	public RePlanAgent(String id, Vehicle vehicle, Node from, Node to, MasController masController,
			SumoTraciConnection connection) {
		super(id, vehicle, from, to, masController, connection);
	}

	protected void updateRoute() throws Exception {
		SumoStringList edges = new SumoStringList(
				route.getEdges().stream().map(Edge::getId).collect(Collectors.toList()));
		connection.do_job_set(de.tudresden.sumo.cmd.Route.add(route.getId(), edges));
		connection.do_job_set(de.tudresden.sumo.cmd.Vehicle.setRouteID(vehicle.getId(), route.getId()));
	}
	
	public abstract boolean selectReRoute(List<Pair<Double, Pair<List<Node>, List<Edge>>>> routes, Edge location);

}
