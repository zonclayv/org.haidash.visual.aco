package org.haidash.visual.aco.oop;

import java.util.List;

import org.haidash.visual.aco.oop.entity.Link;

public interface Agentable {

	public List<Link> getPath();

	public List<Integer> getSpentFuelLevel();

	public int getTotalCost();

	public boolean isOutOfFuel();

	public void run();
}
