package org.haidash.visual.aco.algorithm.model;

public class ReachableNode {

	private final int node;
	private final double value;

	public ReachableNode(final int node, final double value) {
		this.node = node;
		this.value = value;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ReachableNode other = (ReachableNode) obj;
		if (node != other.node) {
			return false;
		}
		if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
			return false;
		}
		return true;
	}

	public int getNode() {
		return node;
	}

	public double getProbability() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + node;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		return result;
	}

}
