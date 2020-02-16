package hu.mas.core.util;

public class Pair<L, R> {

	private final L left;
	
	private final R rigth;

	public Pair(L left, R rigth) {
		super();
		this.left = left;
		this.rigth = rigth;
	}

	public L getLeft() {
		return left;
	}

	public R getRigth() {
		return rigth;
	}

	@Override
	public String toString() {
		return "Pair [left=" + left + ", rigth=" + rigth + "]";
	}
	
}
