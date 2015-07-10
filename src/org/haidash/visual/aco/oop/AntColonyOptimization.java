package org.haidash.visual.aco.oop;

import org.haidash.visual.aco.oop.entity.Graph;

public interface AntColonyOptimization {

	public void run(final Graph graph);

	public void updatePheromones(final Agentable agent);
}
