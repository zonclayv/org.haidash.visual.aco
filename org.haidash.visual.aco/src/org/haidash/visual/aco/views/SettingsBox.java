package org.haidash.visual.aco.views;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.haidash.visual.aco.algorithm.Colony;
import org.haidash.visual.aco.algorithm.model.AcoProperties;
import org.haidash.visual.aco.algorithm.model.SearchResult;

/**
 * Author Aleh Haidash.
 */
public class SettingsBox extends VBox {

    public static final NumberFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
    private final AcoProperties properties = AcoProperties.getInstance();

    public SettingsBox(final Scene scene, final CanvasPane canvasPane) {

        setMinHeight(scene.getHeight() - 10);
        setPrefHeight(scene.getHeight() - 10);
        setMaxHeight(scene.getHeight() - 10);
        setMaxWidth(scene.getWidth() - canvasPane.getPrefWidth() - 20);
        setPrefWidth(scene.getWidth() - canvasPane.getPrefWidth() - 20);
        setMinWidth(scene.getWidth() - canvasPane.getPrefWidth() - 20);
        setAlignment(Pos.TOP_CENTER);

        VBox firstVBox = new VBox(0);
        firstVBox.setAlignment(Pos.CENTER);
        firstVBox.setPrefWidth(getPrefWidth());

        final Label labelTitle = new Label("Settings");
        labelTitle.setPrefHeight(50);
        labelTitle.setAlignment(Pos.TOP_CENTER);
        labelTitle.setPrefWidth(getPrefWidth());

        firstVBox.getChildren().addAll(labelTitle);

        final TextField textInput = new TextField();
        textInput.setPrefHeight(50);
        textInput.setPrefWidth(200);
        textInput.setAlignment(Pos.CENTER_LEFT);
        textInput.setEditable(false);

        final Button btnBrowse = new Button("Browse");
        btnBrowse.setPrefHeight(50);
        btnBrowse.setPrefWidth(80);
        btnBrowse.setAlignment(Pos.CENTER_LEFT);

        HBox settingsHBox = new HBox(5);
        settingsHBox.setAlignment(Pos.CENTER);
        settingsHBox.setPrefWidth(getPrefWidth());
        settingsHBox.getChildren().addAll(textInput, btnBrowse);

        Separator separator = new Separator();
        separator.setPrefHeight(50);
        separator.setValignment(VPos.CENTER);
        separator.setPrefWidth(getPrefWidth());

        firstVBox.getChildren().addAll(settingsHBox, separator);

        final Label labelQ = new Label("Q");
        labelQ.setPrefHeight(50);
        labelQ.setPrefWidth(200);
        labelQ.setAlignment(Pos.CENTER_LEFT);

        final TextField textQ = new TextField();
        textQ.setPrefHeight(50);
        textQ.setPrefWidth(80);
        textQ.setAlignment(Pos.CENTER_RIGHT);

        settingsHBox = new HBox(5);
        settingsHBox.setAlignment(Pos.CENTER);
        settingsHBox.setPrefWidth(getPrefWidth());
        settingsHBox.getChildren().addAll(labelQ, textQ);

        final Slider sliderQ = new Slider();
        sliderQ.setMax(100);
        sliderQ.setMin(0.1);
        sliderQ.setBlockIncrement(0.1);
        sliderQ.setMajorTickUnit(0.1);
        sliderQ.setPrefWidth(getPrefWidth());
        sliderQ.setPrefHeight(50);
        sliderQ.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number oldVal, Number newVal) {

                properties.setQ(newVal.doubleValue());
                textQ.setText(DECIMAL_FORMAT.format(newVal));
            }
        });

        firstVBox.getChildren().addAll(settingsHBox, sliderQ);

        final Label labelAlpha = new Label("Alpha");
        labelAlpha.setPrefHeight(50);
        labelAlpha.setPrefWidth(200);
        labelAlpha.setAlignment(Pos.CENTER_LEFT);

        final TextField textAlpha = new TextField();
        textAlpha.setPrefHeight(50);
        textAlpha.setPrefWidth(80);
        textAlpha.setAlignment(Pos.CENTER_RIGHT);

        settingsHBox = new HBox(5);
        settingsHBox.setAlignment(Pos.CENTER);
        settingsHBox.setPrefWidth(getPrefWidth());
        settingsHBox.getChildren().addAll(labelAlpha, textAlpha);

        final Slider sliderAlpha = new Slider();
        sliderAlpha.setMax(1);
        sliderAlpha.setMin(0.1);
        sliderAlpha.setBlockIncrement(0.1);
        sliderAlpha.setMajorTickUnit(0.1);
        sliderAlpha.setPrefWidth(getPrefWidth());
        sliderAlpha.setPrefHeight(50);
        sliderAlpha.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number oldVal, Number newVal) {

                properties.setAlpha(newVal.doubleValue());
                textAlpha.setText(DECIMAL_FORMAT.format(newVal));
            }
        });

        firstVBox.getChildren().addAll(settingsHBox, sliderAlpha);

        final Label labelBeta = new Label("Beta");
        labelBeta.setPrefHeight(50);
        labelBeta.setPrefWidth(200);
        labelBeta.setAlignment(Pos.CENTER_LEFT);

        final TextField textBeta = new TextField();
        textBeta.setPrefHeight(50);
        textBeta.setPrefWidth(80);
        textBeta.setAlignment(Pos.CENTER_RIGHT);

        settingsHBox = new HBox(5);
        settingsHBox.setAlignment(Pos.CENTER);
        settingsHBox.setPrefWidth(getPrefWidth());
        settingsHBox.getChildren().addAll(labelBeta, textBeta);

        final Slider sliderBeta = new Slider();
        sliderBeta.setMax(1);
        sliderBeta.setMin(0.1);
        sliderBeta.setBlockIncrement(0.1);
        sliderBeta.setMajorTickUnit(0.1);
        sliderBeta.setPrefWidth(getPrefWidth());
        sliderBeta.setPrefHeight(50);
        sliderBeta.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number oldVal, Number newVal) {

                properties.setBeta(newVal.doubleValue());
                textBeta.setText(DECIMAL_FORMAT.format(newVal));
            }
        });

        firstVBox.getChildren().addAll(settingsHBox, sliderBeta);

        separator = new Separator();
        separator.setPrefHeight(50);
        separator.setValignment(VPos.CENTER);
        separator.setPrefWidth(getPrefWidth());

        firstVBox.getChildren().add(separator);

        final Label labelNumAnts = new Label("Num of ants");
        labelNumAnts.setPrefHeight(50);
        labelNumAnts.setPrefWidth(200);
        labelNumAnts.setAlignment(Pos.CENTER_LEFT);

        final TextField textNumAnts = new TextField();
        textNumAnts.setPrefHeight(50);
        textNumAnts.setPrefWidth(80);
        textNumAnts.setAlignment(Pos.CENTER_RIGHT);

        settingsHBox = new HBox(5);
        settingsHBox.setAlignment(Pos.CENTER);
        settingsHBox.setPrefWidth(getPrefWidth());
        settingsHBox.getChildren().addAll(labelNumAnts, textNumAnts);

        final Slider sliderNumAnts = new Slider();
        sliderNumAnts.setMax(100);
        sliderNumAnts.setMin(1);
        sliderNumAnts.setBlockIncrement(1);
        sliderNumAnts.setMajorTickUnit(1);
        sliderNumAnts.setPrefWidth(getPrefWidth());
        sliderNumAnts.setPrefHeight(50);
        sliderNumAnts.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number oldVal, Number newVal) {

                properties.setNumAnts(newVal.intValue());
                textNumAnts.setText(String.valueOf(newVal.intValue()));
            }
        });


        firstVBox.getChildren().addAll(settingsHBox, sliderNumAnts);

        final Label labelNumGeneration = new Label("Num of generation");
        labelNumGeneration.setPrefHeight(50);
        labelNumGeneration.setPrefWidth(200);
        labelNumGeneration.setAlignment(Pos.CENTER_LEFT);

        final TextField textNumGeneration = new TextField();
        textNumGeneration.setPrefHeight(50);
        textNumGeneration.setPrefWidth(80);
        textNumGeneration.setAlignment(Pos.CENTER_RIGHT);

        settingsHBox = new HBox(5);
        settingsHBox.setAlignment(Pos.CENTER);
        settingsHBox.setPrefWidth(getPrefWidth());
        settingsHBox.getChildren().addAll(labelNumGeneration, textNumGeneration);

        final Slider sliderNumGeneration = new Slider();
        sliderNumGeneration.setMax(100);
        sliderNumGeneration.setMin(1);
        sliderNumGeneration.setBlockIncrement(1);
        sliderNumGeneration.setMajorTickUnit(1);
        sliderNumGeneration.setPrefWidth(getPrefWidth());
        sliderNumGeneration.setPrefHeight(50);
        sliderNumGeneration.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number oldVal, Number newVal) {

                properties.setNumGeneration(newVal.intValue());
                textNumGeneration.setText(String.valueOf(newVal.intValue()));
            }
        });

        firstVBox.getChildren().addAll(settingsHBox, sliderNumGeneration);

        VBox secondVBox = new VBox(0);
        secondVBox.setPrefWidth(getPrefWidth());
        secondVBox.setPrefHeight(getPrefHeight() - firstVBox.getPrefHeight());
        secondVBox.setAlignment(Pos.BOTTOM_CENTER);

        ScrollPane logPane = new ScrollPane();
        logPane.setPrefWidth(secondVBox.getPrefWidth() - 20);
        logPane.setPrefHeight(getPrefHeight() - firstVBox.getPrefHeight() - 100);
        logPane.setFitToWidth(true);
        logPane.setFitToHeight(true);

        final Text logText = new Text();
        logText.prefWidth(logPane.getPrefWidth() - 20);
        logText.prefHeight(logPane.getPrefHeight() - 20);

        logPane.setContent(logText);

        secondVBox.getChildren().add(logPane);

        final Button btnStart = new Button("Start");
        btnStart.setPrefHeight(25);
        btnStart.setPrefWidth(80);
        btnStart.setAlignment(Pos.CENTER);
        btnStart.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                Colony ac = new Colony();

                SimpleObjectProperty[][] pheromones = ac.getGlobalPheromones();
                canvasPane.addListener(pheromones);

                ac.start();

                SearchResult searchResult = ac.getSearchResult();

                String message = "Path not fount";

                if (searchResult != null) {
                    message = "Best path: " + searchResult.getTotalCost() + " \n" + searchResult.getVisited().toString();
                }

                logText.setText(message);
            }
        });

        final Button btnCancel = new Button("Cancel");
        btnCancel.setPrefHeight(25);
        btnCancel.setPrefWidth(80);
        btnCancel.setAlignment(Pos.CENTER);
        btnCancel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Platform.exit();
            }
        });

        settingsHBox = new HBox(5);
        settingsHBox.setAlignment(Pos.BOTTOM_CENTER);
        settingsHBox.setPrefWidth(getPrefWidth());
        settingsHBox.getChildren().addAll(btnStart, btnCancel);

        secondVBox.getChildren().add(settingsHBox);


        VBox.setMargin(logPane, new Insets(5, 5, 5, 5));
        VBox.setMargin(settingsHBox, new Insets(5, 5, 5, 5));

        getChildren().addAll(firstVBox, secondVBox);

        btnBrowse.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                final FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select input file");

                final File file = fileChooser.showOpenDialog(scene.getWindow());

                if (file != null && file.getName().endsWith(".txt")) {
                    textInput.setText(file.getName());
                    properties.initializeValue(file);
                } else {
                    return;
                }

                canvasPane.draw();

                setPropertyValue(textQ, sliderQ, properties.getQ());
                setPropertyValue(textAlpha, sliderAlpha, properties.getAlpha());
                setPropertyValue(textBeta, sliderBeta, properties.getBeta());
                setPropertyValue(textNumAnts, sliderNumAnts, properties.getNumAnts());
                setPropertyValue(textNumGeneration, sliderNumGeneration, properties.getNumGeneration());
            }
        });
    }

    private void setPropertyValue(TextField textField, Slider slider, double value) {
        textField.setText(String.valueOf(DECIMAL_FORMAT.format(value)));
        slider.setValue(value);
    }

    private void setPropertyValue(TextField textField, Slider slider, int value) {
        textField.setText(String.valueOf(value));
        slider.setValue(value);
    }

}
