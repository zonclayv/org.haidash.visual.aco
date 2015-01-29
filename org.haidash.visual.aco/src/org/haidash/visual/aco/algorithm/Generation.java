package org.haidash.visual.aco.algorithm;

import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.haidash.visual.aco.algorithm.model.AcoProperties;
import org.haidash.visual.aco.algorithm.model.Cycle;
import org.haidash.visual.aco.algorithm.model.Pair;
import org.haidash.visual.aco.algorithm.model.SearchResult;


public class Generation {

	// private static final Logger LOGGER = Logger.getLogger(Generation.class);

	// global
	private final Set<List<Integer>> badPaths;
	private final Map<Integer, Cycle> cycles;
	private final  SimpleObjectProperty[][] globalPheromones;

	// local
	private final  SimpleObjectProperty[][] pheromones;
	private final int[][] nodeVisits;

	private SearchResult bestResult;

	public Generation(final Set<List<Integer>> badPaths, final Map<Integer, Cycle> cycles, final  SimpleObjectProperty[][] globalPheromones) {

		// global
		this.badPaths = badPaths;
		this.cycles = cycles;
		this.globalPheromones = globalPheromones;

		// local
		this.nodeVisits = initMatrix();
		this.pheromones = initPheromones(globalPheromones);
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
	private  SimpleObjectProperty[][] initPheromones(final  SimpleObjectProperty<Pair>[][] globalPheromones) {

		final AcoProperties properties = AcoProperties.getInstance();
		final int numNodes = properties.getNumNodes();
		final  SimpleObjectProperty[][] pheromones = new SimpleObjectProperty[numNodes][numNodes];

		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {
				pheromones[i][j]=new SimpleObjectProperty();
				pheromones[i][j].setValue(new Pair<>(globalPheromones[i][j].get()));
			}
		}

		return pheromones;
	}

	public SearchResult start() {

		final AcoProperties properties = AcoProperties.getInstance();

		for (int i = 0; i < properties.getNumAnts(); i++) {

			final Ant ant = new Ant(badPaths, cycles, nodeVisits, pheromones);

			final SearchResult result = ant.search();

			ant.updatePheromones(globalPheromones);

			if (result != null && (bestResult == null || result.getTotalCost() < bestResult.getTotalCost())) {
				bestResult = result;
			}
		}

		return bestResult;
	}

}
