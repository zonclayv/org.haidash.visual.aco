package org.haidash.visual.aco.views;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

import java.io.File;
import java.util.List;

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
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import org.haidash.visual.aco.algorithm.Colony;
import org.haidash.visual.aco.algorithm.model.AcoProperties;
import org.haidash.visual.aco.algorithm.model.SearchResult;

/**
 * Author Aleh Haidash.
 */
public class GeneralScene extends Scene {

	// private final static Logger LOGGER = Logger.getLogger(GeneralScene.class);

	private final Group group;

	private Text textLog;
	private Button btnStart;
	private GraphCanvasPane canvasPane;
	private SettingsBox settingsBox;
	private ScrollPane logPane;
	private Button btnBrowse;

	public GeneralScene(Group group, double v, double v1) {
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

	private void openFile(final Scene scene, final GraphCanvasPane canvasPane) {
		textLog.setText("");

		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select input file");

		final File file = fileChooser.showOpenDialog(scene.getWindow());

		if (file != null && file.getName().endsWith(".txt")) {
			AcoProperties.getInstance().initializeValue(file);
			btnStart.setDisable(false);
		} else {
			btnStart.setDisable(true);
			return;
		}

		canvasPane.drawGraph();
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

	private void createLog(final Pane parent) {
		logPane = new ScrollPane();
		logPane.setPrefHeight(200);
		logPane.setPrefWidth(getWidth());
		logPane.setFitToWidth(true);
		logPane.setFitToHeight(true);

		textLog = new Text();
		textLog.prefWidth(logPane.getPrefWidth());
		textLog.prefHeight(logPane.getPrefHeight());

		logPane.setContent(textLog);

		parent.getChildren().add(logPane);
	}

	private void createToolBar(final Pane parent) {
		ToolBar toolbar = new ToolBar();

		btnBrowse = createButton("Open", "/icons/open.png");
		btnBrowse.addEventHandler(MOUSE_CLICKED, (MouseEvent mouseEvent) -> openFile(this, canvasPane));

		btnStart = createButton("Start", "/icons/next.png");
		btnStart.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> findPath());
		btnStart.setDisable(true);

		final Button btnCancel = createButton("Exit", "/icons/off.png");
		btnCancel.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> Platform.exit());

		toolbar.getItems().addAll(btnBrowse, btnStart, btnCancel);

		parent.getChildren().add(toolbar);
	}

	private void createMenu(final Pane parent) {
		MenuBar mainMenu = new MenuBar();
		
		Menu file = new Menu("File");
		MenuItem openFile = new MenuItem("Open File");
		openFile.setOnAction((ActionEvent t) -> openFile(this, canvasPane));

		MenuItem exitApp = new MenuItem("Exit");
		exitApp.setOnAction((ActionEvent t) -> Platform.exit());

		file.getItems().addAll(openFile, exitApp);

		Menu edit = new Menu("Edit");
		MenuItem properties = new MenuItem("Properties");
		edit.getItems().add(properties);

		Menu view = new Menu("View");
		CheckMenuItem settings = new CheckMenuItem("Settings");
		settings.setSelected(true);
		settings.selectedProperty().addListener((Observable valueModel) -> settingsBox.setVisible(settings.isSelected()));

		CheckMenuItem log = new CheckMenuItem("Log");
		log.setSelected(true);
		log.selectedProperty().addListener((Observable valueModel) -> logPane.setVisible(log.isSelected()));

		view.getItems().addAll(settings, log);

		Menu app = new Menu("Application");
		MenuItem run = new MenuItem("Run");
		run.setOnAction((ActionEvent t) -> findPath());
		app.getItems().add(run);

		Menu help = new Menu("Help");
		MenuItem visitWebsite = new MenuItem("Visit Website");
		help.getItems().add(visitWebsite);

		mainMenu.getMenus().addAll(file, edit, view, app, help);

		parent.getChildren().add(mainMenu);
	}

	private void findPath() {
		Colony ac = new Colony();
		ac.start();

		SearchResult searchResult = ac.getSearchResult();

		String message = "Path not fount";

		if (searchResult != null) {
			List<Integer> visited = searchResult.getVisited();
			message =
					"Best path: "
							+ searchResult.getTotalCost()
							+ " \n"
							+ "Path "
							+ visited
							+ "\n"
							+ "Spent fuels"
							+ searchResult.getSpentFuelLevel();

			canvasPane.markPath(visited);
		}

		textLog.setText(message);
	}
}
