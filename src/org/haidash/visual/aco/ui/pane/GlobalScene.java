package org.haidash.visual.aco.ui.pane;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.algorithm.AntColonyRunner;
import org.haidash.visual.aco.algorithm.colony.AntColony;
import org.haidash.visual.aco.algorithm.colony.impl.AntColonyImpl;
import org.haidash.visual.aco.algorithm.graph.Graph;
import org.haidash.visual.aco.algorithm.graph.entity.Link;
import org.haidash.visual.aco.algorithm.graph.process.GraphWriter;
import org.haidash.visual.aco.algorithm.util.ACOParameters;
import org.haidash.visual.aco.algorithm.util.SearchHistory;
import org.haidash.visual.aco.algorithm.util.SearchResult;
import org.haidash.visual.aco.ui.TextAreaAppender;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.TOP_CENTER;
import static javafx.scene.control.Alert.AlertType.WARNING;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

/**
 * Created by zonclayv on 14.07.15.
 */
public class GlobalScene extends Scene {

    private final static Logger LOGGER = Logger.getLogger(GlobalScene.class);

    private RadioMenuItem simulateItem;
    private RadioMenuItem reportItem;

    private Graph graph;
    private MenuItem exportItem;
    private GraphPane graphPane;
    private VBox graphVBox;
    private VBox reportVBox;
    private TextField fileBrowseText;
    private ProgressIndicator progressIndicator;
    private Button runButton;
    private SettingsPane settingsVBox;

    public GlobalScene(final Group group, final double v, final double v1) {
        super(group, v, v1);

        getStylesheets().add("/icons/style.css");

        this.graph = new Graph();

        final VBox generalVBox = new VBox();
        generalVBox.setAlignment(TOP_CENTER);
        group.getChildren().add(generalVBox);

        createMenu(generalVBox);

        final HBox generalHBox = new HBox();
        generalHBox.setAlignment(CENTER);

        addControls(generalHBox);

        generalVBox.getChildren().addAll(generalHBox);
    }

    private void createMenu(final Pane parent) {

        final MenuBar mainMenu = new MenuBar();
        final Menu file = new Menu("File");

        final MenuItem exitAppItem = new MenuItem("Exit");
        exitAppItem.setOnAction((final ActionEvent t) -> Platform.exit());

        file.getItems().addAll(exitAppItem);

        final Menu edit = new Menu("Graph");

        final MenuItem openFileItem = new MenuItem("Open...");
        openFileItem.setOnAction(event -> {
            graphPane.openFile((value) -> {
                fileBrowseText.setText(value);
                runButton.setDisable(false);
            });
        });

        exportItem = new MenuItem("Export...");
        exportItem.setOnAction(event -> export());

        edit.getItems().addAll(openFileItem, exportItem);
        edit.getItems().add(new MenuItem("Properties"));

        final Menu mode = new Menu("Mode");
        final ToggleGroup modeGroup = new ToggleGroup();

        simulateItem = new RadioMenuItem("Simulate");
        simulateItem.setSelected(true);
        simulateItem.setOnAction(event -> {
            graphVBox.toFront();
            reportVBox.setPickOnBounds(false);
            graphVBox.setPickOnBounds(true);

        });
        simulateItem.setToggleGroup(modeGroup);

        reportItem = new RadioMenuItem("Report");
        reportItem.setOnAction(event -> {
            reportVBox.toFront();
            reportVBox.setPickOnBounds(true);
            graphVBox.setPickOnBounds(false);
        });
        reportItem.setToggleGroup(modeGroup);

        mode.getItems().addAll(simulateItem, reportItem);

        final Menu help = new Menu("Help");
        help.getItems().add(new MenuItem("About"));

        mainMenu.getMenus().addAll(file, edit, mode, help);

        parent.getChildren().add(mainMenu);
    }

    private void addControls(final Pane parent) {

        final VBox rootVBox = new VBox();
        rootVBox.prefHeightProperty().bind(heightProperty());
        rootVBox.prefWidthProperty().bind(widthProperty());
        rootVBox.setPadding(new Insets(5));

        parent.getChildren().add(rootVBox);

        final StackPane stackPane = new StackPane();
        stackPane.prefHeightProperty().bind(rootVBox.heightProperty());
        stackPane.prefWidthProperty().bind(rootVBox.widthProperty());
        stackPane.setPadding(new Insets(5, 0, 0, 0));

        rootVBox.getChildren().add(stackPane);

        final Button browseButton = new Button();
        browseButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/open.png"))));
        browseButton.setTooltip(new Tooltip("Browse"));
        browseButton.addEventHandler(MOUSE_CLICKED, (final MouseEvent mouseEvent) -> graphPane.openFile((value) -> {
            fileBrowseText.setText(value);
            runButton.setDisable(false);
        }));

        final StackPane runStackPane = new StackPane();

        runButton = new Button();
        runButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/next.png"))));
        runButton.setTooltip(new Tooltip("Run"));
        runButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (final MouseEvent mouseEvent) -> findPath());
        runButton.setDisable(true);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        runStackPane.getChildren().addAll(runButton, progressIndicator);

