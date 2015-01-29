package org.haidash.visual.aco.algorithm.model;

import java.util.List;

public class Cycle {

	private int startNode;
	private List<Integer> nodes;
	private int fuel;

	public Cycle() {
	}

	public int getFuel() {
		return fuel;
	}

	public int getStartNode() {
		return startNode;
	}

	public List<Integer> getNodes() {
		return nodes;
	}

	public void setNodes(List<Integer> nodes) {
		this.nodes = nodes;
	}

	public void setFuel(final int fuel) {
		this.fuel = fuel;
	}

	public void setStartNode(int startNode) {
		this.startNode = startNode;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + fuel;
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		result = prime * result + startNode;

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

		Cycle other = (Cycle) obj;

		if (fuel != other.fuel)
			return false;

		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;

		if (startNode != other.startNode)
			return false;

		return true;
	}
}
