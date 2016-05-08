package org.haidash.visual.aco.ui.graph;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.util.Pair;

import static org.haidash.visual.aco.ui.Constants.CIRCLE_RADIUS;

/**
 * Created by zonclayv on 05.03.16.
 */
public class NodeView {

    private final Node node;
    private final Circle circle;
    private final StackPane pane;

    private Pair<Integer, Integer> position;

    private Point2D dragAnchor;

    private double initX;
    private double initY;

    private boolean isDrag;

    public NodeView(Node node) {

        this.node = node;
        this.pane = new StackPane();
        this.position = new Pair<>(0, 0);

        final int number = node.getNumber();

        final Text text = new Text(String.valueOf(number));
        text.setBoundsType(TextBoundsType.VISUAL);

        circle = new Circle();
        circle.setRadius(CIRCLE_RADIUS);
        circle.setUserData(number);

        pane.setCursor(Cursor.HAND);
        pane.getChildren().addAll(circle, text);

        pane.setOnMousePressed(me -> {
            isDrag = false;
            initX = pane.getLayoutX();
            initY = pane.getLayoutY();
            dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
        });

        pane.setOnMouseDragged(me -> {
            isDrag = true;
            double dragX = me.getSceneX() - dragAnchor.getX();
            double dragY = me.getSceneY() - dragAnchor.getY();
            //calculate new position of the circle
            int newXPosition = (int) (initX + dragX);
            int newYPosition = (int) (initY + dragY);

            setPosition(new Pair<>(newXPosition, newYPosition));
        });
    }

    public Circle getCircle() {
        return circle;
    }

    public Pane getPane() {
        return pane;
    }

    public boolean isDrag() {
        return isDrag;
    }

    public void setPosition(Pair<Integer, Integer> position) {
        this.position = position;

        this.pane.setLayoutX(position.first);
        this.pane.setLayoutY(position.second);
    }

    public Node getNode() {
        return node;
    }

    public Pair<Integer, Integer> getPosition() {
        return position;
    }
}
