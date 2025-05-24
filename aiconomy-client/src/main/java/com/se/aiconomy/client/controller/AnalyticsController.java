package com.se.aiconomy.client.controller;

import com.se.aiconomy.client.Application.StyleClassFixer;
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

public class AnalyticsController extends BaseController implements Initializable {
    //    @FXML private Hyperlink aiInsightBtn;
    @FXML
    private ScrollPane aiPanel;
    @FXML
    private GridPane analyticsGrid;
    @FXML
    private ColumnConstraints mainCol;
    @FXML
    private ColumnConstraints aiCol;
    @FXML
    private LineChart<String, Number> spendingTrends;
    @FXML
    private PieChart categoryDistribution;
    @FXML
    private NumberAxis yAxis;

//    private boolean isOpenAiPanel = false;

    private void setColumnCount(GridPane grid, int n) {
        grid.getColumnConstraints().clear();  // 清空旧的列配置

        for (int i = 0; i < n; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / n);  // 每列平均分宽度
            grid.getColumnConstraints().add(col);
        }
    }

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
//            setColumnCount(analyticsGrid, 2);
        });
        activetimeline.play();
    }

    @FXML
    public void openAiPanel() {
        if (aiPanel.getContent() != null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ai/ai.fxml"));
            Node view = loader.load();  // 加载 FXML 成为 Node
            AiController controller = loader.getController();
            StyleClassFixer.fixStyleClasses(view);
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
//            setColumnCount(analyticsGrid, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
