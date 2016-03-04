package org.haidash.visual.aco.model.impl;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.model.Agent;
import org.haidash.visual.aco.model.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.haidash.visual.aco.model.ACOUtils.findCycle;

/**
 * Created by zonclayv on 01.08.15.
 */
public class ScoutAnt implements Agent {

    private final static Random RANDOM = new Random(System.nanoTime());

    private static final ACOParameters ACO_PARAMETERS = ACOParameters.INSTANCE;

    private final Graph graph;

    private final List<Link> path;
    private final IntArrayList spentFuelLevel;
    private final IntArrayList tempFuelLevel;

    private final Node startNode;

    private Node currentNode;

    private int fuelBalance = ACO_PARAMETERS.getMaxFuelLevels();
    private int totalCost = 0;

    private boolean outOfFuel = false;

    public ScoutAnt(final Graph graph) {
        this.graph = graph;

        final int nodeIndex = getNodeIndex(graph);

        this.startNode = graph.getNodes().get(nodeIndex);
        this.currentNode = startNode;
        this.tempFuelLevel = new IntArrayList(graph.getFuelLevels());
        this.spentFuelLevel = new IntArrayList();
        this.path = new ArrayList<>();
    }

    private int getNodeIndex(Graph graph) {

        final Node startNode = graph.getStartNode();
        final int startNodeIndex = startNode.getNumber();

        int index;

        do {

            index = RANDOM.nextInt(graph.getGraphSize());

        } while (index == startNodeIndex);

        return index;
    }

    @Override
    public List<Link> getPath() {
        return path;
    }

    @Override
    public IntArrayList getSpentFuelLevel() {
        return spentFuelLevel;
    }

    @Override
    public int getTotalCost() {
        return totalCost;
    }

    @Override
    public boolean isOutOfFuel() {
        return outOfFuel;
    }

    @Override
    public void run() {
        while ((currentNode != graph.getTargetNode()) && !outOfFuel) {
            selectNode();
        }

        if (!outOfFuel) {
            startNode.addProperty("Path", path);
        }
    }

    private void selectNode() {

        final List<ReachableLink> reachableLinks = findReachableLinks();

        final Node targetNode = graph.getTargetNode();

        if (reachableLinks.isEmpty() && (currentNode == targetNode)) {
            return;
        }

        if (reachableLinks.isEmpty() || outOfFuel) {
            outOfFuel = true;
            return;
        }

        double randomBound = 0d;

        for (ReachableLink link : reachableLinks) {
            randomBound += link.getValue();
        }

        final int rand = RANDOM.nextInt((int) Math.ceil(randomBound));

        double roulette = 0.0;

        for (final ReachableLink reachableLink : reachableLinks) {

            final double probability = reachableLink.getValue();

            roulette += probability;

            if (roulette < rand) {
                continue;
            }

            final int usedFuel = getAvailableFuel() - fuelBalance;
            final Link link = reachableLink.getLink();

            addNextNode(usedFuel, link);

            return;
        }

        outOfFuel = true;
    }

    private List<ReachableLink> findReachableLinks() {

        final List<ReachableLink> reachableLinks = new ArrayList<>();

        for (Link link : currentNode.getOutgoingLinks()) {

            final int availableFuel = getAvailableFuel();

            if (availableFuel < link.getWeight()) {
                continue;
            }

            final double probability = 1d / link.getWeight();

            reachableLinks.add(new ReachableLink(link, probability));
        }

        return reachableLinks;
    }

    public void addNextNode(final int usedFuel, final Link link) {

        // 1
        path.add(link);
        spentFuelLevel.add(usedFuel);

        final int fuel = tempFuelLevel.get(currentNode.getNumber()) - usedFuel;

        tempFuelLevel.set(currentNode.getNumber(), fuel);

        // 2
        fuelBalance += usedFuel - link.getWeight();
        totalCost += usedFuel;
        currentNode = link.getSecond();
    }

    private int getAvailableFuel() {

        final int fuelInNode = tempFuelLevel.get(currentNode.getNumber());
        final int maxFuelLevels = ACO_PARAMETERS.getMaxFuelLevels();
        final int allFuel = fuelBalance + fuelInNode;

        return allFuel > maxFuelLevels ? maxFuelLevels : allFuel;
    }
}
