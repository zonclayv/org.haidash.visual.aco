package org.haidash.visual.aco.ui.pane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.model.ACOUtils;
import org.haidash.visual.aco.model.entity.Graph;
import org.haidash.visual.aco.reader.GraphReader;
import org.haidash.visual.aco.ui.TextAreaAppender;

import javax.annotation.processing.Messager;
import java.io.File;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

/**
 * Created by zonclayv on 14.07.15.
 */
public class CentralBox extends VBox {

    private final static Logger LOGGER = Logger.getLogger(CentralBox.class);
    private final Graph graph;

    private TextField fileBrowseText;

    public CentralBox(RootScene rootScene) {

        this.graph = rootScene.getGraph();

        setPadding(new Insets(5, 5, 0, 5));
        setAlignment(Pos.TOP_CENTER);

        addControls();
    }

    private void addControls() {

        final Button browseButton = new Button();
        browseButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/open.png"))));
        browseButton.setTooltip(new Tooltip("Browse"));
        browseButton.addEventHandler(MOUSE_CLICKED, (final MouseEvent mouseEvent) -> openFile());

        final Button runButton = new Button();
        runButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/next.png"))));
        runButton.setTooltip(new Tooltip("Run"));
        runButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (final MouseEvent mouseEvent) -> findPath());

        fileBrowseText = new TextField();
        fileBrowseText.setEditable(false);
        fileBrowseText.addEventHandler(MOUSE_CLICKED, (final MouseEvent mouseEvent) -> openFile());
        fileBrowseText.prefWidthProperty().bind(widthProperty().subtract(browseButton.getWidth()).subtract(runButton.getWidth()));

        final HBox hBox = new HBox();
        hBox.getChildren().addAll(fileBrowseText, browseButton, runButton);

        HBox.setMargin(browseButton, new Insets(0, 5, 0, 5));

        getChildren().add(hBox);

        final GraphPane graphPane = new GraphPane(graph);
        graphPane.prefWidthProperty().bind(prefWidthProperty());
        graphPane.prefHeightProperty().bind(heightProperty().subtract(hBox.heightProperty()));

        VBox.setMargin(graphPane, new Insets(5, 0, 0, 0));

        getChildren().add(graphPane);
    }

    private void findPath() {

        if (!graph.isReady()) {
            final Alert alert = new Alert(Alert.AlertType.WARNING, "Graph is not ready");
            alert.showAndWait();
            return;
        }

        graph.clear();

        new Thread(() -> {
            ACOUtils.runACO(graph);
        }).start();
    }

    private void openFile() {

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select input file");

        final File file = fileChooser.showOpenDialog(getScene().getWindow());

        if ((file != null) && file.getName().endsWith(".txt")) {

            fileBrowseText.setText(file.getAbsolutePath());

            final GraphReader fileReader = new GraphReader(graph);

            try {
                fileReader.read(file);

                graph.fireListener();

                LOGGER.info("Graph initialized...");
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }

        }
    }
}
