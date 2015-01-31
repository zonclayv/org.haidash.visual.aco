package org.haidash.visual.aco.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.haidash.visual.aco.algorithm.model.AcoProperties;
import org.haidash.visual.aco.algorithm.model.Chance;
import org.haidash.visual.aco.algorithm.model.Cycle;
import org.haidash.visual.aco.algorithm.model.Pair;
import org.haidash.visual.aco.algorithm.model.SearchResult;

/**
 * @author Haidash Aleh
 */
public class Ant {

    private final static Logger LOGGER = Logger.getLogger(Ant.class);
    private static final Random RANDOM = new Random(System.nanoTime());
    // global
    private final Set<List<Integer>> badPaths;
    private final Map<Integer, Cycle> cycles;
    // local
    private final int[][] visitsCount;
	private final Pair<Double, Double>[][] localPheromones;
    private final List<Integer> visited;
    private final List<Integer> spentFuelLevel;
    private final int[] tempFuelLevel;
    // ant
    private int node;
    private int fuelBalance = 0;
    private int totalCost = 0;
    private boolean outOfFuel = false;

    public Ant(final Set<List<Integer>> badPaths,
               final Map<Integer, Cycle> cycles,
               final int[][] visitsCount,
			final Pair<Double, Double>[][] localPheromones) {

        // global
        this.badPaths = badPaths;
        this.cycles = cycles;

        // local
        this.visitsCount = visitsCount;
        this.localPheromones = localPheromones;

        // ant
        final AcoProperties properties = AcoProperties.getInstance();

		int startNode = properties.getStartNode();

		this.node = startNode;
        this.tempFuelLevel = properties.getFuelLevels().clone();
        this.spentFuelLevel = new ArrayList<>();
        this.visited = new ArrayList<>();

		visited.add(startNode);
    }

    private void addCycle(final Cycle cycle) {

        final int node = cycle.getStartNode();

        if (!cycles.containsKey(node)) {
            cycles.put(node, cycle);
        } else {

            final Cycle oldCycle = cycles.get(node);

            if (oldCycle.getFuel() < cycle.getFuel()
                    || oldCycle.getFuel() == cycle.getFuel()
                    && oldCycle.getNodes().size() > cycle.getNodes().size()) {

                cycles.remove(oldCycle);
                cycles.put(node, cycle);
            }
        }
    }

    private boolean applyCycle(final Cycle cycle) {

        final AcoProperties properties = AcoProperties.getInstance();
        final List<Integer> visitedNodes = cycle.getNodes();
        final int[][] nodesMap = properties.getNodesMap();

        int currentNode = visitedNodes.get(0);

        for (int i = 1; i < visitedNodes.size(); i++) {

            final int nextNode = visitedNodes.get(i);
            final int fuelCost = nodesMap[currentNode][nextNode];
            final int availableFuel = getAvailableFuel(currentNode);

            if (availableFuel < fuelCost) {
                outOfFuel = true;
                cycles.remove(cycle.getStartNode());

                return false;
            }

            final int usedFuel = availableFuel - fuelBalance;

            goToNextNode(currentNode, nextNode, usedFuel);

            currentNode = nextNode;
        }

        return true;
    }

    private int countNumberEqual(final List<Integer> itemList, final int item) {

        int count = 0;

        for (int i = 1; i < itemList.size(); i++) {

            final int it = itemList.get(i);

            if (it == item) {
                count++;
            }
        }

        return count;
    }

    private Cycle createCycle(final int startNode) {

        final AcoProperties properties = AcoProperties.getInstance();
        final int[] fuelLevels = properties.getFuelLevels();
        final int[][] nodesMap = properties.getNodesMap();

        int fuel = 0;

        final List<Integer> visitedNodes = new ArrayList<>();

        for (int i = visited.indexOf(startNode); i < visited.size(); i++) {

            final int node = visited.get(i);

            if (!visitedNodes.contains(node)) {
                fuel += fuelLevels[node];
            }

            visitedNodes.add(node);

            if (i != visited.size() - 1) {

                final int nextVertex = visited.get(i + 1);

                fuel -= nodesMap[node][nextVertex];
            }
        }

        final Cycle cycle = new Cycle();
        cycle.setStartNode(startNode);
        cycle.setFuel(fuel);
        cycle.setNodes(visitedNodes);

        return cycle;
    }

