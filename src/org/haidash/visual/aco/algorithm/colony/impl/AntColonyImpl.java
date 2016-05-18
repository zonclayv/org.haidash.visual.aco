package org.haidash.visual.aco.algorithm.colony.impl;

import com.carrotsearch.hppc.IntArrayList;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.algorithm.agent.Agent;
import org.haidash.visual.aco.algorithm.agent.impl.ClassicalAnt;
import org.haidash.visual.aco.algorithm.agent.impl.CycleAndBadPathAnt;
import org.haidash.visual.aco.algorithm.agent.impl.ScoutAnt;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.cycle.Cycle;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.preprocessing.FloydWarshall;
import org.haidash.visual.aco.algorithm.util.*;

import java.util.*;

public class AntColonyImpl implements org.haidash.visual.aco.algorithm.colony.AntColony {

    private final static Logger LOGGER = Logger.getLogger(AntColonyImpl.class);

    private final static ACOParameters ACO_PARAMETERS = ACOParameters.INSTANCE;

    private final Graph graph;

    public AntColonyImpl(final Graph graph) {
        this.graph = graph;
    }

    public SearchHistory run() {

        graph.clear();

        final SearchHistory searchHistory = new SearchHistory();
        searchHistory.setGraphName(graph.getGraphName());
        searchHistory.setGraphSize(graph.getGraphSize());
        searchHistory.setQ(ACO_PARAMETERS.getQ().get());
        searchHistory.setAlpha(ACO_PARAMETERS.getAlpha().get());
        searchHistory.setBeta(ACO_PARAMETERS.getBeta().get());
        searchHistory.setAntCount(ACO_PARAMETERS.getNumAnts().get());
        searchHistory.setGenerationCount(ACO_PARAMETERS.getNumGeneration().get());
        searchHistory.setStartNode(graph.getStartNode().getNumber());
        searchHistory.setTargetNode(graph.getTargetNode().getNumber());

        runClassicAnts(searchHistory);

        graph.clear();

        runModACO(searchHistory);

        return searchHistory;
    }
    public void processScoutAnts() {
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

    private void runModACO(SearchHistory searchHistory) {

        LOGGER.info("Running modification ants... ");

        final long startTime = System.currentTimeMillis();

        LOGGER.info("PROCESS START '" + graph.getStartNode() + "' -> '" + graph.getTargetNode() + "'...");

        final IntArrayList remainsFuel = FloydWarshall.getRemainsFuel(graph, ACO_PARAMETERS.getMaxFuelLevels());

        processScoutAnts();

        LOGGER.info("FloydWarshall initialized...");

        Solution globalSolution = null;

        final Map<Node, Cycle> cycles = new HashMap<>();
        final Set<List<Link>> badPaths = new HashSet<>();

        final int numGeneration = ACO_PARAMETERS.getNumGeneration().get();

        for (int i = 0; i < numGeneration; i++) {

            final int generationNumber = i + 1;

            Solution localSolution = null;

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

                if (Solution.isNewBetter(localSolution, agent.getTotalCost(), agent.getPath().size())) {
                    final long runTime = System.currentTimeMillis() - startTime;
                    localSolution = new Solution(agent, generationNumber, runTime);
                }
            }

            agents.forEach(AlgorithmUtils::updatePheromones);

            AlgorithmUtils.processPheromonePersistence(graph);

            if (Solution.isNewBetter(globalSolution, localSolution)) {
                globalSolution = localSolution;
                LOGGER.info("New path (Population " + generationNumber + ") " + globalSolution.getTotalCost() + " " + globalSolution.getPath());

                searchHistory.addSolution(localSolution);
            }
        }

        final long finishTime = System.currentTimeMillis() - startTime;

        LOGGER.info("PROCESS FINISH (" + finishTime + "ms):");

        if (globalSolution == null) {
            LOGGER.info("Path not found");
        } else {
            globalSolution.setTime(finishTime);
            LOGGER.info("Best path: " + globalSolution.getTotalCost());
        }
    }

    private void runClassicAnts(SearchHistory searchHistory) {

        LOGGER.info("Running classical ants... ");

        final long startTime = System.currentTimeMillis();

        Solution globalSolution = null;

        final int numGeneration = ACO_PARAMETERS.getNumGeneration().get();

        for (int i = 0; i < numGeneration; i++) {

            final int generationNumber = i + 1;

            Solution localSolution = null;

            final int numAnts = ACO_PARAMETERS.getNumAnts().get();
            final List<Agent> agents = new ArrayList<>(numAnts);

            for (int j = 0; j < numAnts; j++) {

                final Agent agent = new ClassicalAnt(graph);
                agent.run();

                agents.add(agent);

                if (agent.isOutOfFuel()) {
//                  LOGGER.debug("Out of fuel " + agent.getPath());
                    continue;
                }

//              LOGGER.debug("New path " + agent.getTotalCost() + " " + agent.getPath());
//              LOGGER.info("Path " + agent.getTotalCost());

                if (Solution.isNewBetter(localSolution, agent.getTotalCost(), agent.getPath().size())) {
                    final long runTime = System.currentTimeMillis() - startTime;
                    localSolution = new Solution(agent, generationNumber, runTime);
                }
            }

            agents.forEach(AlgorithmUtils::updatePheromones);

            AlgorithmUtils.processPheromonePersistence(graph);

            if (Solution.isNewBetter(globalSolution, localSolution)) {
                globalSolution = localSolution;
                LOGGER.info("New classical path (Population " + generationNumber + ") " + globalSolution.getTotalCost() + " " + globalSolution.getPath());
            }
        }


        searchHistory.setClassicalSolution(globalSolution);
    }
}
