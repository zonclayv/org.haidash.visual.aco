package org.haidash.visual.aco.oop.entity;

import java.util.ArrayList;
import java.util.List;

public class FloydWarshall {

	private static void calculate(final Graph graph, final int maxFuel) {

		final int inf = Integer.MAX_VALUE;

		final int graphSize = graph.getGraphSize();

		for (int i = 0; i < graphSize; i++) {
			List<Integer> line = new ArrayList<>();
			for (int j = 0; j < graphSize; j++) {
				if (i != j) {
					line.add(inf);
				} else {
					line.add(0);
				}
			}

			costsToMove.add(new ArrayList<>(line));
			fuel.add(new ArrayList<>(line));
			remainsFuel.add(inf);
		}

		for (int i = 0; i < graphSize; i++) {

			List<Integer> prev = new ArrayList<>();

			for (int j = 0; j < graphSize; j++) {
				prev.add(i);
			}

			prevNode.add(new ArrayList<>(prev));
		}

		final List<Link> links = graph.getLinks();

		for (Link link : links) {

			final int firstNodeNum = link.getFirst().getNumber();
			final int secondNodeNum = link.getSecond().getNumber();

			final int weight = link.getWeight();

			if (weight <= maxFuel) {
				costsToMove.get(firstNodeNum).set(secondNodeNum, weight);
			}

			Integer fuelBalanse = link.getFirst().getFuelBalance();

			fuel.get(firstNodeNum).set(secondNodeNum, fuelBalanse > maxFuel ? maxFuel : fuelBalanse);
		}

		for (int k = 0; k < graphSize; ++k) {
			for (int i = 0; i < graphSize; ++i) {
				for (int j = 0; j < graphSize; ++j) {

					if ((costsToMove.get(i).get(k) < inf) && (costsToMove.get(k).get(j) < inf)) {

						if (costsToMove.get(i).get(j) > (costsToMove.get(i).get(k) + costsToMove.get(k).get(j))) {
							prevNode.get(i).set(j, k);
						}

						costsToMove.get(i).set(j,
								Math.min(costsToMove.get(i).get(j), costsToMove.get(i).get(k) + costsToMove.get(k).get(j)));

					}

					if ((fuel.get(i).get(k) < inf) && (fuel.get(k).get(j) < inf)) {

						fuel.get(i).set(j, Math.min(fuel.get(i).get(j), fuel.get(i).get(k) + fuel.get(k).get(j)));

					}
				}
			}
		}

		int finish = graph.getTargetNode().getNumber();
		for (int i = 0; i < graphSize; ++i) {
			int start = i;
			int from;
			do {
				from = findPrevVertex(finish, start);
				if ((costsToMove.get(start).get(from) == inf) || (fuel.get(start).get(from) == inf)) {
					remainsFuel.set(i, Math.min(remainsFuel.get(i), inf));
				} else {
					remainsFuel.set(i, Math.min(remainsFuel.get(i), fuel.get(i).get(from) - costsToMove.get(i).get(from)));
				}

				start = from;
			} while (finish != from);
		}
	}

	private static int findPrevVertex(final int start, final int finish) {
		return prevNode.get(start).get(finish);
	}

	public static List<Integer> getRemainsFuel(final Graph graph, final int maxFuel) {

		costsToMove.clear();
		costsToMove.clear();
		prevNode.clear();
		remainsFuel.clear();
		fuel.clear();

		calculate(graph, maxFuel);

		return remainsFuel;
	}

	private static List<List<Integer>> costsToMove = new ArrayList<>();
	private static List<List<Integer>> fuel = new ArrayList<>();
	private static List<List<Integer>> prevNode = new ArrayList<>();
	private static List<Integer> remainsFuel = new ArrayList<>();
}
