package org.haidash.visual.aco.algorithm;

import static java.lang.Math.pow;
import static org.haidash.visual.aco.algorithm.ACOUtils.countNumberEqual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.haidash.visual.aco.algorithm.model.AcoProperties;
import org.haidash.visual.aco.algorithm.model.Cycle;
import org.haidash.visual.aco.algorithm.model.Pair;
import org.haidash.visual.aco.algorithm.model.ReachableNode;
import org.haidash.visual.aco.algorithm.model.SearchResult;

/**
 * @author Haidash Aleh
 */
public class AbstractAnt {

	private final static Logger LOGGER = Logger.getLogger(AbstractAnt.class);
	private static final Random RANDOM = new Random(System.nanoTime());

	// global
	private final Generation generation;
	private final Colony colony;

	// ant
	private int node;
	private int fuelBalance = 0;
	private int totalCost = 0;
	private boolean outOfFuel = false;
	private final List<Integer> visited;
	private final List<Integer> spentFuelLevel;
	private final int[] tempFuelLevel;

	public AbstractAnt(final Generation generation) {

		// global
		this.generation = generation;
		this.colony = generation.getColony();

		// ant
		final AcoProperties properties = AcoProperties.getInstance();
		final int startNode = properties.getStartNode();

		this.node = startNode;
		this.tempFuelLevel = properties.getFuelLevels().clone();
		this.spentFuelLevel = new ArrayList<>();
		this.visited = new ArrayList<>();

		visited.add(startNode);
	}

	private boolean applyPath(int currentNode, final List<Integer> improverPath) {

		final AcoProperties properties = AcoProperties.getInstance();
		final int[][] nodesMap = properties.getNodesMap();

		for (int i = 0; i < improverPath.size(); i++) {

			final int nextLocalNode = improverPath.get(i);
			final int fuelCost = nodesMap[currentNode][nextLocalNode];
			final int availableFuel = getAvailableFuel(currentNode);

			if (availableFuel < fuelCost) {
				outOfFuel = true;
				return false;
			}

			goToNextNode(currentNode, nextLocalNode);
			currentNode = nextLocalNode;
		}

		return true;
	}

	private void createAndAddCycle(final int startNode) {

		final int firstIndexOf = visited.indexOf(startNode);
		final int lastIndexOf = visited.lastIndexOf(startNode);

		if ((firstIndexOf == lastIndexOf) || (firstIndexOf == -1)) {
			return;
		}

		final AcoProperties properties = AcoProperties.getInstance();
		final int[] fuelLevels = properties.getFuelLevels();
		final int[][] nodesMap = properties.getNodesMap();

		int fuel = 0;

		final List<Integer> visitedNodes = new ArrayList<>();

		for (int i = firstIndexOf + 1; i <= lastIndexOf; i++) {

			final int node = visited.get(i);

			if (!visitedNodes.contains(node)) {
				fuel += fuelLevels[node];
			}

			visitedNodes.add(node);

			if (i == lastIndexOf) {
				break;
			}else if(i ==(firstIndexOf+1)){
				fuel -= nodesMap[visited.get(firstIndexOf)][node];
			}

			final int nextVertex = visited.get(i + 1);
			fuel -= nodesMap[node][nextVertex];
		}

		final Cycle cycle = new Cycle();
		cycle.setStartNode(startNode);
		cycle.setFuel(fuel);
		cycle.setNodes(visitedNodes);

		final Map<Integer, Cycle> cycles = colony.getCycles();
		final Cycle oldCycle = cycles.get(startNode);

		if (oldCycle != null) {
			final int oldCycleFuel = oldCycle.getFuel();
			final int cycleFuel = cycle.getFuel();

			if ((oldCycleFuel > cycleFuel)) {
				return;
			}

			if (!((oldCycleFuel == cycleFuel) && (oldCycle.getNodes().size() > cycle.getNodes().size()))) {
				return;
			}
		}

		cycles.put(startNode, cycle);
	}

