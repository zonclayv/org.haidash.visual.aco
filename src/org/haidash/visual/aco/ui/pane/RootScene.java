package org.haidash.visual.aco.ui.pane;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.haidash.visual.aco.model.entity.Graph;

import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.TOP_CENTER;

/**
 * Created by zonclayv on 14.07.15.
 */
public class RootScene extends Scene {

    private RadioMenuItem simulateItem;
    private RadioMenuItem reportItem;

    private Graph graph;

    public RootScene(final Group group, final double v, final double v1) {
        super(group, v, v1);

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

        mode.getItems().addAll(simulateItem,reportItem);

        final Menu app = new Menu("Application");

        final Menu help = new Menu("Help");
        help.getItems().add(new MenuItem("Visit Website"));

        mainMenu.getMenus().addAll(file, edit, mode, app, help);

        parent.getChildren().add(mainMenu);
    }


    private void addControls(final Pane parent) {

        final VBox settingsBox = new SettingsBox(this);
        settingsBox.prefHeightProperty().bind(heightProperty());
        settingsBox.visibleProperty().bind(simulateItem.selectedProperty());

        final VBox centralVBox = new CentralBox(this);
        centralVBox.prefWidthProperty().bind(widthProperty().subtract(settingsBox.getPrefWidth()));
        centralVBox.visibleProperty().bind(simulateItem.selectedProperty());

        parent.getChildren().addAll(centralVBox, settingsBox);
    }

    public Graph getGraph() {
        return graph;
    }
}
