package org.haidash.visual.aco.algorithm.aco.entity;

import com.carrotsearch.hppc.IntArrayList;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.MAX_VALUE;

public class FloydWarshall {

    private static void calculate(final Graph graph, final int maxFuel) {

        final int graphSize = graph.getGraphSize();

        for (int i = 0; i < graphSize; i++) {

            final IntArrayList line = new IntArrayList(graphSize);

            for (int j = 0; j < graphSize; j++) {
                if (i != j) {
                    line.add(MAX_VALUE);
                } else {
                    line.add(0);
                }
            }

            costsToMove.add(line);
            fuel.add(line);
            remainsFuel.add(MAX_VALUE);
        }

        for (int i = 0; i < graphSize; i++) {

            final IntArrayList prev = new IntArrayList(graphSize);

            for (int j = 0; j < graphSize; j++) {
                prev.add(i);
            }

            prevNode.add(prev);
        }

        final List<Link> links = graph.getLinks();

        for (Link link : links) {

            final int firstNodeNum = link.getFirst().getNumber();
            final int secondNodeNum = link.getSecond().getNumber();

            final int weight = link.getWeight();

            if (weight <= maxFuel) {
                costsToMove.get(firstNodeNum).set(secondNodeNum, weight);
            }

            final int fuelBalance = link.getFirst().getFuelBalance();
            final int newFuelBalance = fuelBalance > maxFuel ? maxFuel : fuelBalance;

            fuel.get(firstNodeNum).set(secondNodeNum, newFuelBalance);
        }

        for (int k = 0; k < graphSize; ++k) {
            for (int i = 0; i < graphSize; ++i) {
                for (int j = 0; j < graphSize; ++j) {

                    if ((costsToMove.get(i).get(k) < MAX_VALUE) && (costsToMove.get(k).get(j) < MAX_VALUE)) {

                        if (costsToMove.get(i).get(j) > (costsToMove.get(i).get(k) + costsToMove.get(k).get(j))) {
                            prevNode.get(i).set(j, k);
                        }

                        final int minCost = Math.min(costsToMove.get(i).get(j), costsToMove.get(i).get(k) + costsToMove.get(k).get(j));

                        costsToMove.get(i).set(j, minCost);

                    }

                    if ((fuel.get(i).get(k) < MAX_VALUE) && (fuel.get(k).get(j) < MAX_VALUE)) {

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

                if ((costsToMove.get(start).get(from) == MAX_VALUE) || (fuel.get(start).get(from) == MAX_VALUE)) {
                    final int minValue = Math.min(remainsFuel.get(i), MAX_VALUE);
                    remainsFuel.set(i, minValue);
                } else {
                    final int minValue = Math.min(remainsFuel.get(i), fuel.get(i).get(from) - costsToMove.get(i).get(from));
                    remainsFuel.set(i, minValue);
                }

                start = from;

            } while (finish != from);
        }
    }

    private static int findPrevVertex(final int start, final int finish) {
        return prevNode.get(start).get(finish);
    }

    public static IntArrayList getRemainsFuel(final Graph graph, final int maxFuel) {

        clear();
        calculate(graph, maxFuel);

        return remainsFuel;
    }

    private static void clear() {
        costsToMove.clear();
        costsToMove.clear();
        prevNode.clear();
        remainsFuel.clear();
        fuel.clear();
    }

    private static List<IntArrayList> costsToMove = new ArrayList<>();
    private static List<IntArrayList> fuel = new ArrayList<>();
    private static List<IntArrayList> prevNode = new ArrayList<>();
    private static IntArrayList remainsFuel = new IntArrayList();
}
