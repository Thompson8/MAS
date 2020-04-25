package hu.mas.core.mas.model.graph;

public class InternalEdge extends AbstractEdge {

	private Edge from;

	private Edge to;

	public InternalEdge(String id, Edge from, Edge to, double speed, double length) {
		super(id, speed, length);
		if (from == null || to == null) {
			throw new IllegalArgumentException();
		}
		this.from = from;
		this.to = to;
	}

	public Edge getFrom() {
		return from;
	}

	public void setFrom(Edge from) {
		this.from = from;
	}

	public Edge getTo() {
		return to;
	}

	public void setTo(Edge to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return "InternalEdge [id=" + id + ", from=" + from.getId() + ", to=" + to.getId() + ", speed=" + speed
				+ ", length=" + length + ", weigth=" + weigth + "]";
	}

}
