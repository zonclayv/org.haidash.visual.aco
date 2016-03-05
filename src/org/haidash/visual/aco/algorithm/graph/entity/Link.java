package org.haidash.visual.aco.algorithm.graph.entity;

public class Link {

    private Node first;
    private Node second;

    private double pPheromone;
    private double nPheromone;

    private int visitsCount;
    private int weight;

    public Link(final int weight) {
        this.weight = weight;
        this.pPheromone = 1.0;
        this.nPheromone = 1.0;
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

        final Link other = (Link) obj;

        if (first == null) {
            if (other.first != null) {
                return false;
            }
        } else if (!first.equals(other.first)) {
            return false;
        }

        if (second == null) {
            if (other.second != null) {
                return false;
            }
        } else if (!second.equals(other.second)) {
            return false;
        }

        if (weight != other.weight) {
            return false;
        }

        return true;
    }

    public Node getFirst() {
        return first;
    }

    public double getNPheromone() {
        return nPheromone;
    }

    public double getPPheromone() {
        return pPheromone;
    }

    public Node getSecond() {
        return second;
    }

    public int getVisitsCount() {
        return visitsCount;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int hashCode() {

        final int prime = 31;

        int result = 1;
        result = (prime * result) + ((first == null) ? 0 : first.hashCode());
        result = (prime * result) + ((second == null) ? 0 : second.hashCode());
        result = (prime * result) + weight;

        return result;
    }

    public void setFirst(final Node first) {
        this.first = first;
    }

    public void setNPheromone(final double nPheromone) {
        this.nPheromone = nPheromone;
    }

    public void setPPheromone(final double pPheromone) {
        this.pPheromone = pPheromone;
    }

    public void setSecond(final Node second) {
        this.second = second;
    }

    public void setVisitsCount(final int visitsCount) {
        this.visitsCount = visitsCount;
    }

    public void setWeight(final int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "[" + first.getNumber() + ", " + second.getNumber() + "]";
    }

}
