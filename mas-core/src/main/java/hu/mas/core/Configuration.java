package hu.mas.core;

import java.util.List;

import hu.mas.core.mas.Mas;
import hu.mas.core.path.PathFinders;

public class Configuration {

	private String sumoConfigFile;

	private String sumoConfigPath;

	private String agentConfigFile;

	private String sumoStartCommand;

	private double stepLength;

	private int iterationCount;

	private PathFinders pathFinderAlgorithm;

	private Mas masToUse;
	
	private String outputFile;
	
	private List<String> roadTypesToInclude;

	public Configuration() {
		this.sumoStartCommand = DefaultConfiguration.DEFAULT_SUMO_START_COMMAND;
		this.stepLength = DefaultConfiguration.DEFAULT_STEP_LENGTH;
		this.iterationCount = DefaultConfiguration.DEFAULT_ITERATION_COUNT;
		this.pathFinderAlgorithm = DefaultConfiguration.DEFAULT_PATH_FINDER_ALGORITHM;
		this.masToUse = DefaultConfiguration.DEFAULT_MAS;
		this.roadTypesToInclude = DefaultConfiguration.DEFAULT_ROAD_TYPES_TO_INCLUDE;
	}

	public String getSumoConfigFile() {
		return sumoConfigFile;
	}

	public void setSumoConfigFile(String sumoConfigFile) {
		this.sumoConfigFile = sumoConfigFile;
	}

	public String getSumoConfigPath() {
		return sumoConfigPath;
	}

	public void setSumoConfigPath(String sumoConfigPath) {
		this.sumoConfigPath = sumoConfigPath;
	}

	public String getAgentConfigFile() {
		return agentConfigFile;
	}

	public void setAgentConfigFile(String agentConfigFile) {
		this.agentConfigFile = agentConfigFile;
	}

	public String getSumoStartCommand() {
		return sumoStartCommand;
	}

	public void setSumoStartCommand(String sumoStartCommand) {
		this.sumoStartCommand = sumoStartCommand;
	}

	public double getStepLength() {
		return stepLength;
	}

	public void setStepLength(double stepLength) {
		this.stepLength = stepLength;
	}

	public int getIterationCount() {
		return iterationCount;
	}

	public void setIterationCount(int iterationCount) {
		this.iterationCount = iterationCount;
	}

	public PathFinders getPathFinderAlgorithm() {
		return pathFinderAlgorithm;
	}

	public void setPathFinderAlgorithm(PathFinders pathFinderAlgorithm) {
		this.pathFinderAlgorithm = pathFinderAlgorithm;
	}

	public Mas getMasToUse() {
		return masToUse;
	}

	public void setMasToUse(Mas masToUse) {
		this.masToUse = masToUse;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public List<String> getRoadTypesToInclude() {
		return roadTypesToInclude;
	}

	public void setRoadTypesToInclude(List<String> roadTypesToInclude) {
		this.roadTypesToInclude = roadTypesToInclude;
	}

}
