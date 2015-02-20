package org.haidash.visual.aco.views;

import java.util.List;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
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

import org.haidash.visual.aco.algorithm.model.AcoProperties;
import org.haidash.visual.aco.algorithm.model.Pair;

/**
 * Author Aleh Haidash.
 */
public class GraphCanvasPane extends ScrollPane {

	public static final int GRID_ITEM_WIDTH = 16;
	public static final int GRID_ITEM_HEIGHT = 20;

	private final Path verticalGridLines = new Path();
	private final Path horizontalGridLines = new Path();

	private final Pane pane;
	private Line[][] lines;
	private Circle[] circles;

	public GraphCanvasPane(final Scene scene) {

		prefHeightProperty().bind(scene.heightProperty().subtract(200));
		prefWidthProperty().bind(scene.widthProperty().subtract(250));

		pane = new Pane();
		pane.getStyleClass().setAll(new String[] { "chart-plot-background" });

		pane.prefHeightProperty().bind(heightProperty().subtract(5));
		pane.prefWidthProperty().bind(widthProperty().subtract(5));

		ChangeListener<Number> resizeLister =
				(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) -> fillBackgroud(pane);

		pane.widthProperty().addListener(resizeLister);
		pane.heightProperty().addListener(resizeLister);

		setContent(pane);
	}

	private void changeVertexStatus(final MouseEvent event, final Circle circle) {
		final AcoProperties properties = AcoProperties.getInstance();

		final int numNodes = properties.getNumNodes();

		Object userData = circle.getUserData();

		if (!(userData instanceof Integer)) {
			return;
		}

		int number = (int) userData;

		MouseButton button = event.getButton();

		if (button.equals(MouseButton.PRIMARY)) {
			properties.setStartNode(number);

			if (number == properties.getTargetNode()) {
				properties.setTargetNode(-1);
			}
		} else if (button.equals(MouseButton.SECONDARY)) {
			properties.setTargetNode(number);

			if (number == properties.getStartNode()) {
				properties.setStartNode(-1);
			}
		}

		for (int i = 0; i < numNodes; i++) {
			fillCircle(i);
		}
	}

	private void createCircle(final Pane parent, final Pair<Integer, Integer> vertex, final int number) {

		Text text = createText(String.valueOf(number));

		final Circle circle = new Circle();
		circle.setRadius(10);
		circle.setUserData(number);

		circles[number] = circle;

		fillCircle(number);

		StackPane layout = new StackPane();
		layout.setLayoutX((vertex.first * GRID_ITEM_WIDTH) - 10);
		layout.setLayoutY((vertex.second * GRID_ITEM_HEIGHT) - 10);
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

		final AcoProperties properties = AcoProperties.getInstance();

		final int[][] nodesMap = properties.getNodesMap();
		final Pair<Integer, Integer>[] verticesMap = properties.getVerticesMap();

		final int numNodes = properties.getNumNodes();

		lines = new Line[numNodes][numNodes];

		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {

				if (nodesMap[i][j] <= 0) {
					continue;
				}

				Pair<Integer, Integer> from = verticesMap[i];
				Pair<Integer, Integer> to = verticesMap[j];

				Line line =
						new Line(from.first * GRID_ITEM_WIDTH, from.second * GRID_ITEM_HEIGHT, to.first * GRID_ITEM_WIDTH, to.second
								* GRID_ITEM_HEIGHT);
				line.setFill(null);
				line.setStroke(Color.GREY);
				line.setStrokeWidth(1);

				lines[i][j] = line;

				pane.getChildren().add(line);
			}
		}

		circles = new Circle[numNodes];

		for (int i = 0; i < verticesMap.length; i++) {
			createCircle(pane, verticesMap[i], i);
		}
	}

	private final void fillBackgroud(final Pane pane) {

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

		verticalGridLines.getStyleClass().setAll(new String[] { "chart-vertical-grid-lines" });
		horizontalGridLines.getStyleClass().setAll(new String[] { "chart-horizontal-grid-lines" });

		pane.getChildren().addAll(new Node[] { verticalGridLines, horizontalGridLines });
	}

	private void fillCircle(final int i) {

		final AcoProperties properties = AcoProperties.getInstance();

		final int startNode = properties.getStartNode();
		final int targetNode = properties.getTargetNode();

		Circle circle = circles[i];

		if (i == startNode) {
			circle.setStroke(Color.GREY);
			circle.setFill(Color.GREEN);
		} else if (i == targetNode) {
			circle.setStroke(Color.GREY);
			circle.setFill(Color.RED);
		} else {
			circle.setStroke(Color.GREY);
			circle.setFill(Color.WHITESMOKE);
		}
	}

	public void markPath(final List<Integer> visitedNodes) {

		if (visitedNodes.size() < 2) {
			return;
		}

		Circle startCircle = circles[visitedNodes.get(0)];

		final Circle circle = new Circle();
		circle.setRadius(10);
		circle.setStroke(Color.GREY);
		circle.setFill(Color.YELLOW);
		circle.setLayoutX(startCircle.getLayoutX() - 5);
		circle.setLayoutY(startCircle.getLayoutY() - 5);

		pane.getChildren().addAll(circle);

		SequentialTransition animation = new SequentialTransition();
		animation.setNode(circle);

		for (int j = 1; j < visitedNodes.size(); j++) {
			Line line = lines[visitedNodes.get(j - 1)][visitedNodes.get(j)];

			TranslateTransition transition = new TranslateTransition();
			transition.setFromX(line.getStartX() - 5);
			transition.setFromY(line.getStartY() - 5);
			transition.setToX(line.getEndX() - 5);
			transition.setToY(line.getEndY() - 5);
			transition.setDelay(Duration.millis(50));

			animation.getChildren().add(transition);
		}

		animation.setCycleCount(1);
		animation.play();

		animation.setOnFinished((ActionEvent) -> pane.getChildren().remove(circle));
	}
}
