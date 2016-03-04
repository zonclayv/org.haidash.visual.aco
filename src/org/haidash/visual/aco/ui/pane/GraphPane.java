package org.haidash.visual.aco.ui.pane;

import java.util.*;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
import org.haidash.visual.aco.model.entity.Graph;
import org.haidash.visual.aco.model.entity.Link;
import org.haidash.visual.aco.model.entity.Node;
import org.haidash.visual.aco.model.entity.Pair;
import org.haidash.visual.aco.ui.GraphChangeListener;

import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseButton.SECONDARY;

/**
 * Created by zonclayv on 04.03.16.
 */
public class GraphPane extends ScrollPane implements GraphChangeListener {

    public static final int GRID_ITEM_WIDTH = 16;
    public static final int GRID_ITEM_HEIGHT = 20;

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

        pane.prefHeightProperty().bind(heightProperty().subtract(5));
        pane.prefWidthProperty().bind(widthProperty().subtract(5));

        ChangeListener<Number> resizeLister =
                (final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) -> fillBackground(pane);

        pane.widthProperty().addListener(resizeLister);
        pane.heightProperty().addListener(resizeLister);

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

        if (button.equals(PRIMARY)) {
            graph.setStartNode(graph.getNode(number));

            if (number == targetNode.getNumber()) {
                graph.setTargetNode(null);
            }
        } else if (button.equals(SECONDARY)) {
            graph.setTargetNode(graph.getNode(number));

            if (number == startNode.getNumber()) {
                graph.setTargetNode(null);
            }
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

        fillCircle(node);

        StackPane layout = new StackPane();
        layout.setLayoutX((node.getLocation().first * GRID_ITEM_WIDTH) - 10);
        layout.setLayoutY((node.getLocation().second * GRID_ITEM_HEIGHT) - 10);
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

//        final AcoProperties properties = AcoProperties.getInstance();
//
//        final int[][] nodesMap = properties.getNodesMap();
//        final Pair<Integer, Integer>[] verticesMap = properties.getVerticesMap();
//
//        final int numNodes = properties.getNumNodes();
//
//        lines = new Line[numNodes][numNodes];
//
//        for (int i = 0; i < numNodes; i++) {
//            for (int j = 0; j < numNodes; j++) {
//
//                if (nodesMap[i][j] <= 0) {
//                    continue;
//                }
//
//                Pair<Integer, Integer> from = verticesMap[i];
//                Pair<Integer, Integer> to = verticesMap[j];
//
//                Line line =
//                        new Line(from.first * GRID_ITEM_WIDTH, from.second * GRID_ITEM_HEIGHT, to.first * GRID_ITEM_WIDTH, to.second
//                                * GRID_ITEM_HEIGHT);
//                line.setFill(null);
//                line.setStroke(Color.GREY);
//                line.setStrokeWidth(1);
//
//                lines[i][j] = line;
//
//                pane.getChildren().add(line);
//            }
//        }
//
//        circles = new Circle[numNodes];
//
//        for (int i = 0; i < verticesMap.length; i++) {
//            createCircle(pane, verticesMap[i], i);
//        }
    }

    private final void fillBackground(final Pane pane) {

        verticalGridLines.getElements().clear();
        horizontalGridLines.getElements().clear();

        pane.getChildren().clear();

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

        pane.getChildren().addAll(new javafx.scene.Node[]{verticalGridLines, horizontalGridLines});
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

    public void markPath(final List<Node> visitedNodes) {

        if (visitedNodes.size() < 2) {
            return;
        }

        Node currentNode = visitedNodes.get(0);

        final Circle circle = new Circle();
        circle.setRadius(10);
        circle.setStroke(Color.GREY);
        circle.setFill(Color.YELLOW);
        circle.setLayoutX(currentNode.getLocation().first);
        circle.setLayoutY(currentNode.getLocation().second);

        pane.getChildren().addAll(circle);

        SequentialTransition animation = new SequentialTransition();
        animation.setNode(circle);

        for (int j = 1; j < visitedNodes.size(); j++) {

            final Node nextNode = visitedNodes.get(j);
            final TranslateTransition transition = new TranslateTransition();
            transition.setFromX(currentNode.getLocation().first);
            transition.setFromY(currentNode.getLocation().second);
            transition.setToX(nextNode.getLocation().first);
            transition.setToY(nextNode.getLocation().second);
            transition.setDelay(Duration.millis(50));

            animation.getChildren().add(transition);
        }

        animation.setCycleCount(1);
        animation.play();

        animation.setOnFinished((ActionEvent) -> pane.getChildren().remove(circle));
    }

    @Override
    public void graphChanged() {

    }
}
