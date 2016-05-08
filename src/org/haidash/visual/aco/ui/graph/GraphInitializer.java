package org.haidash.visual.aco.ui.graph;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.util.Pair;

import java.util.*;

import static org.haidash.visual.aco.ui.Constants.CIRCLE_RADIUS;
import static org.haidash.visual.aco.ui.Constants.GRID_ITEM_HEIGHT;
import static org.haidash.visual.aco.ui.Constants.GRID_ITEM_WIDTH;

/**
 * Created by haidash on 05.03.16.
 */
public class GraphInitializer {

    public final static int NODE_AREA_WIDTH = GRID_ITEM_WIDTH * 10;
    public final static int NODE_AREA_HEIGHT = GRID_ITEM_HEIGHT * 5;

    private final static Random RANDOM = new Random(System.nanoTime());

    private int maxX;
    private int maxY;

    private Graph graph;
    private int maxChildrenCount;

    private Map<Integer, Integer> childrenMap;
    private Map<Integer, List<Node>> levelCount;

    public GraphInitializer(Graph graph) {
        this.graph = graph;
        this.childrenMap = new HashMap<>(graph.getGraphSize());
        this.levelCount = new HashMap<>(graph.getGraphSize());

        final Node startNode = graph.getStartNode();
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

        final Integer value = childrenMap.get(level);
        childrenMap.put(level, value == null ? count : value + count);
        levelCount.put(level, new ArrayList<>());

        for (Node child : children) {
            fillLevelCount(child, level + 1, used);
        }

    }

    public void init() {

        final List<Link> links = graph.getLinks();
        links.forEach(this::initLinkView);

        final Node startNode = graph.getStartNode();

        setPosition(startNode, 0, NODE_AREA_WIDTH * maxChildrenCount / 2);

        final HashSet<Node> used = new HashSet<>();
        used.add(startNode);

        initPosition(startNode, 1, used);

    }

    private void initPosition(Node node, int level, Set<Node> used) {

        final int childrenCount = childrenMap.get(level);

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

            setPosition(child, level + 1, (size + 1) * width - width / 2);

            used.add(child);
            children.add(child);
            added.add(child);
        }

        for (Node child : children) {
            initPosition(child, level + 1, used);
        }
    }


    private void setPosition(Node node, int level, int offset) {

        final NodeView nodeView = node.getView();

        final Double alpha = (-0.9 + (1.8) * RANDOM.nextDouble());
        final Double beta = (-0.9 + (1.8) * RANDOM.nextDouble());

        final int x = (level == 0) ? offset : offset + (int) (GRID_ITEM_WIDTH * alpha);
        final int y = (level == 0) ? GRID_ITEM_HEIGHT : (NODE_AREA_HEIGHT * level) + (int) (GRID_ITEM_HEIGHT * beta);

        if (x > maxX) {
            maxX = x;
        }

        if (y > maxY) {
            maxY = y;
        }

        nodeView.setPosition(new Pair<>(x, y));
    }

    public int getMaxX() {
        return maxX + 2 * GRID_ITEM_WIDTH;
    }

    public int getMaxY() {
        return maxY + 2 * GRID_ITEM_HEIGHT;
    }

    public void initLinkView(final Link link) {
        final LinkView view = new LinkView(link);
        link.setView(view);

        final Node first = link.getFirst();
        final Node second = link.getSecond();

        initNodeView(first);
        initNodeView(second);

        final Line line = view.getLine();
        final Pane firstPane = first.getView().getPane();
        final Pane secondPane = second.getView().getPane();

        line.startXProperty().bind(firstPane.layoutXProperty().add(CIRCLE_RADIUS));
        line.startYProperty().bind(firstPane.layoutYProperty().add(CIRCLE_RADIUS));

        line.endXProperty().bind(secondPane.layoutXProperty().add(CIRCLE_RADIUS));
        line.endYProperty().bind(secondPane.layoutYProperty().add(CIRCLE_RADIUS));
    }

    private void initNodeView(final Node node) {

        if (node.getView() != null) {
            return;
        }

        final NodeView view = new NodeView(node);
        node.setView(view);
    }
}
