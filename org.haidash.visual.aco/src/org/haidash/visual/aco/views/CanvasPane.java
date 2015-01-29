package org.haidash.visual.aco.views;

import org.haidash.visual.aco.algorithm.model.AcoProperties;
import org.haidash.visual.aco.algorithm.model.Pair;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

/**
 * Author Aleh Haidash.
 */
public class CanvasPane extends ScrollPane {
    public static final int GRID_ITEM_WIDTH = 16;
    public static final int GRID_ITEM_HEIGHT = 20;

    private final Path verticalGridLines = new Path();
    private final Path horizontalGridLines = new Path();
    private final Path horizontalRowFill = new Path();
    private final Path verticalRowFill = new Path();
    private final Region plotBackground = new Region();
    private final Pane pane;
    private Line[][] lines;

    public CanvasPane(final Scene scene) {

        double width = scene.getWidth();
        double height = scene.getHeight();

        setMinHeight(height - 10);
        setPrefHeight(height - 10);
        setMinWidth(width - 320);
        setPrefWidth(width - 320);


        pane = new Pane();
        pane.getStyleClass().setAll(new String[]{"chart-plot-background"});
        pane.setPrefSize(getPrefWidth() - 5, getPrefHeight() - 5);

        layoutGrid(pane);
        setContent(pane);
    }

    private final void layoutGrid(Pane pane) {

        verticalRowFill.getElements().clear();
        horizontalRowFill.getElements().clear();
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

        /*for (int i = 0; i <= pane.getPrefHeight() + GRID_ITEM_HEIGHT; i += GRID_ITEM_HEIGHT * 2) {
            for (int j = 0; j <= pane.getPrefWidth() + GRID_ITEM_WIDTH; j += GRID_ITEM_WIDTH * 2) {
                verticalRowFill.getElements().addAll(new PathElement[]{new MoveTo(i, j), new LineTo(i + GRID_ITEM_WIDTH, j), new LineTo(i + GRID_ITEM_WIDTH, j + GRID_ITEM_HEIGHT), new LineTo(i, j + GRID_ITEM_HEIGHT), new ClosePath()});
            }
        }

        for (int i = GRID_ITEM_HEIGHT; i <= pane.getPrefHeight() + GRID_ITEM_WIDTH; i += GRID_ITEM_HEIGHT * 2) {
            for (int j = GRID_ITEM_WIDTH; j <= pane.getPrefWidth() + GRID_ITEM_WIDTH; j += GRID_ITEM_WIDTH * 2) {
                horizontalRowFill.getElements().addAll(new PathElement[]{new MoveTo(i, j), new LineTo(i + GRID_ITEM_WIDTH, j), new LineTo(i + GRID_ITEM_WIDTH, j + GRID_ITEM_HEIGHT), new LineTo(i, j + GRID_ITEM_HEIGHT), new ClosePath()});
            }
        }

        verticalRowFill.getStyleClass().setAll(new String[]{"chart-alternative-column-fill"});
        horizontalRowFill.getStyleClass().setAll(new String[]{"chart-alternative-row-fill"});*/
        verticalGridLines.getStyleClass().setAll(new String[]{"chart-vertical-grid-lines"});
        horizontalGridLines.getStyleClass().setAll(new String[]{"chart-horizontal-grid-lines"});

        pane.getChildren().addAll(new Node[]{verticalGridLines, horizontalGridLines});
    }

    public void draw() {

        final AcoProperties properties = AcoProperties.getInstance();

        final int[][] nodesMap = properties.getNodesMap();
        final Pair[] verticesMap = properties.getVerticesMap();

        final int numNodes = properties.getNumNodes();
        final int startNode = properties.getStartNode();
        final int targetNode = properties.getTargetNode();

        int maxX = (int) pane.getPrefWidth();
        int maxY = (int) pane.getPrefHeight();

        for (Pair<Integer, Integer> vertex : verticesMap) {

            int tempX = vertex.first * GRID_ITEM_WIDTH;

            if (maxX < tempX) {
                maxX = tempX;
            }

            int tempY = vertex.second * GRID_ITEM_HEIGHT;

            if (maxY < tempY) {
                maxY = tempY;
            }
        }

        pane.setPrefWidth(maxX);
        pane.setPrefHeight(maxY);

        layoutGrid(pane);


        double prefHeight = pane.getPrefHeight();
        double prefWidth = pane.getPrefWidth();

        final ObservableList<Node> children = pane.getChildren();

        lines = new Line[numNodes][numNodes];

        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {

                if (nodesMap[i][j] <= 0) {
                    continue;
                }

                Pair<Integer, Integer> from = verticesMap[i];
                Pair<Integer, Integer> to = verticesMap[j];

                Line line = new Line(from.first * GRID_ITEM_WIDTH, from.second * GRID_ITEM_HEIGHT, to.first * GRID_ITEM_WIDTH, to.second * GRID_ITEM_HEIGHT);
                line.setFill(null);
                line.setStroke(Color.GREY);
                line.setStrokeWidth(1);

                lines[i][j] = line;

                children.add(line);
            }
        }

        for (int i = 0; i < verticesMap.length; i++) {
            Pair<Integer, Integer> vertex = verticesMap[i];

            Text text = createText(String.valueOf(i));
            Circle circle = encircle(text);

            if (i == startNode) {
                circle.setStroke(Color.GREEN);
                circle.setFill(Color.GREEN);
            } else if (i == targetNode) {
                circle.setStroke(Color.RED);
                circle.setFill(Color.RED);
            } else {
                circle.setStroke(Color.GREY);
                circle.setFill(Color.WHITESMOKE);
            }

            StackPane layout = new StackPane();
            layout.getChildren().addAll(circle,text);
            layout.setLayoutX(vertex.first * GRID_ITEM_WIDTH-10);
            layout.setLayoutY(vertex.second * GRID_ITEM_HEIGHT-10);

            children.add(layout);
        }
    }

    private Text createText(String string) {
        Text text = new Text(string);
        text.setBoundsType(TextBoundsType.VISUAL);

        return text;
    }

    private Circle encircle(Text text) {
        Circle circle = new Circle();
        final double PADDING = 5;
        circle.setRadius(getWidth(text) / 2 + PADDING);

        return circle;
    }

    private double getWidth(Text text) {
        new Scene(new Group(text));

        return text.getLayoutBounds().getWidth();
    }

    public void addListener(SimpleObjectProperty[][] pheromones) {

        final AcoProperties properties = AcoProperties.getInstance();
        final int numNodes = properties.getNumNodes();

        for (int i = 0; i < numNodes; i++)
            for (int j = 0; j < numNodes; j++) {

                final Line line = lines[i][j];

                pheromones[i][j].addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observableValue, Object o, Object t1) {
                        if (t1 instanceof Pair) {
                            Double pPheromones = ((Pair<Double, Double>) t1).first;
                            Double nPheromones = ((Pair<Double, Double>) t1).second;

                        }
                    }
                });
            }
    }
}
