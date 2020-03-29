package hu.mas.core;

import hu.mas.core.mas.Mas;
import hu.mas.core.path.PathFinders;

public class DefaultConfiguration {

	public static final String DEFAULT_SUMO_START_COMMAND = "sumo-gui";

	public static final double DEFAULT_STEP_LENGTH = 0.1;

	public static final int DEFAULT_ITERATION_COUNT = 500;

	public static final PathFinders DEFAULT_PATH_FINDER_ALGORITHM = PathFinders.K_SHORTEST_SIMPLE_PATHS;

	public static final Mas DEFAULT_MAS = Mas.SIMPLE_INTENTION_MAS;

	private DefaultConfiguration() {
	}

}
