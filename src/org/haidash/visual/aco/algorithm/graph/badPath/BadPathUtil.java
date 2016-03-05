package org.haidash.visual.aco.algorithm.graph.badPath;

import org.haidash.visual.aco.algorithm.graph.entity.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by zonclayv on 05.03.16.
 */
public class BadPathUtil {


    public static boolean isBadPath(final Set<List<Link>> badPaths, final List<Link> path, final Link nextArc) {

        final List<Link> nodes = new ArrayList<>(path);
        nodes.add(nextArc);

        return badPaths.contains(nodes);
    }


    public static boolean isBadPath(final Set<List<Link>> badPaths, final List<Link> path, final List<Link> cycleArcs, final Link nextArc) {

        final List<Link> links = new ArrayList<>(path);
        links.addAll(cycleArcs);
        links.add(nextArc);

        return badPaths.contains(links);
    }
}
