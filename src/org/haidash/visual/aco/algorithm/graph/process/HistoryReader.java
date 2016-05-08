package org.haidash.visual.aco.algorithm.graph.process;

import com.carrotsearch.hppc.IntArrayList;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.AcoRuntimeException;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.graph.process.task.LoadHistoryTask;
import org.haidash.visual.aco.algorithm.util.ACOParameters;
import org.haidash.visual.aco.algorithm.util.SearchHistory;
import org.haidash.visual.aco.algorithm.util.Solution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;


public class HistoryReader {

    private final static Logger LOGGER = Logger.getLogger(HistoryReader.class);

    private LoadHistoryTask loadHistoryTask;

    public HistoryReader(LoadHistoryTask loadHistoryTask) {
        this.loadHistoryTask = loadHistoryTask;
    }

    public HistoryReader() {
    }

    public Map<String, List<SearchHistory>> read(final Path path) {

        final Map<String, List<SearchHistory>> histories = new HashMap<>();

        if (Files.isDirectory(path)) {
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        readFile(file, histories);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                LOGGER.warn("Can't rad files in directory '" + path.getFileName().toString() + "' not fount!");
            }
        } else {
            readFile(path, histories);
        }


        return histories;
    }

    private void readFile(Path path, Map<String, List<SearchHistory>> histories) {

        if (!Files.exists(path)) {
            LOGGER.warn("Input file '" + path.getFileName().toString() + "' not fount!");
            return;
        }

        List<String> allLines = null;
        try {
            allLines = Files.readAllLines(path);

        } catch (IOException e) {
            LOGGER.warn("Can't read file '" + path.getFileName().toString() + "' not fount!");
            return;
        }

        final String fileName = path.getFileName().toString();
        histories.put(fileName, new ArrayList<>());

        final int size = allLines.size();

        if (size < 11) {
            return;
        }

        final int max = size / 11;

        for (int i = 0; i < size; i++) {

            final int toIndex = i + 10;

            try {

                final SearchHistory searchHistory = readHistory(allLines.subList(i, toIndex));

                if (searchHistory != null) {
                    histories.get(fileName).add(searchHistory);
                }

            } catch (Exception e) {
                continue;
            }

            if (toIndex + 1 > size) {
                break;
            }
        }
    }

    private SearchHistory readHistory(List<String> strings) {

        if (strings.size() < 10) {
            return null;
        }

        final SearchHistory searchHistory = new SearchHistory();
        searchHistory.setGraphName(strings.get(0));

        final String graphInfo = strings.get(1);
        final String[] graphInfoArray = graphInfo.split(" ");

        searchHistory.setGraphSize(Integer.valueOf(graphInfoArray[0]));
        searchHistory.setStartNode(Integer.valueOf(graphInfoArray[1]));
        searchHistory.setTargetNode(Integer.valueOf(graphInfoArray[2]));
        searchHistory.setAntCount(Integer.valueOf(graphInfoArray[3]));
        searchHistory.setGenerationCount(Integer.valueOf(graphInfoArray[4]));
        searchHistory.setQ(Integer.valueOf(graphInfoArray[5]));
        searchHistory.setAlpha(Integer.valueOf(graphInfoArray[6]));
        searchHistory.setBeta(Integer.valueOf(graphInfoArray[7]));

        final String firstSolutionString = strings.get(3);
        final String[] firstSplit = firstSolutionString.split(" ");

        final Solution firstSolution = new Solution();
        firstSolution.setGeneration(Integer.valueOf(firstSplit[0]));
        firstSolution.setTime(Long.valueOf(firstSplit[1]));
        firstSolution.setTotalCost(Integer.valueOf(firstSplit[2]));

        searchHistory.setFirstSolution(firstSolution);

        final String bestSolutionString = strings.get(7);
        final String[] bestSplit = bestSolutionString.split(" ");

        final Solution bestSolution = new Solution();
        bestSolution.setGeneration(Integer.valueOf(bestSplit[0]));
        bestSolution.setTime(Long.valueOf(bestSplit[1]));
        bestSolution.setTotalCost(Integer.valueOf(bestSplit[2]));

        searchHistory.setBestSolution(bestSolution);

        return searchHistory;
    }
}
