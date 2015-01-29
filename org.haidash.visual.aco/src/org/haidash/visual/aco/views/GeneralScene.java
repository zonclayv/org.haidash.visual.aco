package org.haidash.visual.aco.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;

import org.apache.log4j.Logger;

/**
 * Author Aleh Haidash.
 */
public class GeneralScene extends Scene {


    private final static Logger LOGGER = Logger.getLogger(GeneralScene.class);

    private final Group group;
    private Line[][] lines;

    public GeneralScene(Group group, double v, double v1) {
        super(group, v, v1);

        this.group = group;

        initControls();
    }

    private void initControls() {

        final CanvasPane canvasPane = new CanvasPane(this);
        final SettingsBox settingsBox = new SettingsBox(this, canvasPane);

        HBox generalHBox = new HBox(5);
        generalHBox.getChildren().addAll(canvasPane, settingsBox);
        generalHBox.setAlignment(Pos.CENTER);

        HBox.setMargin(canvasPane, new Insets(5, 5, 5, 5));
        HBox.setMargin(settingsBox, new Insets(5, 5, 5, 5));

        group.getChildren().add(generalHBox);
    }
}
