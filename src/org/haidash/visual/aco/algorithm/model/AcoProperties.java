package org.haidash.visual.aco.algorithm.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javafx.beans.property.SimpleIntegerProperty;

public class AcoProperties {

	public static AcoProperties getInstance() {

		if (instance == null) {
			instance = new AcoProperties();
		}

		return instance;
	}

	private static AcoProperties instance;

	private final SimpleIntegerProperty alpha = new SimpleIntegerProperty(1);
	private final SimpleIntegerProperty beta = new SimpleIntegerProperty(3);
	private final SimpleIntegerProperty q = new SimpleIntegerProperty(20);

	private final SimpleIntegerProperty numGeneration = new SimpleIntegerProperty(15);
	private final SimpleIntegerProperty numAnts = new SimpleIntegerProperty(30);

	private double pheromonePersistence = 0.5;

	private int startNode;
	private int targetNode;

	private int numNodes;
	private int maxFuel;

	private int[][] nodesMap;
	private int[] fuelLevels;
	private int[] remainsFuel;

	private AcoProperties() {
	}

	public double getAlpha() {
		return alpha.get();
	}

	public SimpleIntegerProperty getAlphaProperty() {
		return alpha;
	}

	public double getBeta() {
		return beta.get();
	}

	public SimpleIntegerProperty getBetaProperty() {
		return beta;
	}

	public int[] getFuelLevels() {
		return fuelLevels;
	}

	public int getMaxFuel() {
		return maxFuel;
	}

	public int[][] getNodesMap() {
		return nodesMap;
	}

	public int getNumAnts() {
		return numAnts.get();
	}

	public SimpleIntegerProperty getNumAntsProperty() {
		return numAnts;
	}

	public int getNumGeneration() {
		return numGeneration.get();
	}

	public SimpleIntegerProperty getNumGenerationProperty() {
		return numGeneration;
	}

	public int getNumNodes() {
		return numNodes;
	}

	public double getPheromonePersistence() {
		return pheromonePersistence;
	}

	public double getQ() {
		return q.get();
	}

	public SimpleIntegerProperty getQProperty() {
		return q;
	}

	public int[] getRemainsFuel() {
		return remainsFuel;
	}

	public int getStartNode() {
		return startNode;
	}

	public int getTargetNode() {
		return targetNode;
	}

	public void initializeValue(final File file) {

		try (Scanner text = new Scanner(new FileReader(file))) {

			numNodes = text.nextInt();
			fuelLevels = new int[numNodes];

			for (int i = 0; i < numNodes; i++) {
				fuelLevels[i] = text.nextInt();
			}

			this.nodesMap = new int[numNodes][numNodes];

			for (int i = 0; i < numNodes; i++) {
				for (int j = 0; j < numNodes; j++) {
					nodesMap[i][j] = -1;
				}
			}

			final int numEdges = text.nextInt();
			for (int i = 0; i < numEdges; i++) {
				final int start = text.nextInt();
				final int finish = text.nextInt();

				nodesMap[start][finish] = text.nextInt();
			}

			maxFuel = text.nextInt();
			startNode = text.nextInt();
			targetNode = text.nextInt();

		} catch (final FileNotFoundException e) {
			throw new RuntimeException("Can't find input file", e);
		}
	}

	public void initRemainsFuel() {
		remainsFuel = FloydWarshall.getRemainsFuel(numNodes, nodesMap, fuelLevels, maxFuel, targetNode);
	}

	public void setAlpha(final double alpha) {
		this.alpha.setValue(alpha);
	}

	public void setBeta(final double beta) {
		this.beta.setValue(beta);
	}

	public void setFuelLevels(final int[] fuelLevels) {
		this.fuelLevels = fuelLevels;
	}

	public void setMaxFuel(final int maxFuel) {
		this.maxFuel = maxFuel;
	}

	public void setNodesMap(final int[][] nodesMap) {
		this.nodesMap = nodesMap;
	}

	public void setNumAnts(final int numAnts) {
		this.numAnts.setValue(numAnts);
	}

	public void setNumGeneration(final int numGeneration) {
		this.numGeneration.setValue(numGeneration);
	}

	public void setNumNodes(final int numNodes) {
		this.numNodes = numNodes;
	}

	public void setPheromonePersistence(final double pheromonePersistence) {
		this.pheromonePersistence = pheromonePersistence;
	}

	public void setQ(final int q) {
		this.q.setValue(q);
	}

	public void setRemainsFuel(final int[] remainsFuel) {
		this.remainsFuel = remainsFuel;
	}

	public void setStartNode(final int startNode) {
		this.startNode = startNode;
	}

	public void setTargetNode(final int targetNode) {
		this.targetNode = targetNode;
	}
}
