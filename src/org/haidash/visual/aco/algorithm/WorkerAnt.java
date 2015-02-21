package org.haidash.visual.aco.algorithm;

import java.util.Random;

import org.haidash.visual.aco.algorithm.model.AcoProperties;

public class WorkerAnt extends AbstractAnt {

	private static final Random RANDOM = new Random(System.nanoTime());

	public WorkerAnt(final Generation generation) {
		super(generation);
	}


	@Override
	protected int getStartNode() {
		final AcoProperties properties = AcoProperties.getInstance();
		int targetNode = properties.getTargetNode();
		int numNodes = properties.getNumNodes();

		int randNode = 0;

		do {
			randNode = RANDOM.nextInt(numNodes - 1);
		} while ((randNode == targetNode) || (randNode > (numNodes - 1)));

		return randNode;
	}
}
