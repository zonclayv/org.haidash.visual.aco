package org.haidash.visual.aco.algorithm.util;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.algorithm.agent.Agent;
import org.haidash.visual.aco.algorithm.graph.entity.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zonclayv on 07.05.16.
 */
public class Solution {

    private int generation;
    private int totalCost;
    private long time;

    private final IntArrayList spentFuelLevel;
    private final List<Link> path;

    public Solution() {
        this.spentFuelLevel = new IntArrayList();
        this.path = new ArrayList<>();
    }

    public Solution(final Agent agent, int generation, long time) {
        this.spentFuelLevel = new IntArrayList(agent.getSpentFuelLevel());
        this.path = new ArrayList<>(agent.getPath());
        this.totalCost = agent.getTotalCost();
        this.generation = generation;
        this.time = time;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getGeneration() {
        return generation;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public long getTime() {
        return time;
    }

    public static boolean isNewBetter(Solution old, final int totalCost, final int pathSize) {

        if (old == null) {
            return true;
        }

        final int oldTotalCost = old.getTotalCost();

        if (totalCost < oldTotalCost) {
            return true;
        }

        final int oldPathSize = old.getPath().size();

        return totalCost < oldTotalCost && pathSize < oldPathSize;
    }

    public static boolean isNewBetter(final Solution old, final Solution newSolution) {

        if (newSolution == null) {
            return false;
        }

        if (old == null) {
            return true;
        }

        final int totalCost = newSolution.getTotalCost();
        final int oldTotalCost = old.getTotalCost();

        if (totalCost < oldTotalCost) {
            return true;
        }

        final int pathSize = newSolution.getPath().size();
        final int oldPathSize = old.getPath().size();

        return totalCost < oldTotalCost && pathSize < oldPathSize;
    }

    public List<Link> getPath() {
        return path;
    }

    public IntArrayList getSpentFuelLevel() {
        return spentFuelLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Solution solution = (Solution) o;

        if (generation != solution.generation) return false;
        if (totalCost != solution.totalCost) return false;
        if (time != solution.time) return false;
        if (spentFuelLevel != null ? !spentFuelLevel.equals(solution.spentFuelLevel) : solution.spentFuelLevel != null)
            return false;
        return !(path != null ? !path.equals(solution.path) : solution.path != null);

    }

    @Override
    public int hashCode() {
        int result = generation;
        result = 31 * result + totalCost;
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + (spentFuelLevel != null ? spentFuelLevel.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}
