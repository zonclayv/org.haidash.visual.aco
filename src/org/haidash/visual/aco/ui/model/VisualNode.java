package org.haidash.visual.aco.ui.model;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.entity.Node;
import org.haidash.visual.aco.algorithm.util.Pair;

import java.util.List;

import static org.haidash.visual.aco.ui.Constants.CIRCLE_RADIUS;

/**
 * Created by zonclayv on 05.03.16.
 */
public class VisualNode extends Node {

    private final Circle circle;
    private final StackPane pane;

    private Point2D dragAnchor;

    private double initX;
    private double initY;

    private boolean isDrag;

    public VisualNode(int number, int fuelBalance) {
        super(number, fuelBalance);

        pane = new StackPane();
        final Text text = new Text(String.valueOf(number));
        text.setBoundsType(TextBoundsType.VISUAL);

        circle = new Circle();
        circle.setRadius(CIRCLE_RADIUS);
        circle.setUserData(number);

        pane.setCursor(Cursor.HAND);
        pane.getChildren().addAll(circle, text);

        pane.setOnMousePressed(me -> {
            isDrag=false;
            initX = pane.getLayoutX();
            initY = pane.getLayoutY();
            dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
        });

        pane.setOnMouseDragged(me -> {
            isDrag=true;
            double dragX = me.getSceneX() - dragAnchor.getX();
            double dragY = me.getSceneY() - dragAnchor.getY();
            //calculate new position of the circle
            int newXPosition = (int)(initX + dragX);
            int newYPosition = (int)(initY + dragY);
            //if new position do not exceeds borders of the rectangle, translate to this position
            pane.setLayoutX(newXPosition);
            pane.setLayoutY(newYPosition);

            setLocation(new Pair<>(newXPosition+10, newYPosition+10));
        });
    }

    public Circle getCircle() {
        return circle;
    }

    public void initLocation() {
        pane.setLayoutX(getLocation().first - 10);
        pane.setLayoutY(getLocation().second - 10);
    }

    public Pane getPane() {
        return pane;
    }

    public boolean isDrag() {
        return isDrag;
    }
}