	private void findReachableNodes(final int currentNode, final List<ReachableNode> reachableNodes) {

		final AcoProperties properties = AcoProperties.getInstance();
		final int[][] nodesMap = properties.getNodesMap();
		final int[] remainsFuel = properties.getRemainsFuel();

		final int[][] nodeVisits = generation.getNodeVisits();
		final Cycle cycle = colony.getCycles().get(currentNode);

		double sum = -1.0;

		for (int nextNode = 0; nextNode < properties.getNumNodes(); nextNode++) {

			final int fuelCost = nodesMap[currentNode][nextNode];
			final int visitCount = nodeVisits[currentNode][nextNode];

			if ((fuelCost <= 0) || (countNumberEqual(visited, nextNode) >= 2)) {
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

				if ((fuelAfterCycle < fuelCost) || isBadPath(visited, cycle.getNodes(), nextNode)) {
					continue;
				}
			}

			final int usedFuel = availableFuel - fuelBalance;

			final double etaVisits = (visitCount == 0) || (visitCount == 1) ? 1.0 : 1.0 - (1 / (double) visitCount);
			final double etaCost = fuelCost == 1 ? 1 : 1 - (1 / (double) fuelCost);

			double etaRemaning;

			final double k = (availableFuel - fuelCost) + remainsFuel[nextNode];

			if (k > 0) {
				etaRemaning = k == 1 ? 1.1 : (1 + 1) - (1 / k);
			} else if (k < 0) {
				etaRemaning = k == -1 ? 0.9 : 1 - (1 / Math.abs(k));
			} else {
				etaRemaning = 2;
			}

			final double eta = (0.1 * etaCost) + (0.5 * etaRemaning * 0.4 * etaVisits);
			final double tau = getTau(currentNode, nextNode);

			if (sum == -1.0) {
				sum = getSumProbabilities(currentNode, cycle, availableFuel, usedFuel);
			}

			final double probability = (100 * Math.pow(tau, properties.getAlpha()) * Math.pow(eta, properties.getBeta())) / sum;

			/*
			 * LOGGER.debug("Chance " + currentNode + "->" + nextNode + " etaCost: " + etaCost + " etaRemaning: " + etaRemaning +
			 * " etaVisits: " + etaVisits + " tau: " + tau + " probability: " + probability);
			 */

			reachableNodes.add(new ReachableNode(nextNode, probability));
		}
	}

	private int getAvailableFuel(final int node) {
		final AcoProperties properties = AcoProperties.getInstance();
		final int fuelInNode = tempFuelLevel[node];
		final int maxFuel = properties.getMaxFuel();

		if ((fuelBalance + fuelInNode) > maxFuel) {
			return maxFuel;
		} else {
			return fuelBalance + fuelInNode;
		}
	}

	private int getFuelAfterCycle(final int currentNode, final int availableFuel, final int fuelInCycle) {

		final AcoProperties properties = AcoProperties.getInstance();
		final int maxFuel = properties.getMaxFuel();
		final int fuelInNode = tempFuelLevel[currentNode];

		int tempFuel = 0;

		if ((fuelBalance + fuelInNode) > maxFuel) {
			tempFuel = maxFuel - availableFuel;
		} else {
			tempFuel = (fuelBalance + fuelInNode) - availableFuel;
		}

		return tempFuel + fuelInCycle;
	}

	private double getSumProbabilities(final int currentNode, final Cycle cycle, final int availableFuel, final int usedFuel) {

		final AcoProperties properties = AcoProperties.getInstance();
		final int numNodes = properties.getNumNodes();
		final int[][] nodesMap = properties.getNodesMap();
		final int[] remainsFuel = properties.getRemainsFuel();

		final int[][] nodeVisits = generation.getNodeVisits();

		double sum = 0.0;

		for (int nextNode = 0; nextNode < numNodes; nextNode++) {

			final double fuelCost = nodesMap[currentNode][nextNode];
			final int visitCount = nodeVisits[currentNode][nextNode];

			if ((fuelCost <= 0) || (countNumberEqual(visited, nextNode) >= 2)) {
				continue;
			}

			if (availableFuel >= fuelCost) {

				if (isBadPath(visited, nextNode)) {
					continue;
				}

			} else {

				if (cycle == null) {
					continue;
				}

				final int fuelAfterCycle = getFuelAfterCycle(currentNode, availableFuel, cycle.getFuel());

				if ((fuelAfterCycle < fuelCost) || isBadPath(visited, cycle.getNodes(), nextNode)) {
					continue;
				}
			}

			final double etaVisits = (visitCount == 0) || (visitCount == 1) ? 1 : 1 - (1 / (double) visitCount);
			final double etaCost = fuelCost == 1 ? 1 : 1 - (1 / fuelCost);

			double etaRemaning;

			final double k = (availableFuel - fuelCost) + remainsFuel[nextNode];

			if (k > 0) {
				etaRemaning = k == 1 ? 1.1 : (1 + 1) - (1 / k);
			} else if (k < 0) {
				etaRemaning = k == -1 ? 0.9 : 1 - (1 / Math.abs(k));
			} else {
				etaRemaning = 2;
			}

			final double eta = (0.1 * etaCost) + (0.5 * etaRemaning * 0.4 * etaVisits);
			final double tau = getTau(currentNode, nextNode);

			sum += pow(tau, properties.getAlpha()) * pow(eta, properties.getBeta());
		}

		return sum;
	}

