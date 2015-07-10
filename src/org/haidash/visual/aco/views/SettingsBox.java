package org.haidash.visual.aco.views;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.haidash.visual.aco.oop.entity.Properties;

/**
 * Author Aleh Haidash.
 */
public class SettingsBox extends VBox {

	public static final NumberFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
	private final Properties properties = Properties.getInstance();

	public SettingsBox(final Scene scene, final GraphCanvasPane canvasPane) {

		configuration(scene);

		setAlignment(Pos.TOP_CENTER);
		setPrefWidth(getPrefWidth());
		setStyle("-fx-border-color: white;");

		final Label labelTitle = new Label("Settings");
		labelTitle.setPrefHeight(25);
		labelTitle.setAlignment(Pos.TOP_CENTER);
		labelTitle.setPrefWidth(getPrefWidth());

		getChildren().addAll(labelTitle);

		addSeparator(this);

		createIntSlider(this, "Q", properties.getQ());
		createDoubleSlider(this, "Alpha", properties.getAlpha());
		createDoubleSlider(this, "Beta", properties.getBeta());

		addSeparator(this);

		createIntSlider(this, "Ants", properties.getNumAnts());
		createIntSlider(this, "Gen", properties.getNumGeneration());
	}

	private void addSeparator(final VBox firstVBox) {
		Separator separator = new Separator();
		separator.setPrefHeight(25);
		separator.setValignment(VPos.CENTER);
		separator.setPrefWidth(getPrefWidth());

		firstVBox.getChildren().add(separator);
	}

	private void configuration(final Scene scene) {
		setMinWidth(250);
		setPrefWidth(250);
		prefHeightProperty().bind(scene.heightProperty().subtract(200));
		setPadding(new Insets(5));
		setAlignment(Pos.TOP_CENTER);
	}

	private Slider createDoubleSlider(final Pane parent, final String name, final SimpleIntegerProperty property) {

		double prefWidth = parent.getPrefWidth();

		final Label label = new Label(name);
		label.setPrefHeight(25);
		label.setPrefWidth(prefWidth * 0.2);
		label.setAlignment(Pos.CENTER_LEFT);

		final Slider slider = new Slider(1, 5, property.get());
		slider.setBlockIncrement(1);
		slider.setPrefWidth(prefWidth * 0.65);
		slider.setPrefHeight(25);

		property.bind(slider.valueProperty());

		final Label value = new Label();
		value.setPrefHeight(25);
		value.setPrefWidth(prefWidth * 0.15);
		value.setAlignment(Pos.CENTER_RIGHT);
		value.textProperty().bind(Bindings.format("%.1f", slider.valueProperty()));

		final HBox settingsHBox = new HBox();
		settingsHBox.setAlignment(Pos.CENTER);
		settingsHBox.setPrefWidth(prefWidth);
		settingsHBox.getChildren().addAll(label, slider, value);

		parent.getChildren().add(settingsHBox);

		return slider;
	}

	private void createIntSlider(final Pane parent, final String name, final IntegerProperty property) {

		double prefWidth = parent.getPrefWidth();

		final Label label = new Label(name);
		label.setPrefHeight(15);
		label.setPrefWidth(prefWidth * 0.2);
		label.setAlignment(Pos.CENTER_LEFT);

		final Slider slider = new Slider(1, 400, 10);
		slider.setBlockIncrement(1);
		slider.setPrefWidth(prefWidth * 0.65);
		slider.setPrefHeight(25);

		property.bind(slider.valueProperty());

		final Label value = new Label();
		value.setPrefHeight(25);
		value.setPrefWidth(prefWidth * 0.15);
		value.setAlignment(Pos.CENTER_RIGHT);
		value.textProperty().bind(Bindings.format("%.0f", slider.valueProperty()));

		final HBox settingsHBox = new HBox();
		settingsHBox.setAlignment(Pos.CENTER);
		settingsHBox.setPrefWidth(parent.getPrefWidth());
		settingsHBox.getChildren().addAll(label, slider, value);

		parent.getChildren().add(settingsHBox);
	}
}