    private void fillChances(final int currentNode, final Cycle cycle, final List<Chance> chances) {

        final AcoProperties properties = AcoProperties.getInstance();
        final int[][] nodesMap = properties.getNodesMap();
        final int[] remainsFuel = properties.getRemainsFuel();

        double sum = -1.0;

        for (int nextNode = 0; nextNode < properties.getNumNodes(); nextNode++) {

            final int fuelCost = nodesMap[currentNode][nextNode];
            final int visitCount = visitsCount[currentNode][nextNode];

            if (fuelCost <= 0 || countNumberEqual(visited, nextNode) >= 2) {
                continue;
            }

            final int availableFuel = getAvailableFuel(currentNode);

            if (availableFuel >= fuelCost) {

                if (isBadPath(visited, nextNode)) {
                    continue;
                }

            } else {

                if (cycle == null) {
                    continue;
                }

                final int fuelAfterCycle = getFuelAfterCycle(currentNode, availableFuel, cycle.getFuel());

                if (fuelAfterCycle < fuelCost) {
                    continue;
                }

                if (isBadPath(visited, cycle.getNodes(), nextNode)) {
                    continue;
                }
            }

            final int usedFuel = availableFuel - fuelBalance;

            final double etaVisits = visitCount == 0 || visitCount == 1 ? 1 : 1 - 1 / visitCount;
            final double etaCost = fuelCost == 1 ? 1 : 1 - 1 / (double) fuelCost;

            double etaRemaning;

            final double k = availableFuel - fuelCost + remainsFuel[nextNode];

            if (k > 0) {
                etaRemaning = k == 1 ? 1.1 : 1 + 1 - 1 / k;
            } else if (k < 0) {
                etaRemaning = k == -1 ? 0.9 : 1 - 1 / Math.abs(k);
            } else {
                etaRemaning = 2;
            }

            final double eta = 0.1 * etaCost + 0.5 * etaRemaning * 0.4 * etaVisits;
            final double tau = getTau(currentNode, nextNode);

            if (sum == -1.0) {
                sum = getSumProbabilities(currentNode, cycle, availableFuel, usedFuel);
            }

            final double probability = 100 * Math.pow(tau, properties.getAlpha()) * Math.pow(eta, properties.getBeta()) / sum;

            final Chance chance = new Chance(nextNode, usedFuel, probability);

            if (!chances.contains(chance)) {
                chances.add(chance);
            }
        }
    }

    private int getAvailableFuel(final int node) {

        final AcoProperties properties = AcoProperties.getInstance();
        final int fuelInNode = tempFuelLevel[node];
        final int maxFuel = properties.getMaxFuel();

        if (fuelBalance + fuelInNode > maxFuel) {
            return maxFuel;
        } else {
            return fuelBalance + fuelInNode;
        }
    }

    private int getFuelAfterCycle(final int currentNode, final int availableFuel, final int fuelInCycle) {

        final AcoProperties properties = AcoProperties.getInstance();
        final int maxFuel = properties.getMaxFuel();
        final int fuelInNode = tempFuelLevel[currentNode];

        int tempFuel;

        if (fuelBalance + fuelInNode > maxFuel) {
            tempFuel = maxFuel - availableFuel;
        } else {
            tempFuel = fuelBalance + fuelInNode - availableFuel;
        }

        return tempFuel + fuelInCycle;
    }

    private double getSumProbabilities(final int currentNode, final Cycle cycle, final int availableFuel, final int usedFuel) {

        final AcoProperties properties = AcoProperties.getInstance();
        final int numNodes = properties.getNumNodes();
        final int[][] nodesMap = properties.getNodesMap();
        final int[] remainsFuel = properties.getRemainsFuel();

        double sum = 0.0;

        for (int nextNode = 0; nextNode < numNodes; nextNode++) {

            final double fuelCost = nodesMap[currentNode][nextNode];
            final int visitCount = visitsCount[currentNode][nextNode];

            if (fuelCost <= 0 || countNumberEqual(visited, nextNode) >= 2) {
                continue;
            }

            if (availableFuel < fuelCost) {

                if (isBadPath(visited, nextNode)) {
                    continue;
                }

            } else {

                if (cycle == null) {
                    continue;
                }

                final int fuelAfterCycle = getFuelAfterCycle(currentNode, availableFuel, cycle.getFuel());

                if (fuelAfterCycle < fuelCost) {
                    continue;
                }

                if (isBadPath(visited, cycle.getNodes(), nextNode)) {
                    continue;
                }
            }

            final double etaVisits = visitCount == 0 || visitCount == 1 ? 1 : 1 - 1 / visitCount;
            final double etaCost = fuelCost == 1 ? 1 : 1 - 1 / fuelCost;

            double etaRemaning;

            final double k = availableFuel - fuelCost + remainsFuel[nextNode];

            if (k > 0) {
                etaRemaning = k == 1 ? 1.1 : 1 + 1 - 1 / k;
            } else if (k < 0) {
                etaRemaning = k == -1 ? 0.9 : 1 - 1 / Math.abs(k);
            } else {
                etaRemaning = 2;
            }

            final double eta = 0.1 * etaCost + 0.5 * etaRemaning * 0.4 * etaVisits;
            final double tau = getTau(currentNode, nextNode);

            sum += Math.pow(tau, properties.getAlpha()) * Math.pow(eta, properties.getBeta());

        }

        return sum;
    }

    private double getTau(final int x, final int y) {
		final Pair<Double, Double> pair = localPheromones[x][y];
        return pair.first / pair.second;
    }

