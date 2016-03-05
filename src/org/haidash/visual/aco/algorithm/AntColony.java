package org.haidash.visual.aco.algorithm;

import com.carrotsearch.hppc.IntArrayList;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.algorithm.agent.Agent;
import org.haidash.visual.aco.algorithm.agent.impl.CycleAndBadPathAnt;
import org.haidash.visual.aco.algorithm.graph.cycle.Cycle;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.preprocessing.FloydWarshall;
import org.haidash.visual.aco.algorithm.util.ACOParameters;
import org.haidash.visual.aco.algorithm.util.SearchResult;
import org.haidash.visual.aco.algorithm.util.Utils;

import java.util.*;

public class AntColony {

    private final static Logger LOGGER = Logger.getLogger(AntColony.class);
    private final static ACOParameters ACO_PARAMETERS = ACOParameters.INSTANCE;

    private final Graph graph;

    public AntColony(final Graph graph) {
        this.graph = graph;
    }

    public SearchResult run() {

        final long startTime = System.currentTimeMillis();

        LOGGER.info("PROCESS START '" + graph.getStartNode() + "' -> '" + graph.getTargetNode() + "'...");

        final IntArrayList remainsFuel = FloydWarshall.getRemainsFuel(graph, ACO_PARAMETERS.getMaxFuelLevels());

        Utils.processScoutAnts(graph);

        LOGGER.info("FloydWarshall initialized...");

        SearchResult globalResult = null;


        final Map<Node, Cycle> cycles = new HashMap<>();
        final Set<List<Link>> badPaths = new HashSet<>();

        final int numGeneration = ACO_PARAMETERS.getNumGeneration().get();

        for (int i = 0; i < numGeneration; i++) {

            SearchResult result = null;

            final int numAnts = ACO_PARAMETERS.getNumAnts().get();
            final List<Agent> agents = new ArrayList<>(numAnts);

            for (int j = 0; j < numAnts; j++) {

                final Agent agent = new CycleAndBadPathAnt(graph, remainsFuel, cycles, badPaths);
                agent.run();

                agents.add(agent);

                if (agent.isOutOfFuel()) {
//                  LOGGER.debug("Out of fuel " + agent.getPath());
                    continue;
                }

//              LOGGER.debug("New path " + agent.getTotalCost() + " " + agent.getPath());
//              LOGGER.info("Path " + agent.getTotalCost());

                if (Utils.isNewResult(result, agent.getTotalCost(), agent.getPath().size())) {
                    result = new SearchResult(agent);
                }
            }

            agents.forEach(Utils::updatePheromones);

            Utils.processPheromonePersistence(graph);

            if (Utils.isNewResult(globalResult, result)) {
                globalResult = result;
                LOGGER.info("New path (Population " + i + 1 + ") " + result.getTotalCost() + " " + result.getPath());
            }
        }

        final long finishTime = System.currentTimeMillis() - startTime;

        LOGGER.info("PROCESS FINISH (" + finishTime + "ms):");

        if (globalResult == null) {
            LOGGER.info("Path not found");
        } else {
            LOGGER.info("Best path: " + globalResult.getTotalCost());
        }

        return globalResult;
    }
}
