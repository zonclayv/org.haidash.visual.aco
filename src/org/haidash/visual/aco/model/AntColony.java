package org.haidash.visual.aco.model;

import com.carrotsearch.hppc.IntArrayList;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.model.entity.ACOParameters;
import org.haidash.visual.aco.model.entity.Graph;
import org.haidash.visual.aco.model.entity.Link;
import org.haidash.visual.aco.model.entity.SearchResult;
import org.haidash.visual.aco.model.impl.Ant;

import java.util.ArrayList;
import java.util.List;

public class AntColony {

    private final static Logger LOGGER = Logger.getLogger(AntColony.class);
    private final static ACOParameters ACO_PARAMETERS = ACOParameters.INSTANCE;

    private final Graph graph;
    private final IntArrayList remainsFuel;

    public AntColony(final Graph graph, final IntArrayList remainsFuel) {
        this.graph = graph;
        this.remainsFuel = remainsFuel;
    }

    public SearchResult run() {

        SearchResult result = null;

        final int numAnts = ACO_PARAMETERS.getNumAnts().get();
        final List<Agent> agents = new ArrayList<>(numAnts);

        for (int j = 0; j < numAnts; j++) {

            final Agent agent = new Ant(graph, remainsFuel);
            agent.run();

            agents.add(agent);

            if (agent.isOutOfFuel()) {
                // LOGGER.debug("Out of fuel " + agent.getPath());
                continue;
            }

            // LOGGER.debug("New path (Population " + populationIndex + ")" + agent.getTotalCost() + " " + agent.getPath());
            // LOGGER.info("Path (Population " + populationIndex + ")" + agent.getTotalCost());

            if (ACOHelper.isNewResult(result, agent.getTotalCost(), agent.getPath().size())) {
                result = new SearchResult(agent);
            }
        }

        for (Agent agent : agents) {
            updatePheromones(agent);
        }

        return result;
    }

    private void updatePheromones(final Agent agent) {

        final List<Link> path = agent.getPath();

        double deltaTau = 0;

        if (agent.getTotalCost() != 0) {
            deltaTau = ACO_PARAMETERS.getQ().get() / agent.getTotalCost();
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
