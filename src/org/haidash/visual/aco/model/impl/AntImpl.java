package org.haidash.visual.aco.model.impl;

import com.carrotsearch.hppc.IntArrayList;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.model.Ant;
import org.haidash.visual.aco.model.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

/**
 * @author Haidash Aleh
 */
public class AntImpl implements Ant {

    private final static Logger LOGGER = Logger.getLogger(AntImpl.class);
    private final static Random RANDOM = new Random(System.nanoTime());

    private final Properties properties = Properties.getInstance();

    private final Graph graph;
    private final IntArrayList remainsFuel;

    private final List<Link> path;
    private final IntArrayList spentFuelLevel;
    private final IntArrayList tempFuelLevel;

    private Node currentNode;

    private int fuelBalance = 0;
    private int totalCost = 0;

    private boolean outOfFuel = false;

    public AntImpl(final Graph graph, final IntArrayList remainsFuel) {
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

    public Cycle findCycle(final Link newLink) {

        final List<Link> visitedLinks = new ArrayList<>();

        int fuel = newLink.getFirst().getFuelBalance() - newLink.getWeight();

        boolean isFindCycle = false;

        for (Link pathLink : path) {

            if ((!pathLink.getFirst().equals(newLink.getSecond()) || pathLink.equals(newLink)) && !isFindCycle) {
                continue;
            }

            isFindCycle = true;

            if (!visitedLinks.contains(pathLink)) {
                fuel += pathLink.getFirst().getFuelBalance();
            }

            visitedLinks.add(pathLink);

            fuel -= pathLink.getWeight();
        }

        if (isFindCycle) {

            visitedLinks.add(newLink);
            fuel -= newLink.getWeight();

            final Cycle cycle = new Cycle();
            cycle.setStartNode(newLink.getSecond());
            cycle.setFuel(fuel);
            cycle.setLinks(visitedLinks);

            return cycle;
        }

        return null;
    }

    private List<ReachableLink> findReachableLinks() {

        final List<ReachableLink> reachableLinks = new ArrayList<>();
        final Cycle cycle = graph.getCycles().get(currentNode);

        double sum = -1.0;

        for (Link link : currentNode.getOutgoingLinks()) {

            final int availableFuel = getAvailableFuel();

            if (availableFuel >= link.getWeight()) {
                if (graph.isBadPath(path, link)) {
                    continue;
                }
            } else {
                if (cycle == null) {
                    continue;
                }

                final int fuelAfterCycle = getFuelAfterCycle(availableFuel, cycle.getFuel());

                if ((fuelAfterCycle < link.getWeight()) || graph.isBadPath(path, cycle.getLinks(), link)) {
                    continue;
                }
            }

            final double powBetaValue = pow(link.getVisitsCount(), properties.getBeta().get());
            final double powAlphaValue = pow(link.getWeight(), properties.getBeta().get());
            final double etaVisits = link.getVisitsCount() == 0 ? properties.getNumAnts().get() : (properties.getNumAnts().get() / powBetaValue);
            final double etaCost = properties.getQ().get() / powAlphaValue;

            double etaRemaning = 0d;

            final double k = (availableFuel - link.getWeight()) + remainsFuel.get(link.getSecond().getNumber());

            if (k > 0) {
                etaRemaning = properties.getMaxFuelLevels() / pow(k, properties.getBeta().get());
            } else if (k < 0) {
                double tempK = 1 / pow(abs(k), properties.getBeta().get());
                tempK = tempK == 1 ? 0 : tempK;
                etaRemaning = -1 * (1 - tempK);
            } else {
                etaRemaning = properties.getMaxFuelLevels();
            }

            double eta = (0.3 * etaCost) + (0.3 * etaRemaning) + (0.3 * etaVisits);

            if (eta < 0) {
                eta = 1;
            }

            final double tau = link.getPPheromone() / link.getNPheromone();

            if (sum == -1.0) {
                sum = getSumProbabilities(cycle);
            }

            final double probability = 100 * ((pow(tau, properties.getAlpha().get()) * eta) / sum);

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
        final int maxFuelLevels = properties.getMaxFuelLevels();
        final int allFuel = fuelBalance + fuelInNode;

        return allFuel > maxFuelLevels ? maxFuelLevels : allFuel;
    }

    private int getFuelAfterCycle(final int availableFuel, final int fuelInCycle) {

        final int fuelInNode = tempFuelLevel.get(currentNode.getNumber());
        final int maxFuelLevels = properties.getMaxFuelLevels();
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
                if (graph.isBadPath(path, link)) {
                    continue;
                }
            } else {
                if (cycle == null) {
                    continue;
                }

                final int fuelAfterCycle = getFuelAfterCycle(availableFuel, cycle.getFuel());

                if ((fuelAfterCycle < link.getWeight()) || graph.isBadPath(path, cycle.getLinks(), link)) {
                    continue;
                }
            }

            final double powBetaValue = pow(link.getVisitsCount(), properties.getBeta().get());
            final double powAlphaValue = pow(link.getWeight(), properties.getBeta().get());
            final double etaVisits = link.getVisitsCount() == 0 ? properties.getNumAnts().get() : (properties.getNumAnts().get() / powBetaValue);
            final double etaCost = properties.getQ().get() / powAlphaValue;

            double etaRemaning;

            final double k = (availableFuel - link.getWeight()) + remainsFuel.get(link.getSecond().getNumber());

            if (k > 0) {
                etaRemaning = properties.getMaxFuelLevels() / pow(k, properties.getBeta().get());
            } else if (k < 0) {
                double tempK = 1 / pow(abs(k), properties.getBeta().get());
                tempK = tempK == 1 ? 0 : tempK;
                etaRemaning = -1 * (1 - tempK);
            } else {
                etaRemaning = properties.getMaxFuelLevels();
            }

            double eta = (0.3 * etaCost) + (0.3 * etaRemaning) + (0.3 * etaVisits);

            if (eta < 0) {
                eta = 1;
            }

            final double tau = link.getPPheromone() / link.getNPheromone();

            sum += pow(tau, properties.getAlpha().get()) * eta;
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
            graph.getBadPaths().add(path);

            return;
        }

        final int randomBound = (int) reachableLinks.stream().mapToDouble(ReachableLink::getValue).sum();
        final int rand = RANDOM.nextInt(randomBound);

        double roulette = 0.0;

        for (final ReachableLink reachableLink : reachableLinks) {

            final double probability = reachableLink.getValue();

            roulette += probability;

            if (roulette < rand) {
                continue;
            }

            final int futureFuelBalance = getAvailableFuel() - reachableLink.getLink().getWeight();

            if (futureFuelBalance < 0) {

                final Cycle cycle = graph.getCycles().get(currentNode);

                if ((cycle != null) && !applyPath(cycle)) {
                    graph.getCycles().remove(currentNode);
                }

                outOfFuel = true;
                graph.getBadPaths().add(path);

                return;
            }

            final int usedFuel = getAvailableFuel() - fuelBalance;
            final Cycle newCycle = findCycle(reachableLink.getLink());

            addNextNode(usedFuel, reachableLink.getLink());

            if (newCycle != null) {
                graph.addCycle(newCycle);
            }

            return;
        }

        outOfFuel = true;
    }

    @Override
    public void setStartNode(final Node currentNode) {
        this.currentNode = currentNode;
    }
}
