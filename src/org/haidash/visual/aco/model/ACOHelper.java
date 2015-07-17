package org.haidash.visual.aco.model;

import com.carrotsearch.hppc.IntArrayList;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.model.entity.ACOParameters;
import org.haidash.visual.aco.model.entity.FloydWarshall;
import org.haidash.visual.aco.model.entity.Graph;
import org.haidash.visual.aco.model.entity.SearchResult;

/**
 * Created by zonclayv on 17.07.15.
 */
public class ACOHelper {

    private final static Logger LOGGER = Logger.getLogger(ACOHelper.class);
    private final static ACOParameters ACO_PARAMETERS = ACOParameters.INSTANCE;


    public static void runACO(final Graph graph) {

        final long startTime = System.currentTimeMillis();

        LOGGER.info("PROCESS START '" + graph.getStartNode() + "' -> '" + graph.getTargetNode() + "'...");

        final IntArrayList remainsFuel = FloydWarshall.getRemainsFuel(graph, ACO_PARAMETERS.getMaxFuelLevels());

        LOGGER.info("FloydWarshall initialized...");

        SearchResult globalResult = null;

        final int numGeneration = ACO_PARAMETERS.getNumGeneration().get();

        for (int i = 0; i < numGeneration; i++) {

            final AntColony colony = new AntColony(graph, remainsFuel);
            final SearchResult result = colony.run();

            if (isNewResult(globalResult, result)) {
                globalResult = result;
                LOGGER.info("New path (Population " + i + ") " + result.getTotalCost() + " " + result.getPath());
            }
        }

        final long finishTime = System.currentTimeMillis() - startTime;

        LOGGER.info("PROCESS FINISH (" + finishTime + "ms):");

        if (globalResult == null) {
            LOGGER.info("Path not found");
        } else {
            LOGGER.info("Best path: " + globalResult.getTotalCost());
        }
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

    private static boolean isNewResult(final SearchResult old, final SearchResult newResult) {

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
}
