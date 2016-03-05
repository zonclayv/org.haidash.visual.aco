package org.haidash.visual.aco.algorithm.graph;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;

import java.util.List;

/**
 * Created by zonclayv on 05.03.16.
 */
public interface Graph<T extends Node, P extends Link> {
    boolean isReady();

    void clear();

    IntArrayList getFuelLevels();

    int getGraphSize();

    List<P> getLinks();

    List<T> getNodes();

    Node getStartNode();

    Node getTargetNode();

    void setFuelLevels(final IntArrayList fuelLevels);

    void setGraphSize(final int graphSize);

    void setLinks(final List<P> links);

    void setNodes(final List<T> nodes);

    void setStartNode(T startNode);

    void setTargetNode(final T targetNode);

    Node getNode(final int index);


}
