package org.haidash.visual.aco.algorithm.util;

import org.apache.log4j.Logger;
import org.haidash.visual.aco.algorithm.agent.Agent;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.agent.impl.ScoutAnt;
import org.haidash.visual.aco.algorithm.graph.entity.Link;

import java.util.List;

/**
 * Created by zonclayv on 17.07.15.
 */
public class AlgorithmUtils {

    private final static Logger LOGGER = Logger.getLogger(AlgorithmUtils.class);
    private final static ACOParameters ACO_PARAMETERS = ACOParameters.INSTANCE;

    public static void processScoutAnts(final Graph graph) {
        final int numAnts = ACO_PARAMETERS.getNumAnts().get();

        for (int j = 0; j < numAnts; j++) {

            final Agent agent = new ScoutAnt(graph);
            agent.run();

            if (agent.isOutOfFuel()) {
                continue;
            }

            LOGGER.debug("New scout path " + agent.getPath());
        }

        LOGGER.info("Scout ants ware initializing...");
    }

    public static boolean isNewResult(SearchResult old, final int totalCost, final int pathSize) {

        if (old == null) {
            return true;
        }

        final int oldTotalCost = old.getTotalCost();

        if (totalCost < oldTotalCost) {
            return true;
        }

        final int oldPathSize = old.getPath().size();

        return (totalCost == oldTotalCost) && (pathSize < oldPathSize);
    }

    public static boolean isNewResult(final SearchResult old, final SearchResult newResult) {

        if (newResult == null) {
            return false;
        }

        if (old == null) {
            return true;
        }

        final int totalCost = newResult.getTotalCost();
        final int oldTotalCost = old.getTotalCost();

        if (totalCost < oldTotalCost) {
            return true;
        }

        final int pathSize = newResult.getPath().size();
        final int oldPathSize = old.getPath().size();

        return (totalCost == oldTotalCost) && (pathSize < oldPathSize);
    }


    public static void updatePheromones(final Agent agent) {

        final List<Link> path = agent.getPath();

        double deltaTau = 0;

        if (agent.getTotalCost() != 0) {
            if (!agent.isOutOfFuel()) {
                deltaTau = (ACO_PARAMETERS.getQ().get() < agent.getTotalCost()) ? agent.getTotalCost() / ACO_PARAMETERS.getQ().get() : ACO_PARAMETERS.getQ().get() / agent.getTotalCost();
            } else {
                deltaTau = ACO_PARAMETERS.getQ().get() / agent.getTotalCost();
            }

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

    public static void processPheromonePersistence(final Graph graph) {

        final List<Link> links = graph.getLinks();

        for (final Link link : links) {


            double pValue = link.getPPheromone();
            double nValue = link.getPPheromone();

            pValue *= 1.0 - ACO_PARAMETERS.getPheromonePersistence();

            if (pValue < 1.0) {
                pValue = 1.0;
            }

            link.setPPheromone(pValue);

            nValue *= 1.0 - ACO_PARAMETERS.getPheromonePersistence();

            if (nValue < 1.0) {
                nValue = 1.0;
            }

            link.setNPheromone(nValue);
        }
    }
}
