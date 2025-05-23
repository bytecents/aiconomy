package com.se.aiconomy.client.controller.accounts;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import lombok.Setter;

public class AddAccountController {

    public Button saveButton;
    @FXML
    private ComboBox<String> bankComboBox;

    @FXML
    private ComboBox<String> typeComboBox;


    @FXML
    public void initialize() {
        bankComboBox.setItems(FXCollections.observableArrayList("Chase", "Bank of America", "Wells Fargo", "Citibank"));
        typeComboBox.setItems(FXCollections.observableArrayList("Checking", "Saving", "Credit", "Investment"));
////        // 可选：设置默认值
//        bankComboBox.setValue("USD");
//        typeComboBox.setValue("English");
    }
    /*
     * 只有当FXML中有StackPane并设置fx:id="rootPane"时才需要此方法
     * 当前FXML中无此定义，应注释掉
     */
    @Setter
    @FXML
    private StackPane rootPane;

    @FXML
    private void onCancel(ActionEvent event) {
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
                    node != rootPane.getChildren().get(0) // 保留主页面，移除弹窗和遮罩
            );
        }
    }


}