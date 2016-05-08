package org.haidash.visual.aco.algorithm.graph.entity;

import org.haidash.visual.aco.ui.graph.NodeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

    private NodeView view;

    private int number;
    private int fuelBalance;
    private Map<String, Object> properties;
    private final List<Link> outgoingLink;
    private final List<Link> ingoingLink;

    public Node(final int number, final int fuelBalance) {
        this.number = number;
        this.fuelBalance = fuelBalance;
        this.outgoingLink = new ArrayList<>();
        this.ingoingLink = new ArrayList<>();
        this.properties = new HashMap<>();
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

        final Node other = (Node) obj;

        if (number != other.number) {
            return false;
        }

        return true;
    }

    public int getFuelBalance() {
        return fuelBalance;
    }

    public int getNumber() {
        return number;
    }

    public List<Link> getOutgoingLinks() {
        return outgoingLink;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    @Override
    public int hashCode() {

        final int prime = 31;

        int result = 1;
        result = (prime * result) + number;

        return result;
    }

    public void setFuelBalance(final int fuelBalance) {
        this.fuelBalance = fuelBalance;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }

    public void clear() {
        properties.clear();
    }

    public List<Link> getIngoingLink() {
        return ingoingLink;
    }

    public NodeView getView() {
        return view;
    }

    public void setView(NodeView view) {
        this.view = view;
    }
}
