package org.haidash.visual.aco.algorithm;

import java.io.File;

import org.haidash.visual.aco.algorithm.model.AcoProperties;

public class Test {

	public static void main(final String[] args) {
		File file = new File("files/new.txt");
		AcoProperties.getInstance().initializeValue(file, false);
		Colony ac = new Colony();
		ac.start();
	}

}
