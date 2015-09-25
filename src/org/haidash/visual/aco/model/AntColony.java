package org.haidash.visual.aco.model;

import com.carrotsearch.hppc.IntArrayList;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.model.entity.ACOParameters;
import org.haidash.visual.aco.model.entity.Graph;
import org.haidash.visual.aco.model.entity.Link;
import org.haidash.visual.aco.model.entity.SearchResult;
import org.haidash.visual.aco.model.impl.ClassicalAnt;

import java.util.ArrayList;
import java.util.List;

public class AntColony {

    //    private final static Logger LOGGER = Logger.getLogger(AntColony.class);
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

            final Agent agent = new ClassicalAnt(graph, remainsFuel);
            agent.run();

            agents.add(agent);

            if (agent.isOutOfFuel()) {
                // LOGGER.debug("Out of fuel " + agent.getPath());
                continue;
            }

//            LOGGER.debug("New path (Population " + populationIndex + ")" + agent.getTotalCost() + " " + agent.getPath());
//            LOGGER.info("Path " + agent.getTotalCost());

            if (ACOUtils.isNewResult(result, agent.getTotalCost(), agent.getPath().size())) {
                result = new SearchResult(agent);
            }
        }

        agents.forEach(this::updatePheromones);

        processPheromonePersistence();

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

    private void processPheromonePersistence() {

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
