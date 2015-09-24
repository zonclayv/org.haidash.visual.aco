package org.haidash.visual.aco.model.entity;

import com.carrotsearch.hppc.IntArrayList;

import java.util.*;

public class Graph {

    private int graphSize;

    private Node startNode;
    private Node targetNode;

    private IntArrayList fuelLevels;

    private List<Link> links;
    private List<Node> nodes;

    private final Map<Node, Cycle> cycles;
    private final Set<List<Link>> badPaths;

    public Graph() {
        this.cycles = new HashMap<>();
        this.badPaths = new HashSet<>();
    }

    public void addCycle(final Cycle cycle) {

        final Cycle oldCycle = cycles.get(cycle.getStartNode());

        if (oldCycle != null) {

            final int oldCycleFuel = oldCycle.getFuel();
            final int cycleFuel = cycle.getFuel();

            if ((oldCycleFuel > cycleFuel)) {
                return;
            }

            if (!((oldCycleFuel == cycleFuel) && (oldCycle.getLinks().size() > cycle.getLinks().size()))) {
                return;
            }
        }

        cycles.put(cycle.getStartNode(), cycle);
    }

    public Set<List<Link>> getBadPaths() {
        return badPaths;
    }

    public Map<Node, Cycle> getCycles() {
        return cycles;
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

    public boolean isBadPath(final List<Link> path, final Link nextArc) {

        final List<Link> nodes = new ArrayList<>(path);
        nodes.add(nextArc);

        return badPaths.contains(nodes);
    }

    public boolean isBadPath(final List<Link> path, final List<Link> cycleArcs, final Link nextArc) {

        final List<Link> nodes = new ArrayList<>(path);
        nodes.addAll(cycleArcs);
        nodes.add(nextArc);

        return badPaths.contains(nodes);
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

    public void setStartNode(final Node startNode) {
        this.startNode = startNode;
    }

    public void setTargetNode(final Node targetNode) {
        this.targetNode = targetNode;
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
}
