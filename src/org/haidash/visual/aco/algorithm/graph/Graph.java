package org.haidash.visual.aco.algorithm.graph;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.ui.GraphChangeListener;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Graph {

    private String graphName;
    private Path location;

    private int graphSize;

    private Node startNode;
    private Node targetNode;

    private IntArrayList fuelLevels;

    private List<Node> nodes;
    private List<Link> links;

    private List<GraphChangeListener> listeners;

    public Graph() {
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
        final List<Link> links = getLinks();
        links.forEach(Link::clear);
    }

    public IntArrayList getFuelLevels() {
        return fuelLevels;
    }

    public int getGraphSize() {
        return graphSize;
    }

    public List<Link> getLinks() {
        return links;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setFuelLevels(final IntArrayList fuelLevels) {
        this.fuelLevels = fuelLevels;
    }

    public void setGraphSize(final int graphSize) {
        this.graphSize = graphSize;
    }

    public void setLinks(final List<Link> links) {
        this.links = links;
    }

    public void setNodes(final List<Node> nodes) {
        this.nodes = nodes;
    }

    public Node getNode(final int index) {
        final List<Node> nodes = getNodes();

        for (Node node : nodes) {
            if (node.getNumber() == index) {
                return node;
            }
        }

        return null;
    }

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

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Path getLocation() {
        return location;
    }

    public void setLocation(Path location) {
        this.location = location;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }
}
