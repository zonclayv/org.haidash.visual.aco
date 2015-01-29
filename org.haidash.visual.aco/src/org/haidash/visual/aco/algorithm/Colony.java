package org.haidash.visual.aco.algorithm;

import javafx.beans.property.SimpleObjectProperty;

import org.apache.log4j.Logger;
import org.haidash.visual.aco.algorithm.model.AcoProperties;
import org.haidash.visual.aco.algorithm.model.Cycle;
import org.haidash.visual.aco.algorithm.model.Pair;
import org.haidash.visual.aco.algorithm.model.SearchResult;

import java.util.*;

public class Colony {

    private static final Logger LOGGER = Logger.getLogger(Colony.class);
    private final SimpleObjectProperty<Pair>[][] globalPheromones;
    private final Map<Integer, Cycle> cycles;
    private final Set<List<Integer>> badPaths;

    private SearchResult searchResult;

    public Colony() {
        this.cycles = new HashMap<>();
        this.badPaths = new HashSet<>();
        this.globalPheromones = initGlobalPheromones();
    }

    public SimpleObjectProperty[][] getGlobalPheromones() {
        return globalPheromones;
    }

    @SuppressWarnings("unchecked")
    private SimpleObjectProperty[][] initGlobalPheromones() {

        final AcoProperties properties = AcoProperties.getInstance();
        final int numNodes = properties.getNumNodes();

        final SimpleObjectProperty[][] result = new SimpleObjectProperty[numNodes][numNodes];

        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {

                result[i][j]=new SimpleObjectProperty();
                result[i][j].setValue(new Pair<>(1.0, 1.0));
            }
        }

        return result;
    }

    public void start() {

        final AcoProperties properties = AcoProperties.getInstance();

        LOGGER.info("PROCESS START '" + properties.getStartNode() + "' -> '" + properties.getTargetNode() + "'...");

        final long startTime = System.currentTimeMillis();

        for (int i = 0; i < properties.getNumGeneration(); i++) {

            final Generation generation = new Generation(badPaths, cycles, globalPheromones);
            searchResult = generation.start();

            updatePheromones();
        }

        final long finishTime = System.currentTimeMillis() - startTime;

        LOGGER.info("PROCESS FINISH (" + finishTime + "ms):");

        if (searchResult == null) {
            LOGGER.info("Path not found");
        } else {
            LOGGER.info("Best path: " + searchResult.getTotalCost());
            LOGGER.info(searchResult.getVisited().toString());
        }
    }

    public SearchResult getSearchResult() {
        return searchResult;
    }

    private void updatePheromones() {

        final AcoProperties properties = AcoProperties.getInstance();
        final int numNodes = properties.getNumNodes();

        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {

                final Pair<Double, Double> pheromon = globalPheromones[i][j].getValue();

                double pValue = pheromon.first;
                double nValue = pheromon.second;

                pValue *= 1.0 - properties.getPheromonePersistence();

                if (pValue < 1.0) {
                    pValue = 1.0;
                }

                nValue *= 1.0 - properties.getPheromonePersistence();

                if (nValue < 1.0) {
                    nValue = 1.0;
                }

                globalPheromones[i][j].setValue(new Pair<>(pValue, nValue));
            }
        }
    }
}