	private double getTau(final int x, final int y) {
		final Pair<Double, Double> pair = generation.getPheromones()[x][y];
		return pair.first / pair.second;
	}

	private void goToNextNode(final int currentNode, final int next) {

		final AcoProperties properties = AcoProperties.getInstance();
		final int[][] nodesMap = properties.getNodesMap();

		final int usedFuel = getAvailableFuel(currentNode) - fuelBalance;

		// 1
		visited.add(next);
		spentFuelLevel.add(usedFuel);
		tempFuelLevel[currentNode] = tempFuelLevel[currentNode] - usedFuel;

		// 2
		fuelBalance += usedFuel - nodesMap[currentNode][next];
		totalCost += usedFuel;

		// 3
		final int[][] nodeVisits = generation.getNodeVisits();
		final int count = nodeVisits[currentNode][next];
		nodeVisits[currentNode][next] = count + 1;
	}

	private boolean isBadPath(final List<Integer> visits, final int nextNode) {
		final List<Integer> nodes = new ArrayList<>(visits);
		nodes.add(nextNode);

		return colony.getBadPaths().contains(nodes);
	}

	private boolean isBadPath(final List<Integer> visits, final List<Integer> cycleNodes, final int nextNode) {
		final List<Integer> nodes = new ArrayList<>(visits);
		nodes.addAll(cycleNodes);
		nodes.add(nextNode);

		return colony.getBadPaths().contains(nodes);
	}

	public SearchResult search() {

		final AcoProperties properties = AcoProperties.getInstance();

		while ((node != properties.getTargetNode()) && !outOfFuel && (node != -1)) {
			node = selectNextNode(node);
		}

		updatePheromones();

		if (outOfFuel) {
			return null;
		}

		LOGGER.debug("New path " + totalCost + " " + visited.toString());
		return new SearchResult(spentFuelLevel, visited, totalCost);
	}

	private int selectNextNode(final int currentNode) {

		final AcoProperties properties = AcoProperties.getInstance();
		final int targetNode = properties.getTargetNode();

		final List<ReachableNode> reachableNodes = new ArrayList<>();
		final Set<List<Integer>> badPaths = colony.getBadPaths();

		findReachableNodes(currentNode, reachableNodes);

		if (reachableNodes.isEmpty() && (visited.get(visited.size() - 1) == targetNode)) {
			return targetNode;
		} else if (reachableNodes.isEmpty() || outOfFuel) {
			outOfFuel = true;
			badPaths.add(visited);
			return -1;
		}

		final int randomBound = (int) reachableNodes.stream().mapToDouble(e -> e.getProbability()).sum();
		final int rand = RANDOM.nextInt(randomBound);

		double roulette = 0.0;

		for (final ReachableNode reachableNode : reachableNodes) {

			final int nextNode = reachableNode.getNode();
			final double probability = reachableNode.getProbability();

			roulette += probability;

			if (roulette < rand) {
				continue;
			}

			final int[][] nodesMap = properties.getNodesMap();
			final int futureFuelBalance = getAvailableFuel(currentNode) - nodesMap[currentNode][nextNode];

			if (futureFuelBalance < 0) {
				final Map<Integer, Cycle> cycles = colony.getCycles();
				final Cycle cycle = cycles.get(currentNode);

				if ((cycle != null) && !applyPath(currentNode, cycle.getNodes())) {
					cycles.remove(currentNode);
				}

				outOfFuel = true;
				badPaths.add(visited);
				return -1;
			}

			goToNextNode(currentNode, nextNode);
			createAndAddCycle(nextNode);

			return nextNode;
		}

		return -1;
	}

	private void updatePheromones() {
		final AcoProperties properties = AcoProperties.getInstance();
		final Pair<Double, Double>[][] globalPheromones = colony.getGlobalPheromones();

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
