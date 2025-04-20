package com.se.aiconomy.client.common;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class CustomDialog {
    public static void show(String summary, String detail, String type, String buttonText) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(summary);
        dialog.setContentText(detail);

        // 创建自定义对话框的内容
        HBox contentBox = new HBox();

        // 根据type设置图标和背景颜色
        Text icon = new Text();  // 用于显示图标
        Text content = new Text(detail); // 显示对话框的详细信息

        Color fontColor;

        // 根据 type 设置不同的图标和背景颜色
        switch (type) {
            case "success":
                icon.setText("✔");  // 勾选符号表示成功
                icon.setFill(Color.GREEN);
                dialog.getDialogPane().setStyle("-fx-background-color: #dcfce7; -fx-border-radius: 15px; -fx-padding: 20px;"); // 成功的绿色背景
                fontColor = Color.web("#16a34a");
                break;
            case "error":
                icon.setText("✘");  // 错误符号表示失败
                icon.setFill(Color.RED);
                dialog.getDialogPane().setStyle("-fx-background-color: #fee2e2; -fx-border-radius: 15px; -fx-padding: 20px;"); // 错误的红色背景
                fontColor = Color.web("#dc2626");
                break;
            case "info":
                icon.setText("ℹ️");  // 信息符号
                icon.setFill(Color.BLUE);
                dialog.getDialogPane().setStyle("-fx-background-color: #dbeafe; -fx-border-radius: 15px; -fx-padding: 20px;"); // 信息的蓝色背景
                fontColor = Color.web("#2563eb");
                break;
            default:
                icon.setText("ℹ️");  // 默认图标
                icon.setFill(Color.GRAY);
                dialog.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-border-radius: 15px; -fx-padding: 20px;"); // 默认白色背景
                fontColor = Color.web("#ffffff");
                break;
        }

        // 设置文本字体
        content.setFont(Font.font("System", FontWeight.BOLD, 16));
        content.setFill(fontColor);


        // 将图标和文本添加到对话框内容区域
        contentBox.getChildren().addAll(icon, content);
        contentBox.setSpacing(10);

        dialog.setWidth(400);
        dialog.setHeight(200);
        dialog.setResizable(true);

        // 获取 Stage 并设置样式，隐藏标题栏
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.DECORATED); // 使用没有标题栏的样式


        dialog.getDialogPane().setContent(contentBox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText(buttonText);

        // 显示对话框并等待用户响应
        dialog.showAndWait();
    }
}
