package hu.mas.core.mas.intention.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import hu.mas.core.mas.model.Edge;

public class Intentions {

	private final Map<Edge, List<Intention>> intentionsForEdges;

	public Intentions() {
		this.intentionsForEdges = new HashMap<>();
	}

	public void add(Edge edge, Intention intention) {
		List<Intention> intentions = intentionsForEdges.get(edge);
		if (intentions == null) {
			intentions = new ArrayList<>();
			intentions.add(intention);
			intentionsForEdges.put(edge, intentions);
		} else {
			intentions.add(intention);
		}
	}

	public Optional<List<Intention>> findIntentions(Edge edge) {
		return Optional.ofNullable(intentionsForEdges.get(edge));
	}

	public Map<Edge, List<Intention>> getIntentionsForEdges() {
		return intentionsForEdges;
	}

	@Override
	public String toString() {
		return "Intentions [intentionsForEdges=" + intentionsForEdges + "]";
	}

}
