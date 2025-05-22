package com.se.aiconomy.client.controller;

import com.se.aiconomy.server.model.dto.budget.response.BudgetCategoryInfo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class AccountsController extends BaseController {


    @FXML
    public void initialize() {
        if (userInfo == null) {
            Platform.runLater(() -> {
                // 延迟到事件调度线程中处理
                if (userInfo != null) {
                    init();
                }
            });
        } else {
            init();
        }
    }
    private BudgetCategoryInfo info;
    @FXML
    private StackPane rootPane; // 这个是 main.fxml 的最外层 StackPane

    @FXML
    public void onAddAccountClick(ActionEvent event) {
        try {
            // 加载 add_budget.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_accounts.fxml"));
            Parent dialogContent = loader.load();
            // 获取 controller 并传入 rootPane
            AddAccountController controller = loader.getController();
            controller.setRootPane(rootPane); // ⚠️这里的 rootPane 是你的页面最外层 StackPane

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

    private void init() {

    }
}
