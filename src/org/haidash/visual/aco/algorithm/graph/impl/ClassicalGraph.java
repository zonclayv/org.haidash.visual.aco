package org.haidash.visual.aco.algorithm.graph.impl;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;

import java.util.List;
import java.util.Objects;

public class ClassicalGraph implements Graph<Node, Link> {

    private int graphSize;

    private Node startNode;
    private Node targetNode;

    private IntArrayList fuelLevels;

    private List<Node> nodes;
    private List<Link> links;

    public ClassicalGraph() {
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
        final List<Node> nodes = getNodes();
        nodes.forEach(Node::clear);
    }

    @Override
    public IntArrayList getFuelLevels() {
        return fuelLevels;
    }

    @Override
    public int getGraphSize() {
        return graphSize;
    }

    @Override
    public List<Link> getLinks() {
        return links;
    }

    @Override
    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public Node getStartNode() {
        return startNode;
    }

    @Override
    public Node getTargetNode() {
        return targetNode;
    }

    @Override
    public void setFuelLevels(final IntArrayList fuelLevels) {
        this.fuelLevels = fuelLevels;
    }

    @Override
    public void setGraphSize(final int graphSize) {
        this.graphSize = graphSize;
    }

    @Override
    public void setLinks(final List<Link> links) {
        this.links = links;
    }

    @Override
    public void setNodes(final List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Node getNode(final int index) {
        final List<Node> nodes = getNodes();

        for (Node node : nodes) {
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

    @Override
    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    @Override
    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }
}
