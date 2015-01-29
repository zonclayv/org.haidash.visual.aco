package org.haidash.visual.aco;
import org.haidash.visual.aco.views.GeneralScene;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.Stage;


public class AntColonyOptimization extends Application {

   /* @Override
    public void start(Stage stage) throws Exception{
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("views/view.fxml"));

        final Parent root = (Parent) loader.load();
        final Controller controller = loader.getController();
        controller.setActiveStage(stage);

        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setScene(new GeneralScene(root));
        stage.show();
    }*/

    @Override
    public void start(final Stage stage) throws Exception {

        Group root = new Group();
        GeneralScene scene = new GeneralScene(root,900, 650);

        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
