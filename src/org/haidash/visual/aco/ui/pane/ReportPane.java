package org.haidash.visual.aco.ui.pane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import org.haidash.visual.aco.algorithm.graph.process.task.LoadHistoryTask;
import org.haidash.visual.aco.algorithm.util.SearchHistory;
import org.haidash.visual.aco.algorithm.util.Solution;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

/**
 * Created by zonclayv on 23.04.16.
 */
public class ReportPane extends VBox {

    private final static Logger LOGGER = Logger.getLogger(ReportPane.class);

    private final Service<ObservableList<SearchHistory>> service;
    private TextField fileBrowseText;
    private TableView tableView;
    private Path openedPath;
    private LineChart<Number, Number> chartTimeAnt;
    private LineChart<Number, Number> chartGenTime;
    private PieChart chartFirstBest;

    public ReportPane() {

        this.service = new Service<ObservableList<SearchHistory>>() {
            @Override
            protected Task<ObservableList<SearchHistory>> createTask() {
                return new LoadHistoryTask((items) -> fillCharts(items)) {
                    @Override
                    public Path getPath() {
                        return openedPath;
                    }
                };
            }
        };

        init();
    }

    private void init() {
        final Button browseButton = new Button();
        browseButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/open.png"))));
        browseButton.setTooltip(new Tooltip("Browse"));
        browseButton.addEventHandler(MOUSE_CLICKED, (final MouseEvent mouseEvent) -> openFile((value) -> {
            fileBrowseText.setText(value);
        }));

        fileBrowseText = new TextField();
        fileBrowseText.setEditable(false);
        fileBrowseText.addEventHandler(MOUSE_CLICKED, (final MouseEvent mouseEvent) -> {
            openFile((value) -> {
                fileBrowseText.setText(value);
            });
        });
        fileBrowseText.prefWidthProperty().bind(widthProperty().subtract(browseButton.getWidth()));

        final HBox filePathHBox = new HBox();
        filePathHBox.getChildren().addAll(fileBrowseText, browseButton);

        HBox.setMargin(fileBrowseText, new Insets(0, 5, 0, 0));

        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.prefHeightProperty().bind(heightProperty().subtract(60));
        scrollPane.prefWidthProperty().bind(widthProperty().subtract(10));
        scrollPane.setContent(getReportContent(scrollPane));

        VBox.setMargin(scrollPane, new Insets(5, 0, 5, 0));

        getChildren().addAll(filePathHBox, scrollPane);
    }

