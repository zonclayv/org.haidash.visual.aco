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
import org.haidash.visual.aco.algorithm.aco.entity.ACOParameters;
import org.haidash.visual.aco.algorithm.aco.entity.Graph;
import org.haidash.visual.aco.ui.GraphChangeListener;

/**
 * Author Aleh Haidash.
 */
public class SettingsBox extends VBox implements GraphChangeListener {

    private static final ACOParameters ACO_PARAMETERS = ACOParameters.INSTANCE;
    private final Graph graph;
    private final Spinner<Integer> fromSpinner;
    private final Spinner<Integer> toSpinner;

    public SettingsBox(RootScene rootScene) {

        this.graph = rootScene.getGraph();
        this.graph.addListener(this);

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

        final HBox nodesHBox = new HBox();
        nodesHBox.setAlignment(Pos.CENTER);
        nodesHBox.setPrefWidth(getPrefWidth());

        double prefWidth = getPrefWidth();

        final Label fromLabel = new Label("From");
        fromLabel.setPrefWidth(prefWidth * 0.20);
        fromLabel.setAlignment(Pos.CENTER);

        fromSpinner = new Spinner<>();
        fromSpinner.setPrefWidth(prefWidth * 0.30);
        fromSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0, 1));
        fromSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            graph.setStartNode(graph.getNode(newValue));
        });

        nodesHBox.getChildren().addAll(fromLabel, fromSpinner);

        HBox.setMargin(fromLabel, new Insets(5, 0, 5, 0));
        HBox.setMargin(fromSpinner, new Insets(5, 10, 5, 0));

        final Label toLabel = new Label("To");
        toLabel.setPrefWidth(prefWidth * 0.20);
        toLabel.setAlignment(Pos.CENTER);

        toSpinner = new Spinner<>();
        toSpinner.setPrefWidth(prefWidth * 0.30);
        toSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0, 1));
        toSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            graph.setTargetNode(graph.getNode(newValue));
        });

        nodesHBox.getChildren().addAll(toLabel, toSpinner);

        HBox.setMargin(toLabel, new Insets(5, 0, 5, 0));
        HBox.setMargin(toSpinner, new Insets(5, 10, 5, 0));

        getChildren().add(nodesHBox);

        addSeparator();

        final HBox constHBox = new HBox();
        constHBox.setAlignment(Pos.CENTER);
        constHBox.setPrefWidth(getPrefWidth());

        addSpinner(constHBox, "Alpha", ACO_PARAMETERS.getAlpha());
        addSpinner(constHBox, "Beta", ACO_PARAMETERS.getBeta());

        getChildren().add(constHBox);

        addSeparator();

        addSlider("Q", ACO_PARAMETERS.getQ());
        addSlider("Ants", ACO_PARAMETERS.getNumAnts());
        addSlider("Gen", ACO_PARAMETERS.getNumGeneration());
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

    @Override
    public void graphChanged() {
        try {
            fromSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, graph.getNodes().size(), graph.getStartNode().getNumber(), 1));
            toSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, graph.getNodes().size(), graph.getTargetNode().getNumber(), 1));

            fromSpinner.setDisable(false);
            fromSpinner.setDisable(false);

        } catch (Exception e) {
            fromSpinner.setDisable(true);
            fromSpinner.setDisable(true);
        }

    }
}
