package org.haidash.visual.aco.views;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

import java.io.File;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import org.haidash.visual.aco.algorithm.Colony;
import org.haidash.visual.aco.algorithm.model.AcoProperties;

/**
 * Author Aleh Haidash.
 */
public class GeneralScene extends Scene {

	// private final static Logger LOGGER = Logger.getLogger(GeneralScene.class);

	private final Group group;

	private TextArea textLog;
	private Button btnStart;
	private GraphCanvasPane canvasPane;
	private SettingsBox settingsBox;
	private ScrollPane logPane;
	private Button btnBrowse;

	private MenuItem runItem;

	public GeneralScene(final Group group, final double v, final double v1) {
		super(group, v, v1);

		this.group = group;

		initControls();
	}

	private Button createButton(final String title, final String iconPath) {
		final Image image = new Image(getClass().getResourceAsStream(iconPath));

		final Button btn = new Button();
		btn.setGraphic(new ImageView(image));
		btn.setTooltip(new Tooltip(title));
		btn.setPrefHeight(18);
		btn.setPrefWidth(18);
		btn.setAlignment(Pos.CENTER);
		return btn;
	}

	private void createLog(final Pane parent) {
		textLog = new TextArea();
		textLog.setWrapText(true);
		textLog.setEditable(false);
		textLog.prefWidth(getWidth());
		textLog.setPrefRowCount(7);

		TextAreaAppender.setTextArea(textLog);

		parent.getChildren().add(textLog);
	}

	private void createMenu(final Pane parent) {
		MenuBar mainMenu = new MenuBar();

		Menu file = new Menu("File");
		MenuItem openFileItem = new MenuItem("Open File");
		openFileItem.setOnAction((final ActionEvent t) -> openFile(this, canvasPane));

		MenuItem exitAppItem = new MenuItem("Exit");
		exitAppItem.setOnAction((final ActionEvent t) -> Platform.exit());

		file.getItems().addAll(openFileItem, exitAppItem);

		Menu edit = new Menu("Edit");
		MenuItem propertiesItem = new MenuItem("Properties");
		edit.getItems().add(propertiesItem);

		Menu view = new Menu("View");
		CheckMenuItem settingsItem = new CheckMenuItem("Settings");
		settingsItem.setSelected(true);
		settingsItem.selectedProperty().addListener((final Observable valueModel) -> settingsBox.setVisible(settingsItem.isSelected()));

		CheckMenuItem logItem = new CheckMenuItem("Log");
		logItem.setSelected(true);
		logItem.selectedProperty().addListener((final Observable valueModel) -> logPane.setVisible(logItem.isSelected()));

		view.getItems().addAll(settingsItem, logItem);

		Menu app = new Menu("Application");
		runItem = new MenuItem("Run");
		runItem.setOnAction((final ActionEvent t) -> findPath());
		runItem.setDisable(true);
		app.getItems().add(runItem);

		Menu help = new Menu("Help");
		MenuItem visitWebsite = new MenuItem("Visit Website");
		help.getItems().add(visitWebsite);

		mainMenu.getMenus().addAll(file, edit, view, app, help);

		parent.getChildren().add(mainMenu);
	}

	private void createToolBar(final Pane parent) {
		ToolBar toolbar = new ToolBar();

		btnBrowse = createButton("Open", "/icons/open.png");
		btnBrowse.addEventHandler(MOUSE_CLICKED, (final MouseEvent mouseEvent) -> openFile(this, canvasPane));

		btnStart = createButton("Start", "/icons/next.png");
		btnStart.addEventHandler(MouseEvent.MOUSE_CLICKED, (final MouseEvent mouseEvent) -> findPath());
		btnStart.setDisable(true);

		final Button btnCancel = createButton("Exit", "/icons/off.png");
		btnCancel.addEventHandler(MouseEvent.MOUSE_CLICKED, (final MouseEvent mouseEvent) -> Platform.exit());

		toolbar.getItems().addAll(btnBrowse, btnStart, btnCancel);

		parent.getChildren().add(toolbar);
	}

	private void findPath() {
		textLog.setText("");

		new Thread(() -> {
			try {
				Colony ac = new Colony();
				ac.start();
			} catch (final Throwable t) {
			}
		}).start();

		// SearchResult searchResult = ac.getSearchResult();
		//
		// if (searchResult != null) {
		// // List<Integer> visited = searchResult.getVisited();
		// // canvasPane.markPath(visited);
		// }
	}

	private void initControls() {

		VBox generalVBox = new VBox();
		generalVBox.setAlignment(Pos.TOP_CENTER);
		group.getChildren().add(generalVBox);

		createMenu(generalVBox);
		createToolBar(generalVBox);

		canvasPane = new GraphCanvasPane(this);
		settingsBox = new SettingsBox(this, canvasPane);

		HBox generalHBox = new HBox();
		generalHBox.setAlignment(Pos.CENTER);
		generalHBox.getChildren().addAll(canvasPane, settingsBox);

		generalVBox.getChildren().addAll(generalHBox);

		createLog(generalVBox);
	}

	private void openFile(final Scene scene, final GraphCanvasPane canvasPane) {
		textLog.setText("");

		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select input file");

		final File file = fileChooser.showOpenDialog(scene.getWindow());

		if ((file != null) && file.getName().endsWith(".txt")) {
			AcoProperties.getInstance().initializeValue(file, false);
			btnStart.setDisable(false);
			runItem.setDisable(false);
		} else {
			btnStart.setDisable(true);
			runItem.setDisable(true);
			return;
		}

		// canvasPane.drawGraph();
	}
}