    private Node getReportContent(ScrollPane scrollPane) {
        final VBox vBox = new VBox();
        vBox.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        vBox.setPadding(new Insets(5));

        final StackPane stack = new StackPane();
        stack.prefWidthProperty().bind(vBox.prefWidthProperty().subtract(25));

        tableView = new TableView();
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.setMaxHeight(300);

        final Region veil = new Region();
        veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
        final ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(150, 150);

        //Define table columns
        final TableColumn nameCol = new TableColumn();
        nameCol.setText("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, String>("graphName"));
        tableView.getColumns().add(nameCol);
        final TableColumn sizeCol = new TableColumn();
        sizeCol.setText("Size");
        sizeCol.setPrefWidth(50);
        sizeCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, Integer>("graphSize"));
        tableView.getColumns().add(sizeCol);
        final TableColumn fromCol = new TableColumn();
        fromCol.setText("From");
        fromCol.setPrefWidth(50);
        fromCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, Integer>("startNode"));
        tableView.getColumns().add(fromCol);
        final TableColumn toCol = new TableColumn();
        toCol.setText("To");
        toCol.setPrefWidth(40);
        toCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, Integer>("targetNode"));
        tableView.getColumns().add(toCol);
        final TableColumn alphaCol = new TableColumn();
        alphaCol.setText("Alpha");
        alphaCol.setPrefWidth(50);
        alphaCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, Integer>("alpha"));
        tableView.getColumns().add(alphaCol);
        final TableColumn betaCol = new TableColumn();
        betaCol.setText("Beta");
        betaCol.setPrefWidth(50);
        betaCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, Integer>("beta"));
        tableView.getColumns().add(betaCol);
        final TableColumn qCol = new TableColumn();
        qCol.setText("Q");
        qCol.setPrefWidth(40);
        qCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, Integer>("q"));
        tableView.getColumns().add(qCol);
        final TableColumn antsCol = new TableColumn();
        antsCol.setText("Ants");
        antsCol.setPrefWidth(50);
        antsCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, Integer>("antCount"));
        tableView.getColumns().add(antsCol);
        final TableColumn genCol = new TableColumn();
        genCol.setText("Populations");
        genCol.setMinWidth(100);
        genCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, Integer>("generationCount"));
        tableView.getColumns().add(genCol);

        final TableColumn firstSolutionCol = new TableColumn();
        firstSolutionCol.setText("First Solution");

        final TableColumn firstCostCol = new TableColumn();
        firstCostCol.setText("Cost");
        firstCostCol.setPrefWidth(50);
        firstCostCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, String>("firstSolution"));
        firstCostCol.setCellFactory(new Callback<TableColumn<SearchHistory, Solution>, TableCell<SearchHistory, Solution>>() {
            @Override
            public TableCell<SearchHistory, Solution> call(TableColumn<SearchHistory, Solution> p) {
                final TableCell<SearchHistory, Solution> cell = new TableCell<SearchHistory, Solution>() {
                    @Override
                    public void updateItem(final Solution item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setText("");
                        } else {
                            this.setText(item.getTotalCost() + "");
                        }
                    }
                };
                return cell;
            }
        });

        final TableColumn firstGenCol = new TableColumn();
        firstGenCol.setText("Nr.");
        firstGenCol.setPrefWidth(40);
        firstGenCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, String>("firstSolution"));
        firstGenCol.setCellFactory(new Callback<TableColumn<SearchHistory, Solution>, TableCell<SearchHistory, Solution>>() {
            @Override
            public TableCell<SearchHistory, Solution> call(TableColumn<SearchHistory, Solution> p) {
                final TableCell<SearchHistory, Solution> cell = new TableCell<SearchHistory, Solution>() {
                    @Override
                    public void updateItem(final Solution item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setText("");
                        } else {
                            this.setText(item.getGeneration() + "");
                        }
                    }
                };
                return cell;
            }
        });

        final TableColumn firstTimeCol = new TableColumn();
        firstTimeCol.setText("Time, ms");
        firstTimeCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, String>("firstSolution"));
        firstTimeCol.setCellFactory(new Callback<TableColumn<SearchHistory, Solution>, TableCell<SearchHistory, Solution>>() {
            @Override
            public TableCell<SearchHistory, Solution> call(TableColumn<SearchHistory, Solution> p) {
                final TableCell<SearchHistory, Solution> cell = new TableCell<SearchHistory, Solution>() {
                    @Override
                    public void updateItem(final Solution item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setText("");
                        } else {
                            this.setText(item.getTime() + "");
                        }
                    }
                };
                return cell;
            }
        });

        firstSolutionCol.getColumns().addAll(firstCostCol, firstGenCol, firstTimeCol);
        tableView.getColumns().add(firstSolutionCol);

        final TableColumn bestSolutionCol = new TableColumn();
        bestSolutionCol.setText("Best Solution");

        final TableColumn bestCostCol = new TableColumn();
        bestCostCol.setText("Cost");
        bestCostCol.setPrefWidth(50);
        bestCostCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, String>("bestSolution"));
        bestCostCol.setCellFactory(new Callback<TableColumn<SearchHistory, Solution>, TableCell<SearchHistory, Solution>>() {
            @Override
            public TableCell<SearchHistory, Solution> call(TableColumn<SearchHistory, Solution> p) {
                final TableCell<SearchHistory, Solution> cell = new TableCell<SearchHistory, Solution>() {
                    @Override
                    public void updateItem(final Solution item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setText("");
                        } else {
                            this.setText(item.getTotalCost() + "");
                        }
                    }
                };
                return cell;
            }
        });

        final TableColumn bestGenCol = new TableColumn();
        bestGenCol.setText("Nr.");
        bestGenCol.setPrefWidth(40);
        bestGenCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, String>("bestSolution"));
        bestGenCol.setCellFactory(new Callback<TableColumn<SearchHistory, Solution>, TableCell<SearchHistory, Solution>>() {
            @Override
            public TableCell<SearchHistory, Solution> call(TableColumn<SearchHistory, Solution> p) {
                final TableCell<SearchHistory, Solution> cell = new TableCell<SearchHistory, Solution>() {
                    @Override
                    public void updateItem(final Solution item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setText("");
                        } else {
                            this.setText(item.getGeneration() + "");
                        }
                    }
                };
                return cell;
            }
        });

        final TableColumn bestTimeCol = new TableColumn();
        bestTimeCol.setText("Time, ms");
        bestTimeCol.setCellValueFactory(new PropertyValueFactory<SearchHistory, String>("bestSolution"));
        bestTimeCol.setCellFactory(new Callback<TableColumn<SearchHistory, Solution>, TableCell<SearchHistory, Solution>>() {
            @Override
            public TableCell<SearchHistory, Solution> call(TableColumn<SearchHistory, Solution> p) {
                final TableCell<SearchHistory, Solution> cell = new TableCell<SearchHistory, Solution>() {
                    @Override
                    public void updateItem(final Solution item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setText("");
                        } else {
                            this.setText(item.getTime() + "");
                        }
                    }
                };
                return cell;
            }
        });

        bestSolutionCol.getColumns().addAll(bestCostCol, bestGenCol, bestTimeCol);
        tableView.getColumns().add(bestSolutionCol);

        progressIndicator.progressProperty().bind(service.progressProperty());
        veil.visibleProperty().bind(service.runningProperty());
        progressIndicator.visibleProperty().bind(service.runningProperty());
        tableView.itemsProperty().bind(service.valueProperty());

        stack.getChildren().addAll(tableView, veil, progressIndicator);

        chartTimeAnt = createChart("The speed of finding a solution dependence of the number of ants", "The speed", "Ant Nr.");
        chartTimeAnt.prefWidthProperty().bind(vBox.prefWidthProperty().subtract(25));

        chartGenTime = createChart("The population number dependence of the speed of finding a solution", "The speed", "Population Nr.");
        chartGenTime.prefWidthProperty().bind(vBox.prefWidthProperty().subtract(25));

        chartFirstBest = new PieChart();
        chartFirstBest.prefWidthProperty().bind(vBox.prefWidthProperty().subtract(25));

        VBox.setMargin(scrollPane, new Insets(10, 0, 0, 0));

        vBox.getChildren().addAll(stack, chartTimeAnt, chartGenTime, chartFirstBest);

        return vBox;
    }

    protected LineChart<Number, Number> createChart(String name, String x, String y) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number, Number> lc = new LineChart<>(xAxis, yAxis);

        lc.setTitle(name);
        xAxis.setLabel(x);
        yAxis.setLabel(y);

        return lc;
    }

    private void clearCharts() {
        chartTimeAnt.getData().clear();
        chartGenTime.getData().clear();
        chartFirstBest.getData().clear();
    }

    public void fillCharts(List<SearchHistory> items) {
        fillChartTimeAnt(items);
        fillChartGenTime(items);
        fillChartFirstBest(items);
    }

    public void fillChartTimeAnt(List<SearchHistory> items) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (SearchHistory historyItem : items) {
            final Solution bestSolution = historyItem.getBestSolution();
            series.getData().add(new XYChart.Data<>(bestSolution.getTime(), historyItem.getAntCount()));
        }

        chartTimeAnt.getData().add(series);
    }

    public void fillChartFirstBest(List<SearchHistory> items) {

        int equals = 0;
        int better = 0;

        for (SearchHistory historyItem : items) {

            final Solution firstSolution = historyItem.getFirstSolution();
            final Solution bestSolution = historyItem.getBestSolution();

            if (firstSolution.getTotalCost() > bestSolution.getTotalCost()) {
                better++;
            } else {
                equals++;
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("First Solution = Best solution", equals),
                new PieChart.Data("First Solution < Best solution", better)
        );

        chartFirstBest.getData().setAll(pieChartData);
    }

    public void fillChartGenTime(List<SearchHistory> items) {
        XYChart.Series<Number, Number> bestSeries = new XYChart.Series<>();
        bestSeries.setName("Best solution");
        XYChart.Series<Number, Number> firstSeries = new XYChart.Series<>();
        firstSeries.setName("First solution");

        for (SearchHistory historyItem : items) {
            final Solution firstSolution = historyItem.getFirstSolution();
            final Solution bestSolution = historyItem.getBestSolution();

            bestSeries.getData().add(new XYChart.Data<>(bestSolution.getTime(), bestSolution.getGeneration()));
            firstSeries.getData().add(new XYChart.Data<>(firstSolution.getTime(), firstSolution.getGeneration()));
        }

        chartGenTime.getData().addAll(firstSeries, bestSeries);
    }

    public void openFile(Consumer<String> consumer) {

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select input file or directory");

        final File file = fileChooser.showOpenDialog(getScene().getWindow());

        if ((file == null)) {
            return;
        }

        clearCharts();

        final String absolutePath = file.getAbsolutePath();
        openedPath = Paths.get(absolutePath);

        consumer.accept(absolutePath);

        try {
            service.restart();
            LOGGER.info("Search history loaded...");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
