package org.haidash.visual.aco.reader;

import com.carrotsearch.hppc.IntArrayList;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.AcoRuntimeException;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.util.ACOParameters;
import org.haidash.visual.aco.ui.Constants;
import org.haidash.visual.aco.ui.model.VisualLink;
import org.haidash.visual.aco.ui.model.VisualNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.haidash.visual.aco.ui.Constants.CIRCLE_RADIUS;


public class VisualGraphReader {

    private final static Logger LOGGER = Logger.getLogger(VisualGraphReader.class);

    private final ACOParameters ACO_PARAMETERS = ACOParameters.INSTANCE;

    private final Graph<VisualNode, VisualLink> graph;

    public VisualGraphReader(Graph<VisualNode, VisualLink> graph) {

        this.graph = graph;

        if (this.graph == null) {
            throw new AcoRuntimeException("Graph can not be empty!");
        }

    }

    public void read(final File file) {

        if ((file == null) || !file.exists()) {
            throw new AcoRuntimeException("Input file not fount!");
        }

        try (Scanner text = new Scanner(new FileReader(file))) {

            final int graphSize = text.nextInt();
            final List<VisualNode> nodes = new ArrayList<>(graphSize);

            graph.setGraphSize(graphSize);
            graph.setNodes(nodes);

            final IntArrayList fuelLevels = new IntArrayList(graphSize);

            graph.setFuelLevels(fuelLevels);

            for (int i = 0; i < graphSize; i++) {

                final int fuel = text.nextInt();

                fuelLevels.add(fuel);
                nodes.add(new VisualNode(i, fuel));
            }

            final int linksSize = text.nextInt();
            final List<VisualLink> links = new ArrayList<>(linksSize);

            graph.setLinks(links);

            for (int i = 0; i < linksSize; i++) {

                final int start = text.nextInt();
                final int finish = text.nextInt();

                final VisualNode startNode = nodes.get(start);
                final VisualNode finishNode = nodes.get(finish);

                final VisualLink link = new VisualLink(text.nextInt());
                link.setFirst(startNode);
                link.setSecond(finishNode);
                links.add(link);
                link.getLine().startXProperty().bind(startNode.getPane().layoutXProperty().add(CIRCLE_RADIUS));
                link.getLine().startYProperty().bind(startNode.getPane().layoutYProperty().add(CIRCLE_RADIUS));

                link.getLine().endXProperty().bind(finishNode.getPane().layoutXProperty().add(CIRCLE_RADIUS));
                link.getLine().endYProperty().bind(finishNode.getPane().layoutYProperty().add(CIRCLE_RADIUS));

                startNode.getOutgoingLinks().add(link);
                finishNode.getIngoingLink().add(link);
            }

            ACO_PARAMETERS.setMaxFuelLevels(text.nextInt());

            graph.setStartNode(nodes.get(text.nextInt()));
            graph.setTargetNode(nodes.get(text.nextInt()));

        } catch (final FileNotFoundException e) {
            throw new AcoRuntimeException("Input file is incorrect!", e);
        }
    }
}
