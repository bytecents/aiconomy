package com.se.aiconomy.client.controller.budgets;

import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.model.dto.budget.response.BudgetCategoryInfo;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.text.DecimalFormat;

public class BudgetCategoryCardController extends BaseController {

    @FXML
    @Setter
    @Getter
    StackPane rootPane;
    @FXML
    private Circle circle;
    @FXML
    private ImageView icon;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label budgetLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label percentageLabel;
    @FXML
    private Label statusLabel;
    @Setter
    @Getter
    private BudgetCategoryInfo budgetCategoryInfo;

    @FXML
    private StackPane clickableIcon;
    @Setter
    @Getter
    private BudgetController budgetController;

    private void initPopover() {
        VBox popoverContent = new VBox(10);
        popoverContent.setStyle("""
                    -fx-background-color: rgba(255, 255, 255, 0.95); /* 半透明背景 */
                    -fx-padding: 10;
                    -fx-border-color: #ccc;
                    -fx-border-width: 1;
                    -fx-border-radius: 6;
                    -fx-background-radius: 6;
                """);

        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");

        updateBtn.setStyle("-fx-background-color: #e0f2fe; -fx-padding: 5 10; -fx-cursor: hand;");
        deleteBtn.setStyle("-fx-background-color: #fee2e2; -fx-padding: 5 10; -fx-cursor: hand;");

        popoverContent.getChildren().addAll(updateBtn, deleteBtn);

        Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.getContent().add(popoverContent);

        // 渐入动画
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), popoverContent);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // 渐出动画
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), popoverContent);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        clickableIcon.setOnMouseClicked(e -> {
            if (!popup.isShowing()) {
                popoverContent.setOpacity(0); // 初始化为透明
                popup.show(clickableIcon, e.getScreenX(), e.getScreenY());
                fadeIn.playFromStart(); // 播放渐入动画
            } else {
                fadeOut.setOnFinished(event -> popup.hide()); // 渐出后关闭弹窗
                fadeOut.playFromStart();
            }
        });

        updateBtn.setOnAction(e -> {
            onUpdateButtonClick();
            fadeOut.setOnFinished(event -> popup.hide());
            fadeOut.playFromStart();
        });

        deleteBtn.setOnAction(e -> {
            onDeleteButtonClick();
            System.out.println("Delete clicked");
            fadeOut.setOnFinished(event -> popup.hide());
            fadeOut.playFromStart();
        });
    }

    private void onUpdateButtonClick() {
        try {
            // 加载 add_budget.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/budgets/add_budget.fxml"));
            Parent dialogContent = loader.load();
            // 获取 controller 并传入 rootPane
            BudgetUpdateCardController controller = loader.getController();
            controller.setRootPane(rootPane);
            controller.setBudgetController(budgetController);
            controller.setUserInfo(userInfo);
            // 设置弹窗样式（你可以在 FXML 里设也行）
            dialogContent.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20;");

            // 创建遮罩
            Region overlay = new Region();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
            overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

            // 弹窗容器（居中）
            StackPane dialogWrapper = new StackPane(dialogContent);
            dialogWrapper.setMaxWidth(500);
            dialogWrapper.setMaxHeight(600);

            // 点击遮罩关闭弹窗
            overlay.setOnMouseClicked((MouseEvent e) -> {
                rootPane.getChildren().removeAll(overlay, dialogWrapper);
            });

            // 添加遮罩和弹窗到页面顶层
            rootPane.getChildren().addAll(overlay, dialogWrapper);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onDeleteButtonClick() {

    }

    @FXML
    private void initialize() {
        initPopover();
    }

    public void setCardData(String category, String budget, String imagePath, String colorHex,
                            double progress, String percentageText, String statusText) {
        categoryLabel.setText(category);
        budgetLabel.setText(budget);
        icon.setImage(new Image(imagePath));
        circle.setFill(Color.web(colorHex));
        adjustProgressBar(progressBar, progress);
//        progressBar.getStyleClass().add(progressBarStyleClass);
        percentageLabel.setText(percentageText);
        adjustRatioText(percentageLabel, progress);
        statusLabel.setText(statusText);
    }


    private void adjustRatioText(Label label, double ratio) {
        if (label != null) {
            String redTextClass = "text-red-500";
            String greenTextClass = "text-green-500";
            String yellowTextClass = "text-yellow-500";
            String targetTextClass;
            DecimalFormat decimalFormat = new DecimalFormat("##.##%");
            String percentage = decimalFormat.format(ratio);
            if (0 <= ratio && ratio < 0.33) {
                targetTextClass = greenTextClass;
            } else if (ratio >= 0.33 && ratio < 0.67) {
                targetTextClass = yellowTextClass;
            } else if (ratio >= 0.67) {
                targetTextClass = redTextClass;
            } else {
                targetTextClass = redTextClass;
            }
            ObservableList<String> styleClasses = label.getStyleClass();
            styleClasses.removeAll(redTextClass, greenTextClass, yellowTextClass);
            if (!styleClasses.contains(targetTextClass)) {
                styleClasses.add(targetTextClass);
            }
        }
    }

    private void adjustProgressBar(ProgressBar progressBar, double ratio) {
        if (progressBar != null) {
            String redClass = "red-bar";
            String greenClass = "green-bar";
            String yellowClass = "yellow-bar";

            String targetClass;
            if (0 <= ratio && ratio < 0.33) {
                targetClass = greenClass;
            } else if (ratio >= 0.33 && ratio < 0.67) {
                targetClass = yellowClass;
            } else if (ratio >= 0.67) {
                targetClass = redClass;
            } else {
                targetClass = redClass;
            }

            ObservableList<String> styleClasses = progressBar.getStyleClass();
            styleClasses.removeAll(redClass, greenClass, yellowClass);
            if (!styleClasses.contains(targetClass)) {
                styleClasses.add(targetClass);
            }
            double progress = ratio;
            if (ratio < 0) progress = 0;
            if (ratio > 1) progress = 1;
            progressBar.setProgress(progress);
        }
    }
}