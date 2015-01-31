package org.haidash.visual.aco.algorithm.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class AcoProperties {

    private static AcoProperties instance;
	private final SimpleDoubleProperty alpha = new SimpleDoubleProperty(0.1);
	private final SimpleDoubleProperty beta = new SimpleDoubleProperty(0.1);
	private final SimpleIntegerProperty q = new SimpleIntegerProperty(70);
    private double pheromonePersistence = 0.3;
    private int startNode;
    private int targetNode;
    private int numNodes;
    private int maxFuel;
	private final SimpleIntegerProperty numGeneration = new SimpleIntegerProperty(10);
	private final SimpleIntegerProperty numAnts = new SimpleIntegerProperty(10);
    private int[][] nodesMap;
    private int[] fuelLevels;
    private int[] remainsFuel;
	private Pair<Integer, Integer>[] verticesMap;

    private AcoProperties() {
    }

    public static AcoProperties getInstance() {

        if (instance == null) {
            instance = new AcoProperties();
        }

        return instance;
    }

	public Pair<Integer, Integer>[] getVerticesMap() {
        return verticesMap;
    }

	public void setVerticesMap(Pair<Integer, Integer>[] verticesMap) {
        this.verticesMap = verticesMap;
    }

	@SuppressWarnings("unchecked")
	public void initializeValue(final File file) {

        try (Scanner text = new Scanner(new FileReader(file))) {

            setNumNodes(text.nextInt());

            setFuelLevels(new int[getNumNodes()]);

            for (int i = 0; i < getNumNodes(); i++) {
                getFuelLevels()[i] = text.nextInt();
            }

			setVerticesMap(new Pair[getNumNodes()]);

            for (int i = 0; i < getNumNodes(); i++) {
				getVerticesMap()[i] = new Pair<>(text.nextInt(), text.nextInt());
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

	public SimpleDoubleProperty getAlphaProperty() {
		return alpha;
	}

    public double getAlpha() {
		return alpha.get();
    }

    public void setAlpha(double alpha) {
		this.alpha.setValue(alpha);
	}

	public SimpleDoubleProperty getBetaProperty() {
		return beta;
    }
    public double getBeta() {
		return beta.get();
    }

    public void setBeta(double beta) {
		this.beta.setValue(beta);
	}

	public SimpleIntegerProperty getQProperty() {
		return q;
    }
    public double getQ() {
		return q.get();
    }

	public void setQ(int q) {
		this.q.setValue(q);
    }

    public double getPheromonePersistence() {
        return pheromonePersistence;
    }

    public void setPheromonePersistence(double pheromonePersistence) {
        this.pheromonePersistence = pheromonePersistence;
    }

    public int getStartNode() {
        return startNode;
    }

    public void setStartNode(int startNode) {
        this.startNode = startNode;
    }

	public SimpleIntegerProperty getNumGenerationProperty() {
		return numGeneration;
	}

    public int getNumGeneration() {
		return numGeneration.get();
    }

    public void setNumGeneration(int numGeneration) {
		this.numGeneration.setValue(numGeneration);
	}

	public SimpleIntegerProperty getNumAntsProperty() {
		return numAnts;
    }

    public int getNumAnts() {
		return numAnts.get();
    }

    public void setNumAnts(int numAnts) {
		this.numAnts.setValue(numAnts);
    }

    public int[] getRemainsFuel() {
        return remainsFuel;
    }

    public void setRemainsFuel(int[] remainsFuel) {
        this.remainsFuel = remainsFuel;
    }

    public int getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(int targetNode) {
        this.targetNode = targetNode;
    }

    public int getNumNodes() {
        return numNodes;
    }

    public void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }

    public int getMaxFuel() {
        return maxFuel;
    }

    public void setMaxFuel(int maxFuel) {
        this.maxFuel = maxFuel;
    }

    public int[][] getNodesMap() {
        return nodesMap;
    }

    public void setNodesMap(int[][] nodesMap) {
        this.nodesMap = nodesMap;
    }

    public int[] getFuelLevels() {
        return fuelLevels;
    }

    public void setFuelLevels(int[] fuelLevels) {
        this.fuelLevels = fuelLevels;
    }
}
