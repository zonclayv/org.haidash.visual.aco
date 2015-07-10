package org.haidash.visual.aco.oop.entity;

import javafx.beans.property.SimpleIntegerProperty;

public class Properties {

	public static Properties getInstance() {

		if (instance == null) {
			instance = new Properties();
		}

		return instance;
	}

	private static Properties instance;

	private final SimpleIntegerProperty alpha = new SimpleIntegerProperty(1);

	private final SimpleIntegerProperty beta = new SimpleIntegerProperty(3);
	private final SimpleIntegerProperty q = new SimpleIntegerProperty(20);
	private final SimpleIntegerProperty numGeneration = new SimpleIntegerProperty(15);

	private final SimpleIntegerProperty numAnts = new SimpleIntegerProperty(30);
	private double pheromonePersistence = 0.5;

	private int maxFuelLevels = 6;

	private Properties() {
	}

	public SimpleIntegerProperty getAlpha() {
		return alpha;
	}

	public SimpleIntegerProperty getBeta() {
		return beta;
	}

	public int getMaxFuelLevels() {
		return maxFuelLevels;
	}

	public SimpleIntegerProperty getNumAnts() {
		return numAnts;
	}

	public SimpleIntegerProperty getNumGeneration() {
		return numGeneration;
	}

	public double getPheromonePersistence() {
		return pheromonePersistence;
	}

	public SimpleIntegerProperty getQ() {
		return q;
	}

	public void setMaxFuelLevels(final int maxFuelLevels) {
		this.maxFuelLevels = maxFuelLevels;
	}

	public void setPheromonePersistence(final double pheromonePersistence) {
		this.pheromonePersistence = pheromonePersistence;
	}

}
