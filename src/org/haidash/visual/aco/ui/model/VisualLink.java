package org.haidash.visual.aco.ui.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.util.Pair;

/**
 * Created by zonclayv on 05.03.16.
 */
public class VisualLink extends Link {

    private final static Logger LOGGER = Logger.getLogger(VisualLink.class);
    private final Line line;

    public VisualLink(int weight) {
        super(weight);

        this.line = new Line();
        line.setFill(null);
        line.setStroke(Color.GREY);
        line.setStrokeWidth(1);
    }

    public void initLocation() {

        final Pair<Integer, Integer> from = getFirst().getLocation();
        final Pair<Integer, Integer> to = getSecond().getLocation();

        if(from==null || to==null){
            LOGGER.error("One of the nodes is null");
            return;
        }

        line.setStartX(from.first);
        line.setStartY(from.second);
        line.setEndX(to.first);
        line.setEndY(to.second);
    }

    public Line getLine() {
        return line;
    }
}
