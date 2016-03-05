package org.haidash.visual.aco.ui.model;

import com.carrotsearch.hppc.IntArrayList;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.ui.GraphChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseButton.SECONDARY;

/**
 * Created by zonclayv on 05.03.16.
 */
public class VisualGraph implements Graph<VisualNode, VisualLink> {

    private int maxX;
    private int maxY;

    private int graphSize;

    private VisualNode startNode;
    private VisualNode targetNode;

    private IntArrayList fuelLevels;

    private List<VisualNode> nodes;
    private List<VisualLink> links;

    private List<GraphChangeListener> listeners;

    public VisualGraph() {
        this.listeners = new ArrayList<>();
    }

    public void addListener(GraphChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GraphChangeListener listener) {
        listeners.remove(listener);
    }

    public void fireListener() {
        listeners.forEach(GraphChangeListener::graphChanged);
    }


    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public void fillCircle(final VisualNode node) {

        final Node startNode = getStartNode();
        final Node targetNode = getTargetNode();

        final Circle circle = node.getCircle();

        if (Objects.equals(startNode, node)) {
            circle.setStroke(Color.GREY);
            circle.setFill(Color.GREEN);
        } else if (Objects.equals(targetNode, node)) {
            circle.setStroke(Color.GREY);
            circle.setFill(Color.RED);
        } else {
            circle.setStroke(Color.GREY);
            circle.setFill(Color.WHITESMOKE);
        }
    }

    @Override
    public boolean isReady() {

        if (graphSize == 0) {
            return false;
        }

        if (Objects.equals(startNode, targetNode)) {
            return false;
        }

        return startNode != null && targetNode != null;
    }

    public void clear() {
        final List<VisualNode> nodes = getNodes();
        nodes.forEach(Node::clear);
    }

    @Override
    public IntArrayList getFuelLevels() {
        return fuelLevels;
    }

    @Override
    public void setFuelLevels(final IntArrayList fuelLevels) {
        this.fuelLevels = fuelLevels;
    }

    @Override
    public int getGraphSize() {
        return graphSize;
    }

    @Override
    public void setGraphSize(final int graphSize) {
        this.graphSize = graphSize;
    }

    @Override
    public List<VisualLink> getLinks() {
        return links;
    }

    @Override
    public void setLinks(final List<VisualLink> links) {
        this.links = links;
    }

    @Override
    public List<VisualNode> getNodes() {
        return nodes;
    }

    @Override
    public void setNodes(final List<VisualNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public VisualNode getStartNode() {
        return startNode;
    }

    @Override
    public void setStartNode(VisualNode startNode) {
        this.startNode = startNode;
    }

    @Override
    public VisualNode getTargetNode() {
        return targetNode;
    }

    @Override
    public void setTargetNode(VisualNode targetNode) {
        this.targetNode = targetNode;
    }

    @Override
    public VisualNode getNode(final int index) {
        final List<VisualNode> nodes = getNodes();

        for (VisualNode node : nodes) {
            if (node.getNumber() == index) {
                return node;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "Graph size - "
                + graphSize
                + "\n"
                + "Start node - "
                + startNode.getNumber()
                + "\n"
                + "Target node - "
                + targetNode.getNumber()
                + "\n"
                + "Nodes: \n"
                + nodes.toString()
                + "\n"
                + "Arcs: \n"
                + links.toString();
    }

    public void changeVertexStatus(final MouseButton btn, final VisualNode node) {

        final VisualNode startNode = getStartNode();
        final VisualNode targetNode = getTargetNode();

        if (btn.equals(PRIMARY)) {

            if (Objects.equals(node, targetNode)) {
                setTargetNode(startNode);
            }

            setStartNode(node);
        } else if (btn.equals(SECONDARY)) {

            if (Objects.equals(node, startNode)) {
                setStartNode(targetNode);
            }

            setTargetNode(node);
        } else {
            return;
        }

        fillCircle(startNode);
        fillCircle(targetNode);
        fillCircle(getStartNode());
        fillCircle(getTargetNode());
    }
}
