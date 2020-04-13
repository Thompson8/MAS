package hu.mas.core.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import hu.mas.core.mas.Mas;
import hu.mas.core.path.PathFinder;

public class DefaultConfiguration {

	public static final String DEFAULT_SUMO_START_COMMAND = "sumo-gui";

	public static final double DEFAULT_STEP_LENGTH = 0.5;

	public static final int DEFAULT_ITERATION_COUNT = 1000;

	public static final PathFinder DEFAULT_PATH_FINDER_ALGORITHM = PathFinder.K_SHORTEST_SIMPLE_PATHS;

	public static final Mas DEFAULT_MAS = Mas.ROUTING_SPEED_INTENTION_MAS;

	public static final List<String> DEFAULT_ROAD_TYPES_TO_INCLUDE = Collections
			.unmodifiableList(Arrays.asList("highway.residential", "highway.tertiary"));

	private DefaultConfiguration() {
	}

}
