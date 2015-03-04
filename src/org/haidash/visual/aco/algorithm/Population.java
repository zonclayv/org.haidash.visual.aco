package org.haidash.visual.aco.algorithm;

import org.haidash.visual.aco.algorithm.model.AcoProperties;
import org.haidash.visual.aco.algorithm.model.Pair;
import org.haidash.visual.aco.algorithm.model.SearchResult;

public class Population {

	// private static final Logger LOGGER = Logger.getLogger(Generation.class);

	// local
	private final Pair<Double, Double>[][] pheromones;
	private final int[][] nodeVisits;

	private final Colony colony;

	private SearchResult bestResult;

	public Population(final Colony colony) {

		this.colony = colony;

		// local
		this.nodeVisits = initMatrix();
		this.pheromones = initPheromones();
	}

	public Colony getColony() {
		return colony;
	}

	public int[][] getNodeVisits() {
		return nodeVisits;
	}

	public Pair<Double, Double>[][] getPheromones() {
		return pheromones;
	}

	private int[][] initMatrix() {

		final AcoProperties properties = AcoProperties.getInstance();
		final int numNodes = properties.getNumNodes();
		final int[][] matrix = new int[numNodes][numNodes];

		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {
				matrix[i][j] = 0;
			}
		}

		return matrix;
	}

	@SuppressWarnings("unchecked")
	private Pair<Double, Double>[][] initPheromones() {

		final AcoProperties properties = AcoProperties.getInstance();
		final int numNodes = properties.getNumNodes();
		final Pair<Double, Double>[][] pheromones = new Pair[numNodes][numNodes];

		final Pair<Double, Double>[][] globalPheromones = colony.getGlobalPheromones();

		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {
				pheromones[i][j] = new Pair<>(globalPheromones[i][j]);
			}
		}

		return pheromones;
	}

	public SearchResult start() {

		final AcoProperties properties = AcoProperties.getInstance();

		for (int i = 0; i < properties.getNumAnts(); i++) {

			final Ant ant = new Ant(this);
			final SearchResult result = ant.search();

			if (result == null) {
				continue;
			}

			if (bestResult == null) {
				bestResult = result;

				continue;
			}

			boolean isBestTotalCost = result.getTotalCost() < bestResult.getTotalCost();
			boolean isEqualsTotalCost = result.getTotalCost() == bestResult.getTotalCost();
			boolean isLessNodes = result.getVisited().size() < bestResult.getVisited().size();

			if (isBestTotalCost || (isEqualsTotalCost && isLessNodes)) {
				bestResult = result;
			}
		}

		return bestResult;
	}

}
