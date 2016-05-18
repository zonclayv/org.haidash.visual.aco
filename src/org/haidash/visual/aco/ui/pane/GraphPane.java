package org.haidash.visual.aco.ui.pane;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;

import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Path;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.graph.process.GraphReader;
import org.haidash.visual.aco.ui.GraphChangeListener;
import org.haidash.visual.aco.ui.graph.GraphInitializer;
import org.haidash.visual.aco.ui.graph.LinkView;
import org.haidash.visual.aco.ui.graph.NodeView;

import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseButton.SECONDARY;
import static org.haidash.visual.aco.ui.Constants.CIRCLE_RADIUS;
import static org.haidash.visual.aco.ui.Constants.GRID_ITEM_HEIGHT;
import static org.haidash.visual.aco.ui.Constants.GRID_ITEM_WIDTH;

/**
 * Created by zonclayv on 04.03.16.
 */
public class GraphPane extends ScrollPane implements GraphChangeListener {

    private final static Logger LOGGER = Logger.getLogger(GraphPane.class);

    private final double SCALE_DELTA = 1.1;

    private final Path verticalGridLines = new Path();
    private final Path horizontalGridLines = new Path();

    private final Pane canvas;
    private final Graph graph;

    private boolean disableGraph;

    private int maxX;
    private int maxY;

    public GraphPane(final Graph graph) {
        this.graph = graph;
        this.graph.addListener(this);

        canvas = new Pane();

        initCanvas();

        getStyleClass().setAll("chart-plot-background");
        setContent(canvas);

        widthProperty().addListener((final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) -> {
            setSize(newValue.doubleValue(), null);
            fillBackground();
        });

        heightProperty().addListener((final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) -> {
            setSize(null, newValue.doubleValue());
            fillBackground();
        });

        viewportBoundsProperty().addListener(
                (observableValue, oldBounds, newBounds) -> {
                    setSize(
                            Math.min(canvas.getBoundsInParent().getMaxX(), newBounds.getWidth()),
                            Math.min(canvas.getBoundsInParent().getMaxY(), newBounds.getHeight())
                    );
                });
    }

    private void initCanvas() {
        canvas.getChildren().addAll(verticalGridLines, horizontalGridLines);
        canvas.setOnScroll(event -> {
            event.consume();

            if (event.getDeltaY() == 0) {
                return;
            }

            double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

            final double scaleY = canvas.getScaleY() * scaleFactor;
            final double scaleX = canvas.getScaleX() * scaleFactor;

            canvas.getTransforms().add(new Scale(scaleX, scaleY, 0, 0));

            Platform.runLater(() -> canvas.setPrefSize(
                    Math.max(canvas.getBoundsInParent().getMaxX(), getViewportBounds().getWidth()),
                    Math.max(canvas.getBoundsInParent().getMaxY(), getViewportBounds().getHeight())
            ));
        });
    }

    private void setSize(Double width, Double height) {
        if (width != null) {
            canvas.setPrefWidth(maxX >= width ? maxX : width);
        }
        if (height != null) {
            canvas.setPrefHeight(maxY >= height ? maxY : height);
        }
    }

    public void drawGraph() {

        final ObservableList<javafx.scene.Node> children = canvas.getChildren();
        if (children.size() > 2) {
            children.remove(2, children.size());
        }

        setSize(getWidth(), getHeight());

        fillBackground();

        final List<Link> links = graph.getLinks();
        for (Link link : links) {
            final LinkView view = link.getView();
            children.add(view.getLine());
        }

        final List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            fillCircle(graph, node);

            final NodeView view = node.getView();
            final Pane nodeCircle = view.getPane();

            nodeCircle.setOnMouseClicked((final MouseEvent event) -> {
                if (disableGraph || view.isDrag()) {
                    return;
                }
                changeVertexStatus(graph, event.getButton(), node);
            });

            children.add(nodeCircle);
        }
    }

    private void fillBackground() {

        verticalGridLines.getElements().clear();
        horizontalGridLines.getElements().clear();

        for (int i = 0; i < canvas.getPrefHeight() - 10; i += GRID_ITEM_HEIGHT) {
            verticalGridLines.getElements().add(new MoveTo(0, i));
            verticalGridLines.getElements().add(new LineTo(canvas.getPrefWidth() - 10, i));
        }

        for (int i = 0; i < canvas.getPrefWidth() - 10; i += GRID_ITEM_WIDTH) {
            horizontalGridLines.getElements().add(new MoveTo(i, 0));
            horizontalGridLines.getElements().add(new LineTo(i, canvas.getPrefHeight() - 10));
        }

        verticalGridLines.getStyleClass().setAll("chart-vertical-grid-lines");
        horizontalGridLines.getStyleClass().setAll("chart-horizontal-grid-lines");
    }

    public void markPath(final List<Link> path) {

        if (path.size() < 1) {
            return;
        }

        final Circle circle = new Circle();
//        circle.setRadius(CIRCLE_RADIUS);
//
//        circle.setStroke(GREY);
//        circle.setFill(YELLOW);
//        circle.setTranslateX(CIRCLE_RADIUS);
//        circle.setTranslateY(CIRCLE_RADIUS);
//
//        canvas.getChildren().addAll(circle);
//
        SequentialTransition animation = new SequentialTransition();
        animation.setNode(circle);

        for (Link link : path) {
            final NodeView fromView = link.getFirst().getView();
            final NodeView toView = link.getSecond().getView();

            final FillTransition fromTransition = new FillTransition(Duration.millis(100), fromView.getCircle());
            fromTransition.setToValue(Color.YELLOW);
            fromTransition.setCycleCount(1);

            final Pane fromPane = fromView.getPane();
            final Pane toPane = toView.getPane();

            final TranslateTransition transition = new TranslateTransition();
            transition.fromXProperty().bind(fromPane.layoutXProperty().add(10));
            transition.fromYProperty().bind(fromPane.layoutYProperty().add(10));
            transition.toXProperty().bind(toPane.layoutXProperty().add(10));
            transition.toYProperty().bind(toPane.layoutYProperty().add(10));
            transition.setDelay(Duration.millis(100));

            final Line line = link.getView().getLine();

            final DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.LIGHTYELLOW);
            dropShadow.setRadius(5);

            final StrokeTransition stLine = new StrokeTransition(Duration.millis(50), line, Color.GRAY, Color.YELLOW);
            final Shape shape = stLine.shapeProperty().getValue();
            shape.setEffect(dropShadow);
            shape.setStrokeWidth(2);

            stLine.setCycleCount(1);
            stLine.setAutoReverse(true);

            final FillTransition toTransition = new FillTransition(Duration.millis(100), toView.getCircle());
            toTransition.setToValue(Color.YELLOW);
            toTransition.setCycleCount(1);

            animation.getChildren().addAll(fromTransition, transition, stLine, toTransition);
        }

        final TranslateTransition transition = new TranslateTransition();
        transition.setDelay(Duration.millis(200));

        animation.getChildren().add(transition);

        animation.setCycleCount(1);
        animation.play();

        animation.setOnFinished((ActionEvent) -> {
            canvas.getChildren().remove(circle);

            for (Link link : path) {

                final LinkView view = link.getView();
                final Line line = view.getLine();
                line.setStroke(Color.GRAY);
                line.setEffect(null);
                line.setStrokeWidth(1);

                final Node first = link.getFirst();
                final Node second = link.getSecond();

                fillCircle(graph, first);
                fillCircle(graph, second);
            }
        });
    }

    @Override
    public void graphChanged() {
        drawGraph();
    }

    private void setMaxSize(int maxX, int maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public void openFile(Consumer<String> consumer) {

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select input file");

        final File file = fileChooser.showOpenDialog(getScene().getWindow());

        if ((file == null) || !file.getName().endsWith(".txt")) {
            return;
        }

        final String absolutePath = file.getAbsolutePath();
        final java.nio.file.Path path = Paths.get(absolutePath);
        final String name = path.getFileName().toString();
        final java.nio.file.Path folder = path.getParent();
        final String testName = FilenameUtils.removeExtension(name);

        graph.setLocation(folder);
        graph.setGraphName(testName);

        consumer.accept(absolutePath);

        final GraphReader fileReader = new GraphReader(graph);

        try {
            fileReader.read(file);
            LOGGER.info("Graph '" + testName + "' initialized...");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return;
        }

        final GraphInitializer initializer = new GraphInitializer(graph);
        initializer.init();

        setMaxSize(initializer.getMaxX(), initializer.getMaxY());

        LOGGER.info("Visual graph initialized...");

        graph.fireListener();
    }

    public void fillCircle(final Graph graph, final Node node) {

        final Node startNode = graph.getStartNode();
        final Node targetNode = graph.getTargetNode();

        final Circle circle = node.getView().getCircle();

        if (Objects.equals(startNode, node)) {
            circle.setStroke(Color.GREY);
            circle.setFill(Color.GREEN);
        } else if (Objects.equals(targetNode, node)) {
            circle.setStroke(Color.GREY);
            circle.setFill(Color.RED);
        } else {
            circle.setStroke(Color.GREY);
            circle.setFill(Color.WHITESMOKE);
        }
    }

    public void changeVertexStatus(final Graph graph, final MouseButton btn, final Node node) {

        final Node startNode = graph.getStartNode();
        final Node targetNode = graph.getTargetNode();

        if (btn == PRIMARY) {

            if (Objects.equals(node, targetNode)) {
                graph.setTargetNode(startNode);
            }

            graph.setStartNode(node);
        } else if (btn == SECONDARY) {

            if (Objects.equals(node, startNode)) {
                graph.setStartNode(targetNode);
            }

            graph.setTargetNode(node);
        } else {
            return;
        }

        fillCircle(graph, startNode);
        fillCircle(graph, targetNode);
        fillCircle(graph, graph.getStartNode());
        fillCircle(graph, graph.getTargetNode());
    }

    public void setDisableGraph(boolean disableGraph) {
        this.disableGraph = disableGraph;
    }
}
