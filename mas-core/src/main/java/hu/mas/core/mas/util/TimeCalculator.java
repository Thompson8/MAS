package hu.mas.core.mas.util;

public class TimeCalculator {

	public static double calculateTime(int iteration, double stepLength) {
		return iteration * stepLength;
	}

	private TimeCalculator() {
	}

}
