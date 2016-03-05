package org.haidash.visual.aco.algorithm.aco.entity;

import java.util.List;

public class Cycle {

    private Node startNode;
    private List<Link> links;
    private int fuel;

    public Cycle() {
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

        final Cycle other = (Cycle) obj;

        if (links == null) {
            if (other.links != null) {
                return false;
            }
        } else if (!links.equals(other.links)) {
            return false;
        }

        if (fuel != other.fuel) {
            return false;
        }

        if (startNode == null) {
            if (other.startNode != null) {
                return false;
            }
        } else if (!startNode.equals(other.startNode)) {
            return false;
        }

        return true;
    }

    public int getFuel() {
        return fuel;
    }

    public List<Link> getLinks() {
        return links;
    }

    public Node getStartNode() {
        return startNode;
    }

    @Override
    public int hashCode() {

        final int prime = 31;

        int result = 1;
        result = (prime * result) + ((links == null) ? 0 : links.hashCode());
        result = (prime * result) + fuel;
        result = (prime * result) + ((startNode == null) ? 0 : startNode.hashCode());

        return result;
    }

    public void setFuel(final int fuel) {
        this.fuel = fuel;
    }

    public void setLinks(final List<Link> links) {
        this.links = links;
    }

    public void setStartNode(final Node startNode) {
        this.startNode = startNode;
    }

}
