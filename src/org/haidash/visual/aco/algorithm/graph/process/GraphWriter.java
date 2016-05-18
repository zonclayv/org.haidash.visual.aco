package org.haidash.visual.aco.algorithm.graph.process;

import com.carrotsearch.hppc.IntArrayList;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.AcoRuntimeException;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.util.*;
import org.haidash.visual.aco.ui.graph.NodeView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by zonclayv on 06.03.16.
 */
public class GraphWriter {

    private final static Logger LOGGER = Logger.getLogger(GraphWriter.class);

    private Graph graph;

    public GraphWriter(Graph graph) {
        this.graph = graph;
    }

    public void writeHistory(SearchHistory history) {

        final Path folder = graph.getLocation().resolve("history");
        final Path output = folder.resolve(graph.getGraphName() + ".history");

        if (!Files.exists(output)) {
            try {
                Files.createDirectories(folder);
                Files.createFile(output);
            } catch (IOException e) {
                LOGGER.error("Can not create output file.");
                return;
            }
        }

        final LocalDateTime currentTime = LocalDateTime.now();
        final ACOParameters acoParameters = ACOParameters.INSTANCE;

        final StringBuilder buffer = new StringBuilder();
        buffer.append(history.getGraphName()).append("\n");
        buffer.append(history.getGraphSize()).append(" ");
        buffer.append(history.getStartNode()).append(" ");
        buffer.append(history.getTargetNode()).append(" ");
        buffer.append(history.getAntCount()).append(" ");
        buffer.append(history.getGenerationCount()).append(" ");
        buffer.append(history.getQ()).append(" ");
        buffer.append(history.getAlpha()).append(" ");
        buffer.append(history.getBeta()).append(" ");
        buffer.append("\n");

        final Solution firstSolution = history.getFirstSolution();
        buffer.append("First Solution:").append("\n");
        buffer.append(firstSolution.getGeneration()).append(" ");
        buffer.append(firstSolution.getTime()).append(" ");
        buffer.append(firstSolution.getTotalCost()).append("\n");

        final List<Link> firstSolutionPath = firstSolution.getPath();

        for (Link link : firstSolutionPath) {
            buffer.append(link.getFirst().getNumber()).append(" ");
        }

        buffer.append("\n");

        final IntArrayList firstSolutionLevel = firstSolution.getSpentFuelLevel();

        for (int i = 0; i < firstSolutionLevel.size(); i++) {
            buffer.append(firstSolutionLevel.get(i)).append(" ");
        }

        buffer.append("\n");

        final Solution bestSolution = history.getBestSolution();
        buffer.append("Best Solution:").append("\n");
        buffer.append(bestSolution.getGeneration()).append(" ");
        buffer.append(bestSolution.getTime()).append(" ");
        buffer.append(bestSolution.getTotalCost()).append("\n");

        final List<Link> bestSolutionPath = bestSolution.getPath();

        for (Link link : bestSolutionPath) {
            buffer.append(link.getFirst().getNumber()).append(" ");
        }

        buffer.append("\n");

        final IntArrayList bestSolutionLevel = bestSolution.getSpentFuelLevel();

        for (int i = 0; i < bestSolutionLevel.size(); i++) {
            buffer.append(bestSolutionLevel.get(i)).append(" ");
        }

        buffer.append("\n");

        final Solution classicalSolution = history.getClassicalSolution();


        buffer.append("Classical Solution:").append("\n");

        if (classicalSolution == null) {

            buffer.append("-").append("\n");
            buffer.append("-").append("\n");
            buffer.append("-");
        } else {
            buffer.append(classicalSolution.getGeneration()).append(" ");
            buffer.append(classicalSolution.getTime()).append(" ");
            buffer.append(classicalSolution.getTotalCost()).append("\n");

            final List<Link> classicalPath = bestSolution.getPath();

            for (Link link : classicalPath) {
                buffer.append(link.getFirst().getNumber()).append(" ");
            }

            buffer.append("\n");

            final IntArrayList classicalLevel = bestSolution.getSpentFuelLevel();

            for (int i = 0; i < classicalLevel.size(); i++) {
                buffer.append(bestSolutionLevel.get(i)).append(" ");
            }
        }

        buffer.append("\n").append("--").append("\n");

        try {
            Files.write(output, buffer.toString().getBytes("utf-8"), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("Can not add new line in output file.");
        }

    }

    public void writeGraph(Path path) {

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                LOGGER.error("Can not create output file.");
                return;
            }
        }

        final List<String> lines = new ArrayList<>();

        lines.add(graph.getGraphSize() + "");

        for (Node node : graph.getNodes()) {
            final NodeView view = node.getView();
            final Pair<Integer, Integer> position = view.getPosition();
            lines.add(node.getFuelBalance() + " " + position.first + " " + position.second);
        }

        lines.add("");

        lines.add(graph.getLinks().size() + "");

        for (Link link : graph.getLinks()) {
            final Node first = link.getFirst();
            final Node second = link.getSecond();
            final int weight = link.getWeight();

            lines.add(first + " " + second + " " + weight);
        }

        lines.add("");

        final ACOParameters acoParameters = ACOParameters.INSTANCE;

        lines.add(acoParameters.getMaxFuelLevels() + "");

        lines.add("");
        lines.add(graph.getStartNode() + " " + graph.getTargetNode());

        try {
            Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
