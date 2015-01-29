package algorithm.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class AcoProperties {

    private static AcoProperties instance;
    private double alpha = 0.1;
    private double beta = 0.1;
    private double q = 70.0;
    private double pheromonePersistence = 0.3;
    private double initialPheromones = 0.8;
    private int startNode;
    private int targetNode;
    private int numNodes;
    private int maxFuel;
    private int numGeneration;
    private int numAnts;
    private int[][] nodesMap;
    private int[] fuelLevels;
    private int[] remainsFuel;
    private Pair[] verticesMap;

    private AcoProperties() {
    }

    public static AcoProperties getInstance() {

        if (instance == null) {
            instance = new AcoProperties();
        }

        return instance;
    }

    public static void setInstance(AcoProperties instance) {
        AcoProperties.instance = instance;
    }

    public Pair[] getVerticesMap() {
        return verticesMap;
    }

    public void setVerticesMap(Pair[] verticesMap) {
        this.verticesMap = verticesMap;
    }

    public void initializeValue(final File file) {

        try (Scanner text = new Scanner(new FileReader(file))) {

            setNumNodes(text.nextInt());

            setFuelLevels(new int[getNumNodes()]);

            for (int i = 0; i < getNumNodes(); i++) {
                getFuelLevels()[i] = text.nextInt();
            }

            setVerticesMap(new Pair[getNumNodes()]);

            for (int i = 0; i < getNumNodes(); i++) {
                getVerticesMap()[i] = new Pair(text.nextInt(), text.nextInt());
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

        setNumAnts(getNumNodes() / 2);
        setNumGeneration(getNumNodes() / 2);

        setRemainsFuel(FloydWarshall.getRemainsFuel(getNumNodes(), getNodesMap(), getFuelLevels(), getMaxFuel(), getTargetNode()));
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getQ() {
        return q;
    }

    public void setQ(double q) {
        this.q = q;
    }

    public double getPheromonePersistence() {
        return pheromonePersistence;
    }

    public void setPheromonePersistence(double pheromonePersistence) {
        this.pheromonePersistence = pheromonePersistence;
    }

    public double getInitialPheromones() {
        return initialPheromones;
    }

    public void setInitialPheromones(double initialPheromones) {
        this.initialPheromones = initialPheromones;
    }

    public int getStartNode() {
        return startNode;
    }

    public void setStartNode(int startNode) {
        this.startNode = startNode;
    }

    public int getNumGeneration() {
        return numGeneration;
    }

    public void setNumGeneration(int numGeneration) {
        this.numGeneration = numGeneration;
    }

    public int getNumAnts() {
        return numAnts;
    }

    public void setNumAnts(int numAnts) {
        this.numAnts = numAnts;
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
