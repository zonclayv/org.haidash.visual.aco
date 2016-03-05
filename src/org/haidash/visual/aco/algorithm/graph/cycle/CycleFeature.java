package org.haidash.visual.aco.algorithm.graph.cycle;

import org.haidash.visual.aco.algorithm.graph.entity.Node;

import java.util.Map;

/**
 * Created by zonclayv on 05.03.16.
 */
public interface CycleFeature {
    void addCycle(final Cycle cycle);
    Map<Node, Cycle> getCycles();

}
