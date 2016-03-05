package org.haidash.visual.aco.ui.pane;

import java.util.*;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.util.Duration;
import org.haidash.visual.aco.algorithm.aco.entity.Graph;
import org.haidash.visual.aco.algorithm.aco.entity.Link;
import org.haidash.visual.aco.algorithm.aco.entity.Node;
import org.haidash.visual.aco.algorithm.aco.entity.Pair;
import org.haidash.visual.aco.ui.GraphChangeListener;

import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseButton.SECONDARY;

/**
 * Created by zonclayv on 04.03.16.
 */
public class GraphPane extends ScrollPane implements GraphChangeListener {

    public static final int GRID_ITEM_WIDTH = 16;
    public static final int GRID_ITEM_HEIGHT = 20;

    private final double SCALE_DELTA = 1.1;

    private final Path verticalGridLines = new Path();
    private final Path horizontalGridLines = new Path();

    private final Pane pane;
    private Map<Link, Line> lines;
    private Map<Node, Circle> circles;
    private Graph graph;

    public GraphPane(final Graph graph) {
        this.graph = graph;
        this.graph.addListener(this);
        this.lines = new HashMap<>();
        this.circles = new HashMap<>();

        pane = new Pane();
        pane.getStyleClass().setAll(new String[]{"chart-plot-background"});

//        pane.prefHeightProperty().bind(heightProperty().subtract(5));
//        pane.prefWidthProperty().bind(widthProperty().subtract(5));

        ChangeListener<Number> resizeLister =
                (final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) -> fillBackground();

        widthProperty().addListener(resizeLister);
        heightProperty().addListener(resizeLister);


        pane.getChildren().addAll(new javafx.scene.Node[]{verticalGridLines, horizontalGridLines});
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

    private void changeVertexStatus(final MouseEvent event, final Circle circle) {

        final Object userData = circle.getUserData();

        if (!(userData instanceof Integer)) {
            return;
        }

        int number = (int) userData;

        final Node startNode = graph.getStartNode();
        final Node targetNode = graph.getTargetNode();

        final MouseButton button = event.getButton();

        final Node node = graph.getNode(number);


        if (button.equals(PRIMARY)) {

            if (Objects.equals(node, targetNode)) {
                graph.setTargetNode(startNode);
            }

            graph.setStartNode(node);
        } else if (button.equals(SECONDARY)) {

            if (Objects.equals(node, startNode)) {
                graph.setStartNode(targetNode);
            }

            graph.setTargetNode(node);
        }

        fillCircle(startNode);
        fillCircle(targetNode);
        fillCircle(graph.getStartNode());
        fillCircle(graph.getTargetNode());
    }

    private void createCircle(final Pane parent, final Node node) {

        Text text = createText(String.valueOf(node.getNumber()));

        final Circle circle = new Circle();
        circle.setRadius(10);
        circle.setUserData(node.getNumber());

        circles.put(node, circle);

        fillCircle(node);

        StackPane layout = new StackPane();
        layout.setLayoutX(node.getLocation().first - 10);
        layout.setLayoutY(node.getLocation().second - 10);
        layout.setCursor(Cursor.HAND);
        layout.setOnMouseClicked((final MouseEvent event) -> changeVertexStatus(event, circle));
        layout.getChildren().addAll(circle, text);

        parent.getChildren().add(layout);
    }

    private Text createText(final String string) {
        Text text = new Text(string);
        text.setBoundsType(TextBoundsType.VISUAL);
        return text;
    }

    public void drawGraph() {

        final ObservableList<javafx.scene.Node> children = pane.getChildren();
        if (children.size() > 2) {
            children.remove(3, children.size());
        }

        fillBackground();

        lines.clear();

        for (Link link : graph.getLinks()) {
            final Node first = link.getFirst();
            final Node second = link.getSecond();

            final Pair<Integer, Integer> from = first.getLocation();
            final Pair<Integer, Integer> to = second.getLocation();

            final Line line = new Line(from.first, from.second, to.first, to.second);
            line.setFill(null);
            line.setStroke(Color.GREY);
            line.setStrokeWidth(1);

            lines.put(link, line);

            children.add(line);
        }

        circles.clear();

        for (Node node : graph.getNodes()) {
            createCircle(pane, node);
        }

        pane.layout();
    }

    private final void fillBackground() {

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

        verticalGridLines.getStyleClass().setAll(new String[]{"chart-vertical-grid-lines"});
        horizontalGridLines.getStyleClass().setAll(new String[]{"chart-horizontal-grid-lines"});
    }

    private void fillCircle(final Node node) {

        final Node startNode = graph.getStartNode();
        final Node targetNode = graph.getTargetNode();

        final Circle circle = circles.get(node);

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

    public void markPath(final List<Link> path) {

        if (path.size() < 1) {
            return;
        }

        Node currentNode = path.get(0).getFirst();

        final Circle circle = new Circle();
        circle.setRadius(10);
        circle.setStroke(Color.GREY);
        circle.setFill(Color.YELLOW);
        circle.setLayoutX(currentNode.getLocation().first - 10-190);
        circle.setLayoutY(currentNode.getLocation().second - 10);

        pane.getChildren().addAll(circle);

        SequentialTransition animation = new SequentialTransition();
        animation.setNode(circle);

        for (Link link : path) {
            final Node fromNode = link.getFirst();
            final Node toNode = link.getSecond();

            final TranslateTransition transition = new TranslateTransition();
            transition.setFromX(fromNode.getLocation().first - 10-190);
            transition.setFromY(fromNode.getLocation().second - 10);
            transition.setToX(toNode.getLocation().first - 10-190);
            transition.setToY(toNode.getLocation().second - 10);
            transition.setDelay(Duration.millis(50));

            animation.getChildren().add(transition);
        }

        animation.setCycleCount(1);
        animation.play();

        animation.setOnFinished((ActionEvent) -> pane.getChildren().remove(circle));
    }

    @Override
    public void graphChanged() {
        drawGraph();
    }
}
