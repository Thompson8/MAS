package hu.mas.core.util;

import java.util.Objects;

public class MutablePair<L, R> {

	private L left;

	private R rigth;

	public MutablePair(L left, R rigth) {
		this.left = left;
		this.rigth = rigth;
	}

	public L getLeft() {
		return left;
	}

	public void setLeft(L left) {
		this.left = left;
	}

	public R getRigth() {
		return rigth;
	}

	public void setRigth(R rigth) {
		this.rigth = rigth;
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
		if (!(obj instanceof MutablePair)) {
			return false;
		}
		MutablePair<?, ?> other = (MutablePair<?, ?>) obj;
		return Objects.equals(left, other.left) && Objects.equals(rigth, other.rigth);
	}

	@Override
	public String toString() {
		return "Pair [left=" + left + ", rigth=" + rigth + "]";
	}

}
