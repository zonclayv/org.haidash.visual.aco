package org.haidash.visual.aco.algorithm;

import org.haidash.visual.aco.algorithm.model.AcoProperties;

public class FinderAnt extends AbstractAnt {


	public FinderAnt(final Generation generation) {
		super(generation);
	}

	@Override
	protected int getStartNode() {
		final AcoProperties properties = AcoProperties.getInstance();
		return properties.getStartNode();
	}

	@Override
	protected boolean isImproverPath(final int startNode, final int targetNode) {
		return false;
	}
}
