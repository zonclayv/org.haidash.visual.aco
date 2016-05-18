package org.haidash.visual.aco.algorithm.agent.impl;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.algorithm.util.ACOParameters;
import org.haidash.visual.aco.algorithm.agent.Agent;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.cycle.Cycle;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.graph.entity.ReachableLink;

import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static org.haidash.visual.aco.algorithm.graph.badPath.BadPathUtil.isBadPath;
import static org.haidash.visual.aco.algorithm.graph.cycle.CycleUtil.addCycle;
import static org.haidash.visual.aco.algorithm.graph.cycle.CycleUtil.findCycle;

/**
 * @author Haidash Aleh
 */
public class CycleAndBadPathAnt implements Agent {

    //    private final static Logger LOGGER = Logger.getLogger(CycleAndBadPathAnt.class);
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

    private final Map<Node, Cycle> cycles;
    private final Set<List<Link>> badPaths;

    public CycleAndBadPathAnt(final Graph graph, final IntArrayList remainsFuel, Map<Node, Cycle> cycles, Set<List<Link>> badPaths) {
        this.graph = graph;
        this.remainsFuel = remainsFuel;

        this.cycles = cycles;
        this.badPaths = badPaths;

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

    private boolean applyPath(final Cycle cycle) {

        if (cycle == null) {
            return false;
        }

        for (Link link : cycle.getLinks()) {

            final int fuelCost = link.getWeight();
            final int availableFuel = getAvailableFuel();

            if (availableFuel < fuelCost) {
                outOfFuel = true;
                return false;
            }

            final int usedFuel = availableFuel - fuelBalance;

            addNextNode(usedFuel, link);
        }

        return true;
    }

    private List<ReachableLink> findReachableLinks() {

        final List<ReachableLink> reachableLinks = new ArrayList<>();
        final Cycle cycle = cycles.get(currentNode);

        double sum = -1.0;

        for (Link link : currentNode.getOutgoingLinks()) {

            final int availableFuel = getAvailableFuel();

            if (availableFuel >= link.getWeight()) {
                if (isBadPath(badPaths, path, link)) {
                    continue;
                }
            } else {
                if (cycle == null) {
                    continue;
                }

                final int fuelAfterCycle = getFuelAfterCycle(availableFuel, cycle.getFuel());

                if ((fuelAfterCycle < link.getWeight()) || isBadPath(badPaths, path, cycle.getLinks(), link)) {
                    continue;
                }
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
                sum = getSumProbabilities(cycle);
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

    private int getFuelAfterCycle(final int availableFuel, final int fuelInCycle) {

        final int fuelInNode = tempFuelLevel.get(currentNode.getNumber());
        final int maxFuelLevels = ACO_PARAMETERS.getMaxFuelLevels();
        final int allFuel = fuelBalance + fuelInNode;

        int tempFuel;

        if (allFuel > maxFuelLevels) {
            tempFuel = maxFuelLevels - availableFuel;
        } else {
            tempFuel = allFuel - availableFuel;
        }

        return tempFuel + fuelInCycle;
    }

    @Override
    public List<Link> getPath() {
        return path;
    }

    @Override
    public IntArrayList getSpentFuelLevel() {
        return spentFuelLevel;
    }

    private double getSumProbabilities(final Cycle cycle) {

        double sum = 0.0;

        for (Link link : currentNode.getOutgoingLinks()) {

            final int availableFuel = getAvailableFuel();

            if (availableFuel >= link.getWeight()) {
                if (isBadPath(badPaths, path, link)) {
                    continue;
                }
            } else {
                if (cycle == null) {
                    continue;
                }

                final int fuelAfterCycle = availableFuel + cycle.getFuel();
                final List<Link> links = cycle.getLinks();

                if (link.equals(links.get(0)) || (fuelAfterCycle < link.getWeight()) || isBadPath(badPaths, path, links, link)) {
                    continue;
                }
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

        final Object property = currentNode.getProperty("Path");

        if (property != null && RANDOM.nextBoolean()) {

            final List<Link> links = (List<Link>) property;

            for (Link link : links) {

                final int futureFuelBalance = getAvailableFuel() - link.getWeight();

                if (futureFuelBalance < 0 && getAvailableFuel() <= 0) {
                    outOfFuel = true;
                    return;
                } else if (futureFuelBalance < 0) {
                    break;
                }

//                LOGGER.debug("Using scout ant path: from "+link.getFirst().getNumber()+" to "+link.getSecond().getNumber());

                final int usedFuel = getAvailableFuel() - fuelBalance;
                addNextNode(usedFuel, link);
            }
        }

        final List<ReachableLink> reachableLinks = findReachableLinks();

        final Node targetNode = graph.getTargetNode();

        if (reachableLinks.isEmpty() && (currentNode == targetNode)) {
            return;
        }

        if (reachableLinks.isEmpty() || outOfFuel) {
            outOfFuel = true;
            badPaths.add(this.path);

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
            final int futureFuelBalance = getAvailableFuel() - link.getWeight();

            if (futureFuelBalance < 0) {

                final Cycle cycle = cycles.get(currentNode);

                if ((cycle != null) && !applyPath(cycle)) {
                    cycles.remove(currentNode);
                }

                outOfFuel = true;
                badPaths.add(this.path);

                return;
            }

            final int usedFuel = getAvailableFuel() - fuelBalance;
            final Cycle newCycle = findCycle(link, this.path);

            addNextNode(usedFuel, link);

            if (newCycle != null) {
                addCycle(cycles, newCycle);
            }

            return;
        }

        outOfFuel = true;
    }
}
