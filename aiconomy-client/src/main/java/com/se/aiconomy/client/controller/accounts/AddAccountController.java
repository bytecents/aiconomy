package com.se.aiconomy.client.controller.accounts;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class AddAccountController {

    /*
     * 只有当FXML中有StackPane并设置fx:id="rootPane"时才需要此方法
     * 当前FXML中无此定义，应注释掉
     */
    @FXML
    private StackPane rootPane;

    @FXML
    private void onCancel(ActionEvent event) {
//        // 方案1：直接关闭当前窗口（推荐）
//        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        // 或方案2：如果确实需要操作StackPane（需确保FXML中有对应定义）
        closeDialog(event);
    }

    @FXML
    private void onSave(ActionEvent event) {
        // TODO: 保存逻辑
        closeDialog(event);
    }

    private void closeDialog(ActionEvent event) {
        if (rootPane != null) {
            rootPane.getChildren().removeIf(node ->
                    "dialogPane".equals(node.getId()) // 建议给弹窗Pane设置id
            );
        }
    }

    public void setRootPane(StackPane rootPane) {
    }


}