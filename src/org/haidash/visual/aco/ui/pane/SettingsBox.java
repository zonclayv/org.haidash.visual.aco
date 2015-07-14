package org.haidash.visual.aco.ui.pane;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.haidash.visual.aco.model.entity.Properties;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Author Aleh Haidash.
 */
public class SettingsBox extends VBox {

    public static final NumberFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
    private final Properties properties = Properties.getInstance();

    public SettingsBox() {

        setMinWidth(250);
        setPrefWidth(250);
        setPadding(new Insets(5));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-border-color: white;");

        final Label labelTitle = new Label("Settings");
        labelTitle.setAlignment(Pos.TOP_CENTER);
        labelTitle.setPrefWidth(getPrefWidth());
        labelTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

        getChildren().add(labelTitle);

        setMargin(labelTitle, new Insets(0, 0, 5, 0));

        addSeparator();

        final HBox settingsHBox = new HBox();
        settingsHBox.setAlignment(Pos.CENTER);
        settingsHBox.setPrefWidth(getPrefWidth());

        addSpinner(settingsHBox, "Alpha", properties.getAlpha());
        addSpinner(settingsHBox, "Beta", properties.getBeta());

        getChildren().add(settingsHBox);

        addSeparator();

        addSlider("Q", properties.getQ());
        addSlider("Ants", properties.getNumAnts());
        addSlider("Gen", properties.getNumGeneration());
    }

    private void addSeparator() {

        final Separator separator = new Separator();
        separator.setValignment(VPos.CENTER);
        separator.setPrefWidth(getPrefWidth());

        getChildren().add(separator);
    }

    private void addSpinner(final Pane parent, final String name, final IntegerProperty property) {

        double prefWidth = getPrefWidth();

        final Label label = new Label(name);
        label.setPrefWidth(prefWidth * 0.20);
        label.setAlignment(Pos.CENTER);

        final Spinner<Integer> spinner = new Spinner<>();
        spinner.setPrefWidth(prefWidth * 0.30);
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, property.get(), 1));

        property.bind(spinner.valueProperty());

        parent.getChildren().addAll(label, spinner);

        HBox.setMargin(label, new Insets(5, 0, 5, 0));
        HBox.setMargin(spinner, new Insets(5, 10, 5, 0));
    }

    private void addSlider(final String name, final IntegerProperty property) {

        double prefWidth = getPrefWidth();

        final Label label = new Label(name);
        label.setPrefWidth(prefWidth * 0.15);
        label.setAlignment(Pos.CENTER_LEFT);

        final Slider slider = new Slider(1, 400, 10);
        slider.setBlockIncrement(1);
        slider.setPrefWidth(prefWidth * 0.70);
        slider.setPrefHeight(25);

        property.bind(slider.valueProperty());

        final Label value = new Label();
        value.setPrefHeight(25);
        value.setPrefWidth(prefWidth * 0.15);
        value.setAlignment(Pos.CENTER_RIGHT);
        value.textProperty().bind(Bindings.format("%.0f", slider.valueProperty()));

        final HBox settingsHBox = new HBox();
        settingsHBox.setAlignment(Pos.CENTER);
        settingsHBox.setPrefWidth(getPrefWidth());
        settingsHBox.getChildren().addAll(label, slider, value);

        getChildren().add(settingsHBox);
    }
}
