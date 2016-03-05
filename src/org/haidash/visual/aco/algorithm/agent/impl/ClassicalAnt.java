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
    private final IntArrayList remainsFuel;

    private final List<Link> path;
    private final IntArrayList spentFuelLevel;
    private final IntArrayList tempFuelLevel;

    private Node currentNode;

    private int fuelBalance = 0;
    private int totalCost = 0;

    private boolean outOfFuel = false;

    public ClassicalAnt(final Graph graph, final IntArrayList remainsFuel) {
        this.graph = graph;
        this.remainsFuel = remainsFuel;

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

            if (availableFuel >= link.getWeight()) {
                continue;
            }

            final double powBetaValue = pow(link.getVisitsCount(), ACO_PARAMETERS.getBeta().get());
            final double powAlphaValue = pow(link.getWeight(), ACO_PARAMETERS.getBeta().get());
            final double etaVisits = link.getVisitsCount() == 0 ? ACO_PARAMETERS.getNumAnts().get() : (ACO_PARAMETERS.getNumAnts().get() / powBetaValue);
            final double etaCost = ACO_PARAMETERS.getQ().get() / powAlphaValue;

            double etaRemaning;

            final double k = (availableFuel - link.getWeight()) + remainsFuel.get(link.getSecond().getNumber());

            if (k > 0) {
                etaRemaning = ACO_PARAMETERS.getMaxFuelLevels() / pow(k, ACO_PARAMETERS.getBeta().get());
            } else if (k < 0) {
                double tempK = 1 / pow(abs(k), ACO_PARAMETERS.getBeta().get());
                tempK = tempK == 1 ? 0 : tempK;
                etaRemaning = -1 * (1 - tempK);
            } else {
                etaRemaning = ACO_PARAMETERS.getMaxFuelLevels();
            }

            double eta = (0.3 * etaCost) + (0.3 * etaRemaning) + (0.3 * etaVisits);

            if (eta < 0) {
                eta = 1;
            }

            final double tau = link.getPPheromone() / link.getNPheromone();

            if (sum == -1.0) {
                sum = getSumProbabilities();
            }

            final double probability = 100 * ((pow(tau, ACO_PARAMETERS.getAlpha().get()) * eta) / sum);

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

            if (availableFuel >= link.getWeight()) {
                continue;
            }

            final double powBetaValue = pow(link.getVisitsCount(), ACO_PARAMETERS.getBeta().get());
            final double powAlphaValue = pow(link.getWeight(), ACO_PARAMETERS.getBeta().get());
            final double etaVisits = link.getVisitsCount() == 0 ? ACO_PARAMETERS.getNumAnts().get() : (ACO_PARAMETERS.getNumAnts().get() / powBetaValue);
            final double etaCost = ACO_PARAMETERS.getQ().get() / powAlphaValue;

            double etaRemaning;

            final double k = (availableFuel - link.getWeight()) + remainsFuel.get(link.getSecond().getNumber());

            if (k > 0) {
                etaRemaning = ACO_PARAMETERS.getMaxFuelLevels() / pow(k, ACO_PARAMETERS.getBeta().get());
            } else if (k < 0) {
                double tempK = 1 / pow(abs(k), ACO_PARAMETERS.getBeta().get());
                tempK = tempK == 1 ? 0 : tempK;
                etaRemaning = -1 * (1 - tempK);
            } else {
                etaRemaning = ACO_PARAMETERS.getMaxFuelLevels();
            }

            double eta = (0.3 * etaCost) + (0.3 * etaRemaning) + (0.3 * etaVisits);

            if (eta < 0) {
                eta = 1;
            }

            final double tau = link.getPPheromone() / link.getNPheromone();

            sum += pow(tau, ACO_PARAMETERS.getAlpha().get()) * eta;
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
