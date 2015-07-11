package org.haidash.visual.aco.oop.impl;

import com.carrotsearch.hppc.IntArrayList;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.oop.Agentable;
import org.haidash.visual.aco.oop.AntColonyOptimization;
import org.haidash.visual.aco.oop.entity.*;

import java.util.ArrayList;
import java.util.List;

public class Colony implements AntColonyOptimization {

    private final static Logger LOGGER = Logger.getLogger(Colony.class);
    private final static Properties properties = Properties.getInstance();

    private SearchResult getBestResult(final SearchResult globalResult, final SearchResult localResult, final int populationIndex) {

        if (globalResult == null) {

            if (localResult != null) {
                LOGGER.info("New path (Population " + populationIndex + ")" + localResult.getTotalCost() + " " + localResult.getPath());
            }

            return localResult;
        }

        if (localResult == null) {
            return globalResult;
        }

        boolean isBestTotalCost = localResult.getTotalCost() < globalResult.getTotalCost();
        boolean isEqualsTotalCost = localResult.getTotalCost() == globalResult.getTotalCost();
        boolean isLessNodes = localResult.getPath().size() < globalResult.getPath().size();

        if (isBestTotalCost || (isEqualsTotalCost && isLessNodes)) {
            LOGGER.info("New path (Population " + populationIndex + ")" + localResult.getTotalCost() + " " + localResult.getPath());

            return localResult;
        }

        return globalResult;
    }

    @Override
    public void run(final Graph graph) {

        SearchResult globalResult = null;

        LOGGER.info("PROCESS START '" + graph.getStartNode() + "' -> '" + graph.getTargetNode() + "'...");

        final long startTime = System.currentTimeMillis();

        final IntArrayList remainsFuel = FloydWarshall.getRemainsFuel(graph, properties.getMaxFuelLevels());

        LOGGER.info("FloydWarshall initialized...");

        for (int i = 0; i < properties.getNumGeneration().get(); i++) {

            final SearchResult localResult = runPopulation(graph, remainsFuel, i);

            globalResult = getBestResult(globalResult, localResult, i);
        }

        final long finishTime = System.currentTimeMillis() - startTime;

        LOGGER.info("PROCESS FINISH (" + finishTime + "ms):");

        if (globalResult == null) {
            LOGGER.info("Path not found");
        } else {
            LOGGER.info("Best path: " + globalResult.getTotalCost());
        }
    }

    private SearchResult runPopulation(final Graph graph, final IntArrayList remainsFuel, final int populationIndex) {

        final int numAnts = properties.getNumAnts().get();

        final List<Agentable> ants = new ArrayList<>(numAnts);

        for (int i = 0; i < numAnts; i++) {
            Ant ant = new Ant(graph, remainsFuel);
            ant.setCurrentNode(graph.getStartNode());
            ants.add(ant);
            ant.run();
        }

        SearchResult searchResult = null;

        for (Agentable agent : ants) {

            updatePheromones(agent);

            if (agent.isOutOfFuel()) {
//                LOGGER.debug("Out of fuel " + agent.getPath());

                continue;
            }

//            LOGGER.debug("New path (Population " + populationIndex + ")" + agent.getTotalCost() + " " + agent.getPath());

            if ((searchResult == null)
                    || ((agent.getTotalCost() < searchResult.getTotalCost()) || ((agent.getTotalCost() == searchResult.getTotalCost()) && (agent
                    .getPath().size() < searchResult.getPath().size())))) {

                searchResult = new SearchResult(agent);
            }
        }

        return searchResult;
    }

    @Override
    public void updatePheromones(final Agentable agent) {

        final List<Link> path = agent.getPath();

        double deltaTau = 0;

        if (agent.getTotalCost() != 0) {
            deltaTau = properties.getQ().get() / agent.getTotalCost();
        }

        for (Link link : path) {

            double pValue = link.getPPheromone();
            double nValue = link.getNPheromone();

            if (!agent.isOutOfFuel()) {
                pValue += deltaTau;
                link.setPPheromone(pValue);
            } else {
                nValue += deltaTau;
                link.setNPheromone(nValue);
            }
        }
    }

}
