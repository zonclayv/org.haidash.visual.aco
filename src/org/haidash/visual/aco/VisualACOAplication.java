package org.haidash.visual.aco;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.haidash.visual.aco.ui.pane.GlobalScene;


public class VisualACOAplication extends Application {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {

        final Group root = new Group();

        final Scene scene = new GlobalScene(root, 900, 650);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }
}
