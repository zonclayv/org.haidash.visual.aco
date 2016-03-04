package org.haidash.visual.aco.model;

import com.carrotsearch.hppc.IntArrayList;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.model.entity.*;
import org.haidash.visual.aco.model.impl.ClassicalAnt;
import org.haidash.visual.aco.model.impl.ScoutAnt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zonclayv on 17.07.15.
 */
public class ACOUtils {

    private final static Logger LOGGER = Logger.getLogger(ACOUtils.class);
    private final static ACOParameters ACO_PARAMETERS = ACOParameters.INSTANCE;


    public static void runACO(final Graph graph) {

        final long startTime = System.currentTimeMillis();

        LOGGER.info("PROCESS START '" + graph.getStartNode() + "' -> '" + graph.getTargetNode() + "'...");

        final IntArrayList remainsFuel = FloydWarshall.getRemainsFuel(graph, ACO_PARAMETERS.getMaxFuelLevels());

        processScoutAnts(graph);

        LOGGER.info("FloydWarshall initialized...");

        SearchResult globalResult = null;

        final int numGeneration = ACO_PARAMETERS.getNumGeneration().get();

        for (int i = 0; i < numGeneration; i++) {

            final AntColony colony = new AntColony(graph, remainsFuel);
            final SearchResult result = colony.run();

            if (isNewResult(globalResult, result)) {
                globalResult = result;
                LOGGER.info("New path (Population " + i+1 + ") " + result.getTotalCost() + " " + result.getPath());
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

    private static void processScoutAnts(final Graph graph){
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

    public static Cycle findCycle(final Link newLink, final List<Link> path) {

        final List<Link> visitedLinks = new ArrayList<>();

        int fuel = newLink.getFirst().getFuelBalance() - newLink.getWeight();

        boolean isFindCycle = false;

        for (Link pathLink : path) {

            if ((!pathLink.getFirst().equals(newLink.getSecond()) || pathLink.equals(newLink)) && !isFindCycle) {
                continue;
            }

            isFindCycle = true;

            if (!visitedLinks.contains(pathLink)) {
                fuel += pathLink.getFirst().getFuelBalance();
            }

            visitedLinks.add(pathLink);

            fuel -= pathLink.getWeight();
        }

        if (isFindCycle) {

            visitedLinks.add(newLink);
            fuel -= newLink.getWeight();

            final Cycle cycle = new Cycle();
            cycle.setStartNode(newLink.getSecond());
            cycle.setFuel(fuel);
            cycle.setLinks(visitedLinks);

            return cycle;
        }

        return null;
    }

}
