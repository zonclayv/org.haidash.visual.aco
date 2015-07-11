package org.haidash.visual.aco;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.Stage;
import org.haidash.visual.aco.views.GeneralScene;


public class AntColonyOptimization extends Application {

	public static void main(final String[] args) {
		launch(args);
	}

    @Override
    public void start(final Stage stage) throws Exception {

        Group root = new Group();
		GeneralScene scene = new GeneralScene(root, 900, 650);

        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }
}
