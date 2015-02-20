package org.haidash.visual.aco.algorithm.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class AcoProperties {

	public static AcoProperties getInstance() {

		if (instance == null) {
			instance = new AcoProperties();
		}

		return instance;
	}

	private static AcoProperties instance;
	private final SimpleDoubleProperty alpha = new SimpleDoubleProperty(0.1);
	private final SimpleDoubleProperty beta = new SimpleDoubleProperty(0.1);
	private final SimpleIntegerProperty q = new SimpleIntegerProperty(20);
	private double pheromonePersistence = 0.3;
	private int startNode;
	private int targetNode;
	private int numNodes;
	private int maxFuel;
	private final SimpleIntegerProperty numGeneration = new SimpleIntegerProperty(1500);
	private final SimpleIntegerProperty numAnts = new SimpleIntegerProperty(3000);
	private int[][] nodesMap;
	private int[] fuelLevels;
	private int[] remainsFuel;

	private Pair<Integer, Integer>[] verticesMap;

	private AcoProperties() {
	}

	public double getAlpha() {
		return alpha.get();
	}

	public SimpleDoubleProperty getAlphaProperty() {
		return alpha;
	}

	public double getBeta() {
		return beta.get();
	}

	public SimpleDoubleProperty getBetaProperty() {
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

	public Pair<Integer, Integer>[] getVerticesMap() {
		return verticesMap;
	}

	@SuppressWarnings("unchecked")
	public void initializeValue(final File file) {

		initializeValue(file, true);
	}

	@SuppressWarnings("unchecked")
	public void initializeValue(final File file, final boolean dontGraph) {

		try (Scanner text = new Scanner(new FileReader(file))) {

			setNumNodes(text.nextInt());

			setFuelLevels(new int[getNumNodes()]);

			for (int i = 0; i < getNumNodes(); i++) {
				getFuelLevels()[i] = text.nextInt();
			}

			setVerticesMap(new Pair[getNumNodes()]);

			if (dontGraph) {
				for (int i = 0; i < getNumNodes(); i++) {
					getVerticesMap()[i] = new Pair<>(text.nextInt(), text.nextInt());
				}
			}

			final int numEdges = text.nextInt();

			setNodesMap(new int[getNumNodes()][getNumNodes()]);

			for (int i = 0; i < getNumNodes(); i++) {
				for (int j = 0; j < getNumNodes(); j++) {
					getNodesMap()[i][j] = -1;
				}
			}

			for (int i = 0; i < numEdges; i++) {
				final int start = text.nextInt();
				final int finish = text.nextInt();

				getNodesMap()[start][finish] = text.nextInt();
			}

			setMaxFuel(text.nextInt());
			setStartNode(text.nextInt());
			setTargetNode(text.nextInt());

		} catch (final FileNotFoundException e) {
			throw new RuntimeException("Can't find input file", e);
		}
	}

	public void initRemainsFuel() {
		setRemainsFuel(FloydWarshall.getRemainsFuel(getNumNodes(), getNodesMap(), getFuelLevels(), getMaxFuel(), getTargetNode()));
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

	public void setVerticesMap(final Pair<Integer, Integer>[] verticesMap) {
		this.verticesMap = verticesMap;
	}
}
