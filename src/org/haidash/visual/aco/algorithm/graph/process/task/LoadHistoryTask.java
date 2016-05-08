package org.haidash.visual.aco.algorithm.graph.process.task;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.algorithm.graph.process.HistoryReader;
import org.haidash.visual.aco.algorithm.util.SearchHistory;
import org.haidash.visual.aco.algorithm.util.Solution;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by zonclayv on 07.05.16.
 */
public abstract class LoadHistoryTask extends Task<ObservableList<SearchHistory>> {

    private final static Logger LOGGER = Logger.getLogger(LoadHistoryTask.class);

    private final List<SearchHistory> history;
    private final Consumer<List<SearchHistory>> fillConsumer;

    public LoadHistoryTask( Consumer<List<SearchHistory>> fillConsumer) {
        this.history = new ArrayList<>();
        this.fillConsumer=fillConsumer;
    }

    @Override
    protected ObservableList<SearchHistory> call() throws Exception {

        final ObservableList<SearchHistory> data = FXCollections.observableArrayList();
        final Path path = getPath();

        try {
            read(path);

            history.forEach(data::addAll);

            LOGGER.info("Search history loaded...");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        Platform.runLater(() -> fillConsumer.accept(history));

        return data;
    }

    public abstract Path getPath();

    private void read(Path path) {

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

        final int size = allLines.size();

        if (size < 11) {
            return;
        }

        final long max = (size / 11);

        for (int i = 0; i < size; i++) {

            final int toIndex = i + 10;

            try {

                final SearchHistory searchHistory = readHistory(allLines.subList(i, toIndex));

                if (searchHistory != null) {
                    history.add(searchHistory);
                }

                updateProgress((i / 11), max);
                Thread.sleep(5);

            } catch (Exception e) {
                i = toIndex;
                continue;
            }

            if (toIndex + 1 > size) {
                break;
            }

            i = toIndex;
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
