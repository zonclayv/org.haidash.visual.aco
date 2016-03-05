package org.haidash.visual.aco.reader;

import org.haidash.visual.aco.algorithm.aco.entity.Graph;
import org.haidash.visual.aco.algorithm.aco.entity.Link;
import org.haidash.visual.aco.algorithm.aco.entity.Node;
import org.haidash.visual.aco.algorithm.aco.entity.Pair;
import org.haidash.visual.aco.ui.pane.GraphPane;

import java.util.*;

/**
 * Created by haidash on 05.03.16.
 */
public class GraphLocation {

    private Graph graph;
    private int maxChildrenCount;

    private Map<Node, Integer> levelCount;

    public GraphLocation(Graph graph) {
        this.graph = graph;
        this.levelCount = new HashMap<>();

        fillLevelCount(graph.getStartNode(), new HashSet<>());
    }

    private void fillLevelCount(Node node, Set<Node> used) {

        used.add(node);

        final List<Link> links = node.getOutgoingLinks();

        int count = 0;

        for (Link link : links) {

            final Node child = link.getSecond();

            if (used.contains(child)) {
                continue;
            }

            count++;
        }

        if (maxChildrenCount < count) {
            maxChildrenCount = count;
        }

        levelCount.put(node, count);


        for (Link link : links) {

            final Node child = link.getSecond();

            if (used.contains(child)) {
                continue;
            }

            fillLevelCount(child, used);
        }

    }

    public void locate() {

        final Node startNode = graph.getStartNode();
        final int maxWidth = GraphPane.GRID_ITEM_HEIGHT * 10 * maxChildrenCount;

        setLocation(startNode, 0, maxWidth / 2);

        final HashSet<Node> used = new HashSet<>();
        used.add(startNode);

        prepareLocation(startNode, 1, used);
    }

    private void prepareLocation(Node node, int level, Set<Node> used) {

        final int childrenCount = levelCount.get(node);

        if (childrenCount == 0) {
            return;
        }

        final int maxWidth = GraphPane.GRID_ITEM_HEIGHT * 10 * maxChildrenCount;
        final int width = maxWidth / childrenCount;

        final List<Link> links = node.getOutgoingLinks();
        final List<Node> children = new ArrayList<>();

        int count = 1;
        for (Link link : links) {

            final Node child = link.getSecond();

            if (used.contains(child)) {
                continue;
            }

            setLocation(child, level + 1, (count - 1) * width + width / 2);

            used.add(child);
            children.add(child);

            count++;
        }

        for (Node child : children) {
            prepareLocation(child, level + 1, used);
        }
    }

    private void setLocation(Node node, int level, int offset) {

        final Pair<Integer, Integer> location = node.getLocation();
        location.first = offset;
        location.second = level == 0 ? GraphPane.GRID_ITEM_HEIGHT : GraphPane.GRID_ITEM_HEIGHT * level * 5;

        if (location.first > graph.getMaxX()) {
            graph.setMaxX(location.first);
        }

        if (location.second > graph.getMaxY()) {
            graph.setMaxY(location.second);
        }
    }
}
