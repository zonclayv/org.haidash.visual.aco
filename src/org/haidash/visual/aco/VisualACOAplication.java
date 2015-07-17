package org.haidash.visual.aco;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.haidash.visual.aco.ui.pane.RootScene;


public class VisualACOAplication extends Application {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {

        Group root = new Group();
        Scene scene = new RootScene(root, 900, 650);

        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }
}
