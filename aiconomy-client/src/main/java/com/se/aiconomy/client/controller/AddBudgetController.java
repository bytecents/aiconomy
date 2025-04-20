package com.se.aiconomy.client.controller;

//import com.alibaba.fastjson2.internal.asm.Label;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Setter;
import javafx.scene.control.Label;


import java.io.IOException;
import java.util.List;

@Setter
public class AddBudgetController extends BaseController {
    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private StackPane rootPane; // 这个是 main.fxml 的最外层 StackPane

    @FXML
    private VBox vbox1;
    @FXML
    private VBox vbox2;
    @FXML
    private VBox vbox3;
    @FXML
    private VBox vbox4;
    @FXML
    private VBox vbox5;
    @FXML
    private VBox vbox6;
    @FXML
    private VBox vbox7;
    @FXML
    private VBox vbox8;
    @FXML
    private VBox vboxMonthly, vboxWeekly, vboxYearly;

    @FXML
    public void onAddBudgetClick(ActionEvent event) {
        try {
            // 加载 add_budget.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_budget.fxml"));
            Parent dialogContent = loader.load();
            // 获取 controller 并传入 rootPane
            AddBudgetController controller = loader.getController();
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

    @FXML
    private void onCancel(ActionEvent event) {
        closeDialog(event);
    }

    @FXML
    private void onSave(ActionEvent event) {
        // TODO: 保存逻辑
        closeDialog(event);
    }

    @FXML
    private void handleClick(MouseEvent event) {
        resetAllBoxes();  // Reset all boxes to default styles

        VBox clickedVBox = (VBox) event.getSource();

        // Set the styles for the clicked box (background, border, etc.)
        clickedVBox.setStyle("-fx-background-color: #eff6ff; -fx-border-color: #2563eb; -fx-border-width: 1; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Iterate through the children of the clicked VBox to update the Circle and Image styles
        for (Node node : clickedVBox.getChildren()) {
            if (node instanceof StackPane stackPane) {
                for (Node child : stackPane.getChildren()) {
                    if (child instanceof Circle circle) {
                        circle.setFill(Color.web("#bfdbfe"));  // Set the circle color to blue
                    } else if (child instanceof ImageView imageView) {
                        String oldUrl = imageView.getImage().getUrl();
                        if (oldUrl.contains(".png")) {
                            String blueUrl = oldUrl.replace(".png", "_blue.png");
                            imageView.setImage(new Image(blueUrl));  // Change the image to the "_blue" version
                        }
                    }
                }
            }
        }

        // Update the label's style for the clicked box (bold and blue text)
        for (Node node : clickedVBox.getChildren()) {
            if (node instanceof Label label) {
                label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2563eb;"); // Set bold and blue for selected box
            }
        }
    }

    private void resetAllBoxes() {
        List<VBox> allBoxes = List.of(
                vbox1, vbox2, vbox3, vbox4, vbox5, vbox6, vbox7, vbox8
        );

        // Reset all boxes to the default style
        for (VBox box : allBoxes) {
            box.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15;");

            // Reset the styles of the label (only reset color and font-weight)
            for (Node node : box.getChildren()) {
                if (node instanceof Label label) {
                    label.setStyle("-fx-font-size: 12px; -fx-font-weight: normal; -fx-text-fill: #6b7280;"); // Default label style
                }
            }

            // Reset Circle and Image colors for all boxes
            for (Node node : box.getChildren()) {
                if (node instanceof StackPane stackPane) {
                    for (Node child : stackPane.getChildren()) {
                        if (child instanceof Circle circle) {
                            circle.setFill(Color.web("#f3f4f6"));  // Default circle color (gray-100)
                        } else if (child instanceof ImageView imageView) {
                            String oldUrl = imageView.getImage().getUrl();
                            if (oldUrl.contains("_blue.png")) {
                                String originalUrl = oldUrl.replace("_blue.png", ".png");
                                imageView.setImage(new Image(originalUrl));  // Restore original image
                            }
                        }
                    }
                }
            }
        }
    }


    private void closeDialog(ActionEvent event) {
        if (rootPane != null) {
            rootPane.getChildren().removeIf(node ->
                    node != rootPane.getChildren().get(0) // 保留主页面，移除弹窗和遮罩
            );
        }
    }

    public ToggleGroup getToggleGroup() {
        return toggleGroup;
    }
}