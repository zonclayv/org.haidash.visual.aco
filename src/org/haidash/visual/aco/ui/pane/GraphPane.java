package org.haidash.visual.aco.ui.pane;

import java.util.*;

import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.util.Pair;
import org.haidash.visual.aco.ui.Constants;
import org.haidash.visual.aco.ui.GraphChangeListener;
import org.haidash.visual.aco.ui.model.VisualGraph;
import org.haidash.visual.aco.ui.model.VisualLink;
import org.haidash.visual.aco.ui.model.VisualNode;

import static javafx.scene.paint.Color.GREY;
import static javafx.scene.paint.Color.YELLOW;
import static org.haidash.visual.aco.ui.Constants.CIRCLE_RADIUS;
import static org.haidash.visual.aco.ui.Constants.GRID_ITEM_HEIGHT;
import static org.haidash.visual.aco.ui.Constants.GRID_ITEM_WIDTH;

/**
 * Created by zonclayv on 04.03.16.
 */
public class GraphPane extends ScrollPane implements GraphChangeListener {


    private final double SCALE_DELTA = 1.1;

    private final Path verticalGridLines = new Path();
    private final Path horizontalGridLines = new Path();

    private final Pane pane;

    private VisualGraph graph;

    public GraphPane(final VisualGraph graph) {
        this.graph = graph;
        this.graph.addListener(this);

        pane = new Pane();
        pane.getStyleClass().setAll("chart-plot-background");

        pane.minHeightProperty().bind(heightProperty().subtract(5));
        pane.minWidthProperty().bind(widthProperty().subtract(5));

        widthProperty().addListener((final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) -> {
            setNewWidth(newValue);

            fillBackground();
        });

        heightProperty().addListener((final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) -> {
            setNewHeight(newValue);

            fillBackground();
        });

        pane.getChildren().addAll(verticalGridLines, horizontalGridLines);
        pane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                event.consume();

                if (event.getDeltaY() == 0) {
                    return;
                }

                double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

                pane.setScaleX(pane.getScaleX() * scaleFactor);
                pane.setScaleY(pane.getScaleY() * scaleFactor);
            }
        });

        setContent(pane);
    }

    private void setNewHeight(Number newValue) {
        final int maxY = graph.getMaxY();
        final int value = newValue.intValue() - 10;

        pane.setPrefHeight(maxY >= value ? maxY + GRID_ITEM_HEIGHT : value);
    }

    private void setNewWidth(Number newValue) {
        final int maxX = graph.getMaxX();
        final int value = newValue.intValue() - 10;

        pane.setPrefWidth(maxX >= value ? maxX + GRID_ITEM_WIDTH : value);
    }

    public void drawGraph() {

        final ObservableList<javafx.scene.Node> children = pane.getChildren();
        if (children.size() > 2) {
            children.remove(2, children.size());
        }

        setNewWidth(getWidth());
        setNewHeight(getHeight());

        fillBackground();

        for (VisualLink link : graph.getLinks()) {
            // link.initLocation();
            children.add(link.getLine());
        }

        for (VisualNode node : graph.getNodes()) {
            node.initLocation();
            graph.fillCircle(node);

            final Pane nodeCircle = node.getPane();
            nodeCircle.setOnMouseClicked((final MouseEvent event) -> {
                if (node.isDrag()) {
                    return;
                }
                graph.changeVertexStatus(event.getButton(), node);
            });
            children.add(nodeCircle);
        }

        pane.layout();
    }

    private void fillBackground() {

        verticalGridLines.getElements().clear();
        horizontalGridLines.getElements().clear();

        for (int i = 0; i < pane.getPrefHeight(); i += GRID_ITEM_HEIGHT) {
            verticalGridLines.getElements().add(new MoveTo(0, i));
            verticalGridLines.getElements().add(new LineTo(pane.getPrefWidth(), i));
        }

        for (int i = 0; i < pane.getPrefWidth(); i += GRID_ITEM_WIDTH) {
            horizontalGridLines.getElements().add(new MoveTo(i, 0));
            horizontalGridLines.getElements().add(new LineTo(i, pane.getPrefHeight()));
        }

        verticalGridLines.getStyleClass().setAll("chart-vertical-grid-lines");
        horizontalGridLines.getStyleClass().setAll("chart-horizontal-grid-lines");
    }

    public void markPath(final List<Link> path) {

        if (path.size() < 1) {
            return;
        }

        final Circle circle = new Circle();
        circle.setRadius(CIRCLE_RADIUS);

        circle.setStroke(GREY);
        circle.setFill(YELLOW);
        circle.setTranslateX(CIRCLE_RADIUS);
        circle.setTranslateY(CIRCLE_RADIUS);

        pane.getChildren().addAll(circle);

        SequentialTransition animation = new SequentialTransition();
        animation.setNode(circle);

        for (Link link : path) {
            final VisualNode fromNode = (VisualNode) link.getFirst();
            final VisualNode toNode = (VisualNode) link.getSecond();

            final FillTransition fromTransition = new FillTransition(Duration.millis(100), fromNode.getCircle());
            fromTransition.setToValue(Color.YELLOW);
            fromTransition.setCycleCount(1);

            final TranslateTransition transition = new TranslateTransition();

            final Pair<Integer, Integer> fromLocation = fromNode.getLocation();
            transition.setFromX(fromLocation.first);
            transition.setFromY(fromLocation.second);

            final Pair<Integer, Integer> toLocation = toNode.getLocation();
            transition.setToX(toLocation.first);
            transition.setToY(toLocation.second);
            transition.setDelay(Duration.millis(100));

            final Line line = ((VisualLink) link).getLine();

            final DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.LIGHTYELLOW);
            dropShadow.setRadius(5);

            final StrokeTransition stLine = new StrokeTransition(Duration.millis(50), line, Color.GRAY, Color.YELLOW);
            final Shape shape = stLine.shapeProperty().getValue();
            shape.setEffect(dropShadow);
            shape.setStrokeWidth(2);

            stLine.setCycleCount(1);
            stLine.setAutoReverse(true);

            final FillTransition toTransition = new FillTransition(Duration.millis(100), toNode.getCircle());
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
            pane.getChildren().remove(circle);

            for (Link link : path) {
                final Line line = ((VisualLink) link).getLine();

                line.setStroke(Color.GRAY);
                line.setEffect(null);
                line.setStrokeWidth(1);

                final VisualNode fromNode = (VisualNode) link.getFirst();
                final VisualNode toNode = (VisualNode) link.getSecond();

                graph.fillCircle(fromNode);
                graph.fillCircle(toNode);
            }
        });
    }

    @Override
    public void graphChanged() {
        drawGraph();
    }
}
