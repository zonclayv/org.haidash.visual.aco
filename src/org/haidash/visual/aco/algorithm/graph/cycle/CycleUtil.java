package org.haidash.visual.aco.algorithm.graph.cycle;

import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zonclayv on 05.03.16.
 */
public class CycleUtil {

    public static void addCycle(final Map<Node, Cycle> cycles, final Cycle cycle) {

        final Cycle oldCycle = cycles.get(cycle.getStartNode());

        if (oldCycle != null) {

            final int oldCycleFuel = oldCycle.getFuel();
            final int cycleFuel = cycle.getFuel();

            if ((oldCycleFuel > cycleFuel)) {
                return;
            }

            if (!((oldCycleFuel == cycleFuel) && (oldCycle.getLinks().size() > cycle.getLinks().size()))) {
                return;
            }
        }

        cycles.put(cycle.getStartNode(), cycle);
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
