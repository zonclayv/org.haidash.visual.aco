package org.haidash.visual.aco.model;

import org.haidash.visual.aco.model.entity.Graph;

public interface Colony {

	public void run(final Graph graph);

	public void updatePheromones(final Ant agent);
}
