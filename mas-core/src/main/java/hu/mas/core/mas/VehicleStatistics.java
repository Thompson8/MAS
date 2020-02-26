package hu.mas.core.mas;

public class VehicleStatistics {

	private Integer start;

	private Integer agentStartMessage;

	private Integer finish;

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getFinish() {
		return finish;
	}

	public void setFinish(Integer finish) {
		this.finish = finish;
	}

	public Integer getAgentStartMessage() {
		return agentStartMessage;
	}

	public void setAgentStartMessage(Integer agentStartMessage) {
		this.agentStartMessage = agentStartMessage;
	}

	@Override
	public String toString() {
		return "VehicleStatistics [start=" + start + ", agentStartMessage=" + agentStartMessage + ", finish=" + finish
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agentStartMessage == null) ? 0 : agentStartMessage.hashCode());
		result = prime * result + ((finish == null) ? 0 : finish.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		if (agentStartMessage == null) {
			if (other.agentStartMessage != null)
				return false;
		} else if (!agentStartMessage.equals(other.agentStartMessage)) {
			return false;
		}
		if (finish == null) {
			if (other.finish != null)
				return false;
		} else if (!finish.equals(other.finish)) {
			return false;
		}
		if (start == null) {
			if (other.start != null) {
				return false;
			}
		} else if (!start.equals(other.start)) {
			return false;
		}
		return true;
	}

}
