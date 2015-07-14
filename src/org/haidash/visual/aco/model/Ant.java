package org.haidash.visual.aco.model;

import java.util.List;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.model.entity.Link;
import org.haidash.visual.aco.model.entity.Node;

public interface Ant {

	public List<Link> getPath();

	public IntArrayList getSpentFuelLevel();

	public int getTotalCost();

	public boolean isOutOfFuel();

	public void run();

	public void setStartNode(final Node currentNode);
}
