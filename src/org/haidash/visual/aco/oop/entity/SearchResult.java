package org.haidash.visual.aco.oop.entity;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.oop.Agentable;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    private final IntArrayList spentFuelLevel;
    private final List<Link> path;
    private final int totalCost;

    public SearchResult(final Agentable agent) {
        this.spentFuelLevel = new IntArrayList(agent.getSpentFuelLevel());
        this.path = new ArrayList<>(agent.getPath());
        this.totalCost = agent.getTotalCost();
    }

    public SearchResult(final IntArrayList spentFuelLevel, final List<Link> path, final int totalCost) {
        this.spentFuelLevel = new IntArrayList(spentFuelLevel);
        this.path = new ArrayList<>(path);
        this.totalCost = totalCost;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final SearchResult other = (SearchResult) obj;

        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }

        if (spentFuelLevel == null) {
            if (other.spentFuelLevel != null) {
                return false;
            }
        } else if (!spentFuelLevel.equals(other.spentFuelLevel)) {
            return false;
        }

        if (totalCost != other.totalCost) {
            return false;
        }

        return true;
    }

    public List<Link> getPath() {
        return path;
    }

    public IntArrayList getSpentFuelLevel() {
        return spentFuelLevel;
    }

    public int getTotalCost() {
        return totalCost;
    }

    @Override
    public int hashCode() {
        final int prime = 31;

        int result = 1;
        result = (prime * result) + ((path == null) ? 0 : path.hashCode());
        result = (prime * result) + ((spentFuelLevel == null) ? 0 : spentFuelLevel.hashCode());
        result = (prime * result) + totalCost;

        return result;
    }

}
