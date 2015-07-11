package org.haidash.visual.aco.oop;

import java.util.List;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.oop.entity.Link;

public interface Agentable {

	public List<Link> getPath();

	public IntArrayList getSpentFuelLevel();

	public int getTotalCost();

	public boolean isOutOfFuel();

	public void run();
}