    private boolean getToNextNode(final int currentNode, final int next, int usedFuel, final Cycle cycle) {

        final AcoProperties properties = AcoProperties.getInstance();
        final int[][] nodesMap = properties.getNodesMap();

        int futureFuelBalance = fuelBalance + usedFuel - nodesMap[currentNode][next];

        if (futureFuelBalance < 0) {

            if (!applyCycle(cycle)) {
                return false;
            } else {

                usedFuel = getAvailableFuel(currentNode) - nodesMap[currentNode][next];
                futureFuelBalance = fuelBalance - nodesMap[currentNode][next] + usedFuel;

                if (usedFuel < 0) {
                    outOfFuel = true;
                    return false;
                }
            }
        }

        visited.add(next);
        spentFuelLevel.add(usedFuel);

        final int newFuelLevel = tempFuelLevel[currentNode] - usedFuel;

        tempFuelLevel[currentNode] = newFuelLevel;
        fuelBalance = futureFuelBalance;
        totalCost += usedFuel;

        incVisits(currentNode, next);

        return true;
    }

    private void goToNextNode(final int currentNode, final int next, final int usedFuel) {

        final AcoProperties properties = AcoProperties.getInstance();
        final int[][] nodesMap = properties.getNodesMap();

        // 1
        visited.add(next);
        final int remainingFuelInCurrentNode = tempFuelLevel[currentNode] - usedFuel;

        spentFuelLevel.add(usedFuel);
        tempFuelLevel[currentNode] = remainingFuelInCurrentNode;

        // 2
        fuelBalance += usedFuel - nodesMap[currentNode][next];
        totalCost += usedFuel;

        incVisits(currentNode, next);
    }

    private void incVisits(final int from, final int to) {
        final int count = visitsCount[from][to];
        visitsCount[from][to] = count + 1;
    }

    private boolean isBadPath(final List<Integer> visits, final int nextNode) {
        final List<Integer> nodes = new ArrayList<>(visits);

        nodes.add(nextNode);

        return badPaths.contains(nodes);
    }

    private boolean isBadPath(final List<Integer> visits, final List<Integer> cycleNodes, final int nextNode) {

        final List<Integer> nodes = new ArrayList<>(visits);

        nodes.remove(nodes.size() - 1);
        nodes.addAll(cycleNodes);
        nodes.add(nextNode);

        return badPaths.contains(nodes);
    }

    public SearchResult search() {

        final AcoProperties properties = AcoProperties.getInstance();

        while (node != properties.getTargetNode() && !outOfFuel && node != -1) {
            node = selectNextNode(node);
        }

        SearchResult searchResult = null;
        String message;

        if (outOfFuel) {
            badPaths.add(visited);
            message = "Out fuel ";
        } else {
            searchResult = new SearchResult(spentFuelLevel, visited, totalCost);
            message = "New path " + searchResult.getTotalCost() + " ";
        }

        LOGGER.info(message + visited.toString());

        return searchResult;
    }

    private int selectNextNode(final int currentNode) {

        final List<Chance> chances = new ArrayList<>();
        final Cycle cycle = cycles.get(currentNode);

        fillChances(currentNode, cycle, chances);

        if (chances.size() == 0 || outOfFuel) {
            outOfFuel = true;

            return -1;
        }

        Double roulette = 0.0;

        for (final Chance chance : chances) {
            roulette += chance.getValue();
        }

        final int r = RANDOM.nextInt(roulette.intValue());


        roulette = 0.0;

        for (final Chance chance : chances) {

            final int nextNode = chance.getNode();
            final int usedFuels = chance.getFuel();
            final double probability = chance.getValue();

            roulette += probability;

            if (roulette < r) {
                continue;
            }

            if (!getToNextNode(currentNode, nextNode, usedFuels, cycle)) {
                return -1;
            }

            final int firstIndexOf = visited.indexOf(nextNode);
            final int lastIndexOf = visited.lastIndexOf(nextNode);

            if (firstIndexOf != lastIndexOf && firstIndexOf != -1) {
                final Cycle newCycle = createCycle(nextNode);

                addCycle(newCycle);
            }

            return nextNode;
        }

        return -1;
    }

	public void updatePheromones(final Pair<Double, Double>[][] globalPheromones) {
        final AcoProperties properties = AcoProperties.getInstance();

        int first = visited.get(0);
        double deltaTau = 0;

        if (totalCost != 0) {
            deltaTau = properties.getQ() / totalCost;
        }

        for (int i = 1; i < visited.size(); i++) {

            final Integer second = visited.get(i);

			final Pair<Double, Double> pairPheromones = globalPheromones[first][second];

            double pValue = pairPheromones.first;
            double nValue = pairPheromones.second;

            if (!outOfFuel) {
                pValue += deltaTau;
            } else {
                nValue += deltaTau;
            }

			globalPheromones[first][second] = new Pair<>(pValue, nValue);

            first = second;
        }
    }
}
