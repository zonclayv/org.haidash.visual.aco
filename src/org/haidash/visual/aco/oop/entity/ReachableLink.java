package org.haidash.visual.aco.oop.entity;

public class ReachableLink {

    private final Node node;
    private final double value;
    private final Link link;

    public ReachableLink(final Link link, final double value) {
        this.node = link.getSecond();
        this.link = link;
        this.value = value;
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

        final ReachableLink other = (ReachableLink) obj;

        if (link == null) {
            if (other.link != null) {
                return false;
            }
        } else if (!link.equals(other.link)) {
            return false;
        }

        return Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value);

    }

    public Link getLink() {
        return link;
    }

    public Node getNode() {
        return node;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int hashCode() {

        final int prime = 31;

        int result = 1;
        result = (prime * result) + ((link == null) ? 0 : link.hashCode());

        long temp;
        temp = Double.doubleToLongBits(value);

        result = (prime * result) + (int) (temp ^ (temp >>> 32));

        return result;
    }
}
