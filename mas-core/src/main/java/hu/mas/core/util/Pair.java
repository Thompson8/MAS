package hu.mas.core.util;

import java.util.Objects;

public class Pair<L, R> {

	private final L left;
	
	private final R rigth;

	public Pair(L left, R rigth) {
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
	public int hashCode() {
		return Objects.hash(left, rigth);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Pair)) {
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) obj;
		return Objects.equals(left, other.left) && Objects.equals(rigth, other.rigth);
	}

	@Override
	public String toString() {
		return "Pair [left=" + left + ", rigth=" + rigth + "]";
	}
	
}
