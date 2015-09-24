package org.haidash.visual.aco.ui.pane;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.TOP_CENTER;

/**
 * Created by zonclayv on 14.07.15.
 */
public class RootScene extends Scene {


    public RootScene(final Group group, final double v, final double v1) {
        super(group, v, v1);

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

        final Menu view = new Menu("View");
        final CheckMenuItem settingsItem = new CheckMenuItem("Settings");
        settingsItem.setSelected(true);

        view.getItems().add(settingsItem);

        final Menu app = new Menu("Application");

        final Menu help = new Menu("Help");
        help.getItems().add(new MenuItem("Visit Website"));

        mainMenu.getMenus().addAll(file, edit, view, app, help);

        parent.getChildren().add(mainMenu);
    }


    private void addControls(final Pane parent) {

        final VBox settingsBox = new SettingsBox();
        settingsBox.prefHeightProperty().bind(heightProperty());

        final VBox centralVBox = new CentralBox();
        centralVBox.prefWidthProperty().bind(widthProperty().subtract(settingsBox.getPrefWidth()));

        parent.getChildren().addAll(centralVBox, settingsBox);
    }
}
