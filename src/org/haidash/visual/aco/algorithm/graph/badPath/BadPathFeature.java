package org.haidash.visual.aco.algorithm.graph.badPath;

import org.haidash.visual.aco.algorithm.graph.entity.Link;

import java.util.List;
import java.util.Set;

/**
 * Created by zonclayv on 05.03.16.
 */
public interface BadPathFeature {
    boolean isBadPath(final List<Link> path, final Link nextArc);
    boolean isBadPath(final List<Link> path, final List<Link> cycleArcs, final Link nextArc);
    Set<List<Link>> getBadPaths();
}