        fileBrowseText = new TextField();
        fileBrowseText.setEditable(false);
        fileBrowseText.addEventHandler(MOUSE_CLICKED, (final MouseEvent mouseEvent) -> {
            graphPane.openFile((value) -> {
                fileBrowseText.setText(value);
                runButton.setDisable(false);
            });
        });
        fileBrowseText.prefWidthProperty().bind(widthProperty().subtract(browseButton.getWidth()).subtract(runButton.getWidth()));

        final HBox filePathHBox = new HBox();
        filePathHBox.getChildren().addAll(fileBrowseText, browseButton, runStackPane);

        HBox.setMargin(fileBrowseText, new Insets(0, 5, 0, 0));
        HBox.setMargin(browseButton, new Insets(0, 5, 0, 0));
        HBox.setMargin(runStackPane, new Insets(-12, 0, 0, 0));

        graphVBox = new VBox();
        graphVBox.setAlignment(Pos.TOP_CENTER);
        graphVBox.visibleProperty().bind(simulateItem.selectedProperty());

        settingsVBox = new SettingsPane(graph);
        settingsVBox.prefHeightProperty().bind(stackPane.heightProperty());

        graphPane = new GraphPane(graph);
        graphPane.prefWidthProperty().bind(stackPane.widthProperty().subtract(settingsVBox.getPrefWidth()));

        HBox.setMargin(graphPane, new Insets(0, 0, 0, 0));

        final TextArea textLog = new TextArea();
        textLog.setWrapText(true);
        textLog.setEditable(false);
        textLog.setPrefRowCount(7);
        textLog.setPrefHeight(150);

        final HBox graphHBox = new HBox();
        graphHBox.setAlignment(Pos.CENTER);
        graphHBox.prefHeightProperty().bind(stackPane.heightProperty().subtract(textLog.prefHeightProperty()).subtract(75));
        graphHBox.getChildren().addAll(graphPane, settingsVBox);

        VBox.setMargin(graphHBox, new Insets(-5, 0, 0, 0));
        VBox.setMargin(textLog, new Insets(5, 0, 0, 0));

        graphVBox.getChildren().addAll(filePathHBox, graphHBox, textLog);

        TextAreaAppender.setTextArea(textLog);

        reportVBox = new ReportPane();
        reportVBox.setAlignment(Pos.TOP_CENTER);
        reportVBox.prefHeightProperty().bind(heightProperty().subtract(40));
        reportVBox.prefWidthProperty().bind(widthProperty());
        reportVBox.setPickOnBounds(false);
        reportVBox.visibleProperty().bind(reportItem.selectedProperty());

        stackPane.getChildren().addAll(graphVBox, reportVBox);

    }

    private void findPath() {

        if (!graph.isReady()) {
            final Alert alert = new Alert(WARNING, "Graph is not ready");
            alert.showAndWait();
            return;
        }

        runButton.setDisable(true);
        progressIndicator.setVisible(true);
        settingsVBox.setDisable(true);
        graphPane.setDisableGraph(true);
        progressIndicator.toFront();

        // loads the items at another thread, asynchronously
        Task listLoader = new Task<SearchHistory>() {
            {
                setOnSucceeded(event -> {
                    progressIndicator.toBack();
                    runButton.setDisable(false);
                    graphPane.setDisableGraph(false);
                    progressIndicator.setVisible(false);
                    settingsVBox.setDisable(false);

                    final SearchHistory searchResult = getValue();

                    if (searchResult == null || !searchResult.isPathFound()) {
                        return;
                    }

                    final ACOParameters instance = ACOParameters.INSTANCE;
                    final boolean animation = instance.getAnimation().get();

                    if (animation) {
                        final List<Link> path = searchResult.getBestSolution().getPath();
                        graphPane.markPath(path);
                    }
                });

                setOnFailed(LOGGER::error);
            }

            @Override
            protected SearchHistory call() throws Exception {

                AntColony antColony = new AntColonyImpl(graph);

                final SearchHistory result = antColony.run();

                if (result == null || !result.isPathFound()) {
                    return null;
                }

                GraphWriter writer = new GraphWriter(graph);
                writer.writeHistory(result);

                return result;
            }
        };

        Thread loadingThread = new Thread(listLoader, "Ant colony runner");
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    private void export() {

        if (!graph.isReady()) {
            final Alert alert = new Alert(WARNING, "Graph is not valid");
            alert.showAndWait();
            return;
        }

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select output file");

        final File file = fileChooser.showSaveDialog(getWindow());

        if (file == null) {
            return;
        }

        final GraphWriter writer = new GraphWriter(graph);
        writer.writeGraph(Paths.get(file.getAbsolutePath()));
    }


}
