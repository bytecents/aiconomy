package com.se.aiconomy.client.controller;

import com.se.aiconomy.client.Application.StyleClassFixer;
import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.ai.AiController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the analytics dashboard, handling chart initialization and AI panel interactions.
 */
public class AnalyticsController extends BaseController implements Initializable {

    /** The panel displaying AI insights. */
    @FXML
    private ScrollPane aiPanel;

    /** The main grid layout for analytics content. */
    @FXML
    private GridPane analyticsGrid;

    /** The main column constraint for layout adjustment. */
    @FXML
    private ColumnConstraints mainCol;

    /** The AI column constraint for layout adjustment. */
    @FXML
    private ColumnConstraints aiCol;

    /** The line chart showing spending trends. */
    @FXML
    private LineChart<String, Number> spendingTrends;

    /** The pie chart showing category distribution. */
    @FXML
    private PieChart categoryDistribution;

    /** The Y axis for the spending trends chart. */
    @FXML
    private NumberAxis yAxis;

    /**
     * Sets the number of columns in the given grid, distributing width evenly.
     *
     * @param grid the grid pane to modify
     * @param n    the number of columns
     */
    private void setColumnCount(GridPane grid, int n) {
        grid.getColumnConstraints().clear();
        for (int i = 0; i < n; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / n);
            grid.getColumnConstraints().add(col);
        }
    }

    /**
     * Initializes the analytics dashboard, setting up charts and layout.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if unknown
     * @param resources the resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainCol.setPercentWidth(100);
        aiCol.setPercentWidth(0);
        setColumnCount(analyticsGrid, 1);

        spendingTrends.setLegendVisible(false);

        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return "$" + object.intValue();
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string.replace("$", ""));
            }
        });

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expenses");
        series.getData().add(new XYChart.Data<>("1", 500));
        series.getData().add(new XYChart.Data<>("5", 800));
        series.getData().add(new XYChart.Data<>("10", 600));
        series.getData().add(new XYChart.Data<>("15", 1200));
        series.getData().add(new XYChart.Data<>("20", 800));
        series.getData().add(new XYChart.Data<>("25", 950));
        series.getData().add(new XYChart.Data<>("30", 700));

        spendingTrends.getData().add(series);

        categoryDistribution.getData().addAll(
                new PieChart.Data("Food & Dining", 35),
                new PieChart.Data("Shopping", 25),
                new PieChart.Data("Transportation", 20),
                new PieChart.Data("Entertainment", 15),
                new PieChart.Data("Others", 5)
        );
    }

    /**
     * Closes the AI panel with an animated transition.
     */
    private void closeAiPanel() {
        if (aiPanel.getContent() == null) {
            return;
        }
        Duration duration = Duration.millis(300);
        Timeline activetimeline = new Timeline(new KeyFrame(duration));
        activetimeline.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            double t = newTime.toMillis() / duration.toMillis();
            double easedProgress;
            if (t < 0.5) {
                easedProgress = 4 * t * t * t;
            } else {
                easedProgress = 1 - Math.pow(-2 * t + 2, 3) / 2;
            }
            double mainColWidth = 60 + 40 * easedProgress;
            double aiColWidth = 40 - 40 * easedProgress;
            mainCol.setPercentWidth(mainColWidth);
            aiCol.setPercentWidth(aiColWidth);
        });
        activetimeline.setOnFinished(event -> {
            aiPanel.setContent(null);
            mainCol.setPercentWidth(100);
            aiCol.setPercentWidth(0);
        });
        activetimeline.play();
    }

    /**
     * Opens the AI panel with an animated transition and loads the AI insight view.
     */
    @FXML
    public void openAiPanel() {
        if (aiPanel.getContent() != null) {
            return;
        }
        try {
            MyFXMLLoader loader = new MyFXMLLoader("/fxml/ai/ai.fxml");
            Node view = loader.load();
            AiController controller = loader.getController();
            aiPanel.setContent(view);

            controller.setUserInfo(userInfo);
            controller.setOnCloseListener(this::closeAiPanel);

            Duration duration = Duration.millis(300);
            Timeline activetimeline = new Timeline(new KeyFrame(duration));
            activetimeline.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                double t = newTime.toMillis() / duration.toMillis();
                double easedProgress;
                if (t < 0.5) {
                    easedProgress = 4 * t * t * t;
                } else {
                    easedProgress = 1 - Math.pow(-2 * t + 2, 3) / 2;
                }
                double mainColWidth = 100 - 40 * easedProgress;
                double aiColWidth = 40 * easedProgress;
                mainCol.setPercentWidth(mainColWidth);
                aiCol.setPercentWidth(aiColWidth);
            });
            activetimeline.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}