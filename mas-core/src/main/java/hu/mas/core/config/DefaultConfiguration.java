package hu.mas.core.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import hu.mas.core.mas.Mas;
import hu.mas.core.mas.pathfinder.PathFinder;

public class DefaultConfiguration {

	public static final String DEFAULT_SUMO_START_COMMAND = "sumo-gui";

	public static final double DEFAULT_STEP_LENGTH = 0.5;

	public static final int DEFAULT_ITERATION_COUNT = 10;

	public static final PathFinder DEFAULT_PATH_FINDER_ALGORITHM = PathFinder.K_SHORTEST_SIMPLE_PATHS;

	public static final Mas DEFAULT_MAS = Mas.DETAILED_INTENTION_TRAVEL_TIME_MAS;

	public static final List<String> DEFAULT_ROAD_TYPES_TO_INCLUDE = Collections
			.unmodifiableList(Arrays.asList("highway.residential", "highway.tertiary"));

	public static final double DEFAULT_TIME_FRAME_FOR_FLOW_CALCULATION = 60;
	
	private DefaultConfiguration() {
	}

}
