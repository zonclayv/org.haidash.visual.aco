package org.haidash.visual.aco.ui.pane;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.haidash.visual.aco.ui.TextAreaAppender;
import org.haidash.visual.aco.ui.model.VisualGraph;

import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.TOP_CENTER;

/**
 * Created by zonclayv on 14.07.15.
 */
public class RootScene extends Scene {

    private RadioMenuItem simulateItem;
    private RadioMenuItem reportItem;

    private VisualGraph graph;

    public RootScene(final Group group, final double v, final double v1) {
        super(group, v, v1);

        this.graph = new VisualGraph();

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
        final MenuItem openFileItem = new MenuItem("Open File");

        final MenuItem exitAppItem = new MenuItem("Exit");
        exitAppItem.setOnAction((final ActionEvent t) -> Platform.exit());

        file.getItems().addAll(openFileItem, exitAppItem);

        final Menu edit = new Menu("Edit");
        edit.getItems().add(new MenuItem("Properties"));

        final Menu mode = new Menu("Mode");
        final ToggleGroup modeGroup = new ToggleGroup();

        simulateItem = new RadioMenuItem("Simulate");
        simulateItem.setSelected(true);
        simulateItem.setToggleGroup(modeGroup);

        reportItem = new RadioMenuItem("Report");
        reportItem.setToggleGroup(modeGroup);

        mode.getItems().addAll(simulateItem, reportItem);

        final Menu app = new Menu("Application");

        final Menu help = new Menu("Help");
        help.getItems().add(new MenuItem("Visit Website"));

        mainMenu.getMenus().addAll(file, edit, mode, app, help);

        parent.getChildren().add(mainMenu);
    }


    private void addControls(final Pane parent) {

        final TextArea textLog = new TextArea();
        textLog.setWrapText(true);
        textLog.setEditable(false);
        textLog.visibleProperty().bind(simulateItem.selectedProperty());
        textLog.setPrefRowCount(7);
        textLog.setPrefHeight(150);

        VBox.setMargin(textLog, new Insets(5));

        final VBox generalVBox = new VBox();
        generalVBox.setAlignment(Pos.TOP_CENTER);

        final VBox settingsBox = new SettingsBox(graph);
        settingsBox.prefHeightProperty().bind(heightProperty());
        settingsBox.visibleProperty().bind(simulateItem.selectedProperty());

        final VBox centralVBox = new CentralBox(graph);
        centralVBox.prefWidthProperty().bind(widthProperty().subtract(settingsBox.getPrefWidth()));
        centralVBox.visibleProperty().bind(simulateItem.selectedProperty());

        final HBox generalHBox = new HBox();
        generalHBox.setAlignment(Pos.CENTER);
        generalHBox.prefHeightProperty().bind(heightProperty().subtract(textLog.prefHeightProperty()).subtract(40));
        generalHBox.getChildren().addAll(centralVBox, settingsBox);

        generalVBox.getChildren().addAll(generalHBox,textLog);

        TextAreaAppender.setTextArea(textLog);

        parent.getChildren().add(generalVBox);
    }
}
