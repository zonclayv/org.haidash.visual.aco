package org.haidash.visual.aco.oop.impl;

import static java.lang.Math.pow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.haidash.visual.aco.oop.Agentable;
import org.haidash.visual.aco.oop.entity.Cycle;
import org.haidash.visual.aco.oop.entity.Graph;
import org.haidash.visual.aco.oop.entity.Link;
import org.haidash.visual.aco.oop.entity.Node;
import org.haidash.visual.aco.oop.entity.Properties;
import org.haidash.visual.aco.oop.entity.ReachableLink;

/**
 * @author Haidash Aleh
 */
public class Ant implements Agentable{

//	private final static Logger LOGGER = Logger.getLogger(Ant.class);
	private final static Random RANDOM = new Random(System.nanoTime());
	private final static Properties properties = Properties.getInstance();

	private final Graph graph;
	private final List<Integer> remainsFuel;

	private final List<Link> path;
	private final List<Integer> spentFuelLevel;
	private final List<Integer> tempFuelLevel;

	private Node curentNode;

	private int fuelBalance = 0;

	private int totalCost = 0;
	private boolean outOfFuel = false;

	public Ant(final Graph graph, final List<Integer> remainsFuel) {
		this.graph = graph;
		this.remainsFuel = remainsFuel;

		this.curentNode = graph.getStartNode();
		this.tempFuelLevel = new ArrayList<>(graph.getFuelLevels());
		this.spentFuelLevel = new ArrayList<>();
		this.path = new ArrayList<>();
	}

	public void addNextNode(final int usedFuel, final Link link) {

		// 1
		path.add(link);
		spentFuelLevel.add(usedFuel);
		tempFuelLevel.set(curentNode.getNumber(), tempFuelLevel.get(curentNode.getNumber()) - usedFuel);

		// 2
		fuelBalance += usedFuel - link.getWeight();
		totalCost += usedFuel;
		curentNode = link.getSecond();
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

		int fuel = newLink.getFirst().getFuelBalance() - newLink.getWeight();
		final List<Link> visitedLinks = new ArrayList<>();

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
			final Cycle cycle = new Cycle();
			cycle.setStartNode(newLink.getFirst());
			cycle.setFuel(fuel);
			cycle.setLinks(visitedLinks);

			return cycle;
		}

		return null;
	}

	private List<ReachableLink> findReachableLinks() {

		final List<ReachableLink> reachableLinks = new ArrayList<>();
		final Cycle cycle = graph.getCycles().get(curentNode);

		double sum = -1.0;

		for (Link link : curentNode.getOutgoingLinks()) {

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

			final double etaVisits =
					link.getVisitsCount() == 0 ? properties.getNumAnts().get() : (properties.getNumAnts().get() / Math.pow(link
							.getVisitsCount(), properties.getBeta().get()));
			final double etaCost = properties.getQ().get() / Math.pow(link.getWeight(), properties.getBeta().get());

			double etaRemaning = 0.0;

			final double k = (availableFuel - link.getWeight()) + remainsFuel.get(link.getSecond().getNumber());

			if (k > 0) {
				etaRemaning = properties.getMaxFuelLevels() / Math.pow(k, properties.getBeta().get());
			} else if (k < 0) {
				double tempK = 1 / Math.pow(Math.abs(k), properties.getBeta().get());
				tempK = tempK == 1 ? 0 : tempK;
				etaRemaning = -1 * (1 - tempK);
			} else {
				etaRemaning = properties.getMaxFuelLevels();
			}

			double eta = (0.3 * etaCost) + (0.3 * etaRemaning) + (0.3 * etaVisits);

			if (eta < 0) {
				eta = 1;
			}

			final double tau = link.getpPheromone() / link.getnPheromone();

			if (sum == -1.0) {
				sum = getSumProbabilities(cycle);
			}

			final double probability = 100 * ((Math.pow(tau, properties.getAlpha().get()) * eta) / sum);

			// LOGGER.debug("Chance "
			// + curentNode
			// + "->"
			// + link.getSecond()
			// + " etaCost: "
			// + etaCost
			// + " etaRemaning: "
			// + etaRemaning
			// + " etaVisits: "
			// + etaVisits
			// + " tau: "
			// + tau
			// + " probability: "
			// + probability);

			reachableLinks.add(new ReachableLink(link, probability));
		}

		return reachableLinks;
	}

	private int getAvailableFuel() {
		final int fuelInNode = tempFuelLevel.get(curentNode.getNumber());
		final int maxFuelLevels = properties.getMaxFuelLevels();
		final int allFuel = fuelBalance + fuelInNode;

		return allFuel > maxFuelLevels ? maxFuelLevels : allFuel;
	}

	private int getFuelAfterCycle(final int availableFuel, final int fuelInCycle) {

		final int fuelInNode = tempFuelLevel.get(curentNode.getNumber());
		final int maxFuelLevels = properties.getMaxFuelLevels();
		final int allFuel = fuelBalance + fuelInNode;

		int tempFuel = 0;

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
	public List<Integer> getSpentFuelLevel() {
		return spentFuelLevel;
	}

	private double getSumProbabilities(final Cycle cycle) {

		double sum = 0.0;

		for (Link link : curentNode.getOutgoingLinks()) {

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

			final double etaVisits =
					link.getVisitsCount() == 0 ? properties.getNumAnts().get() : (properties.getNumAnts().get() / Math.pow(link
							.getVisitsCount(), properties.getBeta().get()));
			final double etaCost = properties.getQ().get() / Math.pow(link.getWeight(), properties.getBeta().get());

			double etaRemaning = 0.0;

			final double k = (availableFuel - link.getWeight()) + remainsFuel.get(link.getSecond().getNumber());

			if (k > 0) {
				etaRemaning = properties.getMaxFuelLevels() / Math.pow(k, properties.getBeta().get());
			} else if (k < 0) {
				double tempK = 1 / Math.pow(Math.abs(k), properties.getBeta().get());
				tempK = tempK == 1 ? 0 : tempK;
				etaRemaning = -1 * (1 - tempK);
			} else {
				etaRemaning = properties.getMaxFuelLevels();
			}

			double eta = (0.3 * etaCost) + (0.3 * etaRemaning) + (0.3 * etaVisits);

			if (eta < 0) {
				eta = 1;
			}

			final double tau = link.getpPheromone() / link.getnPheromone();

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

		while ((curentNode != graph.getTargetNode()) && !outOfFuel) {
			selectNode();
		}
	}

	private void selectNode() {

		final List<ReachableLink> reachableLinks = findReachableLinks();

		final Node targetNode = graph.getTargetNode();
		if (reachableLinks.isEmpty() && (curentNode == targetNode)) {
			return;
		} else if (reachableLinks.isEmpty() || outOfFuel) {
			outOfFuel = true;
			graph.getBadPaths().add(path);

			return;
		}

		final int randomBound = (int) reachableLinks.stream().mapToDouble(e -> e.getValue()).sum();
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
				final Cycle cycle = graph.getCycles().get(curentNode.getNumber());

				if ((cycle != null) && !applyPath(cycle)) {
					graph.getCycles().remove(curentNode);
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

	public void setCurentNode(final Node curentNode) {
		this.curentNode = curentNode;
	}
}
