package org.haidash.visual.aco.algorithm;

import javafx.scene.control.Alert;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.algorithm.colony.AntColony;
import org.haidash.visual.aco.algorithm.colony.impl.AntColonyImpl;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.process.GraphWriter;
import org.haidash.visual.aco.algorithm.util.ACOParameters;
import org.haidash.visual.aco.algorithm.util.SearchHistory;
import org.haidash.visual.aco.algorithm.util.SearchResult;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by zonclayv on 23.04.16.
 */
public class AntColonyRunner {

    private final static Logger LOGGER = Logger.getLogger(AntColonyRunner.class);

    private final ExecutorService pool = Executors.newSingleThreadExecutor(r -> {
        final Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    });

    private final Graph graph;

    public AntColonyRunner(Graph graph) {
        this.graph = graph;
    }

    public void run() {

        graph.clear();

        final Callable<SearchHistory> search = () -> {
            AntColony antColony = new AntColonyImpl(graph);

            final SearchHistory searchResult = antColony.run();

            if (searchResult == null) {
                return null;
            }

            GraphWriter writer = new GraphWriter(graph);
            writer.writeHistory(searchResult);

            return searchResult;
        };

        final Future<SearchHistory> future = pool.submit(search);

        try {
            final SearchHistory result = future.get(1, TimeUnit.MILLISECONDS);
//            consumer.accept(result);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            LOGGER.error(e);
        }
    }
}
