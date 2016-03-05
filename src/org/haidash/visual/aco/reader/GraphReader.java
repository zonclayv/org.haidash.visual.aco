package org.haidash.visual.aco.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.AcoRuntimeException;
import org.haidash.visual.aco.algorithm.aco.entity.*;
import org.haidash.visual.aco.ui.pane.GraphPane;


public class GraphReader {

    final ACOParameters ACO_PARAMETERS = ACOParameters.INSTANCE;

    final Graph graph;

    public GraphReader(final Graph graph) {

        if (graph == null) {
            throw new AcoRuntimeException("Graph can not be empty!");
        }

        this.graph = graph;
    }

    public void read(final File file) {

        if ((file == null) || !file.exists()) {
            throw new AcoRuntimeException("Input file not fount!");
        }

        try (Scanner text = new Scanner(new FileReader(file))) {

            final int graphSize = text.nextInt();
            final List<Node> nodes = new ArrayList<>(graphSize);

            graph.setGraphSize(graphSize);
            graph.setNodes(nodes);

            final IntArrayList fuelLevels = new IntArrayList(graphSize);

            graph.setFuelLevels(fuelLevels);

            for (int i = 0; i < graphSize; i++) {

                final int fuel = text.nextInt();

                fuelLevels.add(fuel);
                nodes.add(new Node(i, fuel));
            }

            final int linksSize = text.nextInt();
            final List<Link> links = new ArrayList<>(linksSize);

            graph.setLinks(links);

            for (int i = 0; i < linksSize; i++) {

                final int start = text.nextInt();
                final int finish = text.nextInt();

                final Node startNode = nodes.get(start);
                final Node finishNode = nodes.get(finish);

                final Link link = new Link(text.nextInt());
                link.setFirst(startNode);
                link.setSecond(finishNode);
                links.add(link);

                startNode.getOutgoingLinks().add(link);
            }

            ACO_PARAMETERS.setMaxFuelLevels(text.nextInt());

            graph.setStartNode(nodes.get(text.nextInt()));
            graph.setTargetNode(nodes.get(text.nextInt()));

        } catch (final FileNotFoundException e) {
            throw new AcoRuntimeException("Input file is incorrect!", e);
        }
    }
}
