package org.haidash.visual.aco.ui.graph;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.haidash.visual.aco.algorithm.graph.entity.Link;

/**
 * Created by zonclayv on 05.03.16.
 */
public class LinkView {

    private final Link link;
    private final Line line;

    public LinkView(Link link) {

        this.link = link;

        this.line = new Line();
        line.setFill(null);
        line.setStroke(Color.GREY);
        line.setStrokeWidth(1);
    }

    public Line getLine() {
        return line;
    }

    public Link getLink() {
        return link;
    }
}
