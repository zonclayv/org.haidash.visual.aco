package org.haidash.visual.aco.model.entity;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.model.Agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchResult {

    private final IntArrayList spentFuelLevel;
    private final List<Link> path;
    private final int totalCost;

    public SearchResult(final Agent agent) {
        this.spentFuelLevel = new IntArrayList(agent.getSpentFuelLevel());
        this.path = new ArrayList<>(agent.getPath());
        this.totalCost = agent.getTotalCost();
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

        if (!Objects.equals(path,other.path)) {
            return false;
        }

        if (!Objects.equals(spentFuelLevel,other.spentFuelLevel)) {
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
        result = (prime * result) + (path.hashCode());
        result = (prime * result) + (spentFuelLevel.hashCode());
        result = (prime * result) + totalCost;

        return result;
    }

}
