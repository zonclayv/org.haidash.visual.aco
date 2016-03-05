package org.haidash.visual.aco.reader;

import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.util.Pair;
import org.haidash.visual.aco.ui.Constants;
import org.haidash.visual.aco.ui.model.VisualGraph;
import org.haidash.visual.aco.ui.model.VisualNode;

import java.util.*;

import static org.haidash.visual.aco.ui.Constants.GRID_ITEM_HEIGHT;
import static org.haidash.visual.aco.ui.Constants.GRID_ITEM_WIDTH;

/**
 * Created by haidash on 05.03.16.
 */
public class GraphLocation {

    private final static Random RANDOM = new Random(System.nanoTime());
    public static final int NODE_AREA_WIDTH = GRID_ITEM_HEIGHT * 10;
    public static final int NODE_AREA_HEIGHT = GRID_ITEM_HEIGHT * 5;

    private VisualGraph graph;
    private int maxChildrenCount;

    private Map<Integer, Integer> childrenCount;
    private Map<Integer, List<Node>> levelCount;

    public GraphLocation(VisualGraph graph) {
        this.graph = graph;
        this.childrenCount = new HashMap<>(graph.getGraphSize());
        this.levelCount = new HashMap<>(graph.getGraphSize());

        final VisualNode startNode = graph.getStartNode();
        final HashSet<Node> used = new HashSet<>();
        used.add(startNode);

        fillLevelCount(startNode, 1, used);
    }

    private void fillLevelCount(Node node, int level, Set<Node> used) {

        final List<Link> links = node.getOutgoingLinks();
        final List<Node> children = new ArrayList<>();

        for (Link link : links) {

            final Node child = link.getSecond();

            if (used.contains(child)) {
                continue;
            }

            used.add(child);
            children.add(child);
        }

        int count = children.size();

        if (maxChildrenCount < count) {
            maxChildrenCount = count;
        }

        final Integer value = childrenCount.get(level);
        childrenCount.put(level, value == null ? count : value + count);
        levelCount.put(level, new ArrayList<>());

        for (Node child : children) {
            fillLevelCount(child, level + 1, used);
        }

    }

    public void locate() {

        final Node startNode = graph.getStartNode();
        final int maxWidth = NODE_AREA_WIDTH * maxChildrenCount;

        setLocation(startNode, 0, maxWidth / 2);

        final HashSet<Node> used = new HashSet<>();
        used.add(startNode);

        prepareLocation(startNode, 1, used);
    }

    private void prepareLocation(Node node, int level, Set<Node> used) {

        final int childrenCount = this.childrenCount.get(level);

        if (childrenCount == 0) {
            return;
        }

        final int maxWidth = NODE_AREA_WIDTH * maxChildrenCount;
        final int width = maxWidth / childrenCount;

        final List<Link> links = node.getOutgoingLinks();
        final List<Node> children = new ArrayList<>();

        for (Link link : links) {

            final Node child = link.getSecond();

            if (used.contains(child)) {
                continue;
            }

            final List<Node> added = levelCount.get(level);
            final int size = added.size();

            setLocation(child, level + 1, (size + 1) * width - width / 2);

            used.add(child);
            children.add(child);
            added.add(child);
        }

        for (Node child : children) {
            prepareLocation(child, level + 1, used);
        }
    }

    private void setLocation(Node node, int level, int offset) {

        final Double alpha = (-0.9 + (1.8) * RANDOM.nextDouble());
        final Double beta = (-0.9 + (1.8) * RANDOM.nextDouble());

        final Pair<Integer, Integer> location = node.getLocation();
        location.first = (level == 0) ? offset : offset + (int) (GRID_ITEM_WIDTH * 2 * alpha);
        location.second = (level == 0) ? GRID_ITEM_HEIGHT : (NODE_AREA_HEIGHT * level) + (int) (1 * GRID_ITEM_HEIGHT * beta);

        if (location.first > graph.getMaxX()) {
            graph.setMaxX(location.first);
        }

        if (location.second > graph.getMaxY()) {
            graph.setMaxY(location.second);
        }
    }

}
