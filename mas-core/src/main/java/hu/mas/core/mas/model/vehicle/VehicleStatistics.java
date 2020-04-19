package hu.mas.core.mas.model.vehicle;

public class VehicleStatistics {

	private Double actualStart;

	private Double actualFinish;

	private Double agentStart;

	public Double getActualStart() {
		return actualStart;
	}

	public void setActualStart(Double actualStart) {
		this.actualStart = actualStart;
	}

	public Double getActualFinish() {
		return actualFinish;
	}

	public void setActualFinish(Double actualFinish) {
		this.actualFinish = actualFinish;
	}

	public Double getAgentStart() {
		return agentStart;
	}

	public void setAgentStart(Double agentStart) {
		this.agentStart = agentStart;
	}

	@Override
	public String toString() {
		return "VehicleStatistics [actualStartIteration=" + actualStart + ", agentStartMessage=" + agentStart
				+ ", actualFinishIteration=" + actualFinish + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agentStart == null) ? 0 : agentStart.hashCode());
		result = prime * result + ((actualFinish == null) ? 0 : actualFinish.hashCode());
		result = prime * result + ((actualStart == null) ? 0 : actualStart.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VehicleStatistics other = (VehicleStatistics) obj;
		if (agentStart == null) {
			if (other.agentStart != null)
				return false;
		} else if (!agentStart.equals(other.agentStart)) {
			return false;
		}
		if (actualFinish == null) {
			if (other.actualFinish != null)
				return false;
		} else if (!actualFinish.equals(other.actualFinish)) {
			return false;
		}
		if (actualStart == null) {
			if (other.actualStart != null) {
				return false;
			}
		} else if (!actualStart.equals(other.actualStart)) {
			return false;
		}
		return true;
	}

}
