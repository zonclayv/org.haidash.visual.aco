package org.haidash.visual.aco.algorithm.agent.impl;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.algorithm.util.ACOParameters;
import org.haidash.visual.aco.algorithm.agent.Agent;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.graph.entity.ReachableLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

/**
 * @author Haidash Aleh
 */
public class ClassicalAnt implements Agent {

    //    private final static Logger LOGGER = Logger.getLogger(ClassicalAnt.class);
    private final static Random RANDOM = new Random(System.nanoTime());

    private static final ACOParameters ACO_PARAMETERS = ACOParameters.INSTANCE;

    private final Graph graph;

    private final List<Link> path;
    private final IntArrayList spentFuelLevel;
    private final IntArrayList tempFuelLevel;

    private Node currentNode;

    private int fuelBalance = 0;
    private int totalCost = 0;

    private boolean outOfFuel = false;

    public ClassicalAnt(final Graph graph) {
        this.graph = graph;

        this.currentNode = graph.getStartNode();
        this.tempFuelLevel = new IntArrayList(graph.getFuelLevels());
        this.spentFuelLevel = new IntArrayList();
        this.path = new ArrayList<>();
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

    private List<ReachableLink> findReachableLinks() {

        final List<ReachableLink> reachableLinks = new ArrayList<>();

        double sum = -1.0;

        for (Link link : currentNode.getOutgoingLinks()) {

            final int availableFuel = getAvailableFuel();

            if (availableFuel < link.getWeight()) {
                continue;
            }

            final double powAlphaValue = pow(link.getWeight(), ACO_PARAMETERS.getBeta().get());
            final double etaCost = ACO_PARAMETERS.getQ().get() / powAlphaValue;

            final double tau = link.getPPheromone();

            if (sum == -1.0) {
                sum = getSumProbabilities();
            }

            final double probability = 100 * ((pow(tau, ACO_PARAMETERS.getAlpha().get()) * etaCost) / sum);

//            LOGGER.debug("Chance "
//                    + currentNode
//                    + "->"
//                    + link.getSecond()
//                    + " etaCost: "
//                    + etaCost
//                    + " etaRemaning: "
//                    + etaRemaning
//                    + " etaVisits: "
//                    + etaVisits
//                    + " tau: "
//                    + tau
//                    + " probability: "
//                    + probability);

            reachableLinks.add(new ReachableLink(link, probability));
        }

        return reachableLinks;
    }

    private int getAvailableFuel() {

        final int fuelInNode = tempFuelLevel.get(currentNode.getNumber());
        final int maxFuelLevels = ACO_PARAMETERS.getMaxFuelLevels();
        final int allFuel = fuelBalance + fuelInNode;

        return allFuel > maxFuelLevels ? maxFuelLevels : allFuel;
    }

    @Override
    public List<Link> getPath() {
        return path;
    }

    @Override
    public IntArrayList getSpentFuelLevel() {
        return spentFuelLevel;
    }

    private double getSumProbabilities() {

        double sum = 0.0;

        for (Link link : currentNode.getOutgoingLinks()) {

            final int availableFuel = getAvailableFuel();

            if (availableFuel < link.getWeight()) {
                continue;
            }

            final double powAlphaValue = pow(link.getWeight(), ACO_PARAMETERS.getBeta().get());
            final double etaCost = ACO_PARAMETERS.getQ().get() / powAlphaValue;

            final double tau = link.getPPheromone();

            sum += pow(tau, ACO_PARAMETERS.getAlpha().get()) * etaCost;
        }

        return sum;
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

        final int rand = RANDOM.nextInt((int) randomBound);

        double roulette = 0.0;

        for (final ReachableLink reachableLink : reachableLinks) {

            final double probability = reachableLink.getValue();

            roulette += probability;

            if (roulette < rand) {
                continue;
            }

            final Link link = reachableLink.getLink();
            final int usedFuel = getAvailableFuel() - fuelBalance;

            addNextNode(usedFuel, link);

            return;
        }

        outOfFuel = true;
    }
}
