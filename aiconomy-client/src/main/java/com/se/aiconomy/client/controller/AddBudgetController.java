package com.se.aiconomy.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Setter;

import java.util.List;

import com.se.aiconomy.server.handler.BudgetRequestHandler;
import com.se.aiconomy.server.model.dto.budget.request.BudgetAddRequest;
import com.se.aiconomy.server.model.dto.budget.response.BudgetInfo;

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
    private TextField budgetAmountInput;
    @FXML
    private TextField additionalNotesInput;
    @FXML
    private Label FoodAndDining;
    @FXML
    private Label Transportation;
    @FXML
    private Label Shopping;
    @FXML
    private Label Housing;
    @FXML
    private Label Education;
    @FXML
    private Label Travel;
    @FXML
    private Label Gifts;
    @FXML
    private Label Custom;
    @FXML
    private RadioButton six;
    @FXML
    private RadioButton eight;
    @FXML
    private RadioButton ten;
    private Label selectedLabel = null;
    private AddBudgetController.OnOpenListener openListener;

    @FXML
    public void setOnOpenListener(AddBudgetController.OnOpenListener listener) {
        this.openListener = listener;
    }

    @FXML
    public void onAddBudgetClick(ActionEvent event) {
        if (openListener != null) {
            openListener.onOpenAddBudgetPanel();
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        closeDialog(event);
    }

    @FXML
    private void onSave(ActionEvent event) {
        // TODO: 保存逻辑
        String budgetCategory = selectedLabel.getText();
        String notes = additionalNotesInput.getText();
        double budgetAmount = Double.parseDouble(budgetAmountInput.getText());
        double alertSettings = 1;
        if (six.isSelected()) {
            alertSettings = 0.6;
        } else if (eight.isSelected()) {
            alertSettings = 0.8;
        } else if (ten.isSelected()) {
            alertSettings = 1;
        } 
        BudgetAddRequest request = new BudgetAddRequest();
        // request.setUserId(userInfo.getId());
        request.setBudgetAmount(budgetAmount);
        request.setBudgetCategory(budgetCategory);
        request.setAlertSettings(alertSettings);
        request.setNotes(notes);
        BudgetRequestHandler handler = new BudgetRequestHandler();
        try {
            BudgetInfo info = handler.handleBudgetAddRequest(request);
            System.out.println("添加预算成功：" + info.getBudgetCategory());
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeDialog(event);
    }

    @FXML
    private void handleClick(MouseEvent event) {
        resetAllBoxes(); // Reset all boxes to default styles

        VBox clickedVBox = (VBox) event.getSource();

        // Set the styles for the clicked box (background, border, etc.)
        clickedVBox.setStyle(
                "-fx-background-color: #eff6ff; -fx-border-color: #2563eb; -fx-border-width: 1; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Iterate through the children of the clicked VBox to update the Circle and
        // Image styles
        for (Node node : clickedVBox.getChildren()) {
            if (node instanceof StackPane stackPane) {
                for (Node child : stackPane.getChildren()) {
                    if (child instanceof Circle circle) {
                        circle.setFill(Color.web("#bfdbfe")); // Set the circle color to blue
                    } else if (child instanceof ImageView imageView) {
                        String oldUrl = imageView.getImage().getUrl();
                        if (oldUrl.contains(".png")) {
                            String blueUrl = oldUrl.replace(".png", "_blue.png");
                            imageView.setImage(new Image(blueUrl)); // Change the image to the "_blue" version
                        }
                    }
                }
            }
        }

        // Update the label's style for the clicked box (bold and blue text)
        for (Node node : clickedVBox.getChildren()) {
            if (node instanceof Label label) {
                label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2563eb;"); // Set bold and
                                                                                                       // blue for
                                                                                                       // selected box
            }
        }

        // Update Label's style for clicked box
        for (Node node : clickedVBox.getChildren()) {
            if (node instanceof Label label) {
                label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

                // Save the selected label
                selectedLabel = label;
            }
        }
    }

    private void resetAllBoxes() {
        List<VBox> allBoxes = List.of(
                vbox1, vbox2, vbox3, vbox4, vbox5, vbox6, vbox7, vbox8);

        // Reset all boxes to the default style
        for (VBox box : allBoxes) {
            box.setStyle(
                    "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15;");

            // Reset the styles of the label (only reset color and font-weight)
            for (Node node : box.getChildren()) {
                if (node instanceof Label label) {
                    label.setStyle("-fx-font-size: 12px; -fx-font-weight: normal; -fx-text-fill: #6b7280;"); // Default
                                                                                                             // label
                                                                                                             // style
                }
            }

            // Reset Circle and Image colors for all boxes
            for (Node node : box.getChildren()) {
                if (node instanceof StackPane stackPane) {
                    for (Node child : stackPane.getChildren()) {
                        if (child instanceof Circle circle) {
                            circle.setFill(Color.web("#f3f4f6")); // Default circle color (gray-100)
                        } else if (child instanceof ImageView imageView) {
                            String oldUrl = imageView.getImage().getUrl();
                            if (oldUrl.contains("_blue.png")) {
                                String originalUrl = oldUrl.replace("_blue.png", ".png");
                                imageView.setImage(new Image(originalUrl)); // Restore original image
                            }
                        }
                    }
                }
            }
        }
    }

    private void closeDialog(ActionEvent event) {
        if (rootPane != null) {
            rootPane.getChildren().removeIf(node -> node != rootPane.getChildren().get(0) // 保留主页面，移除弹窗和遮罩
            );
        }
    }

    public ToggleGroup getToggleGroup() {
        return toggleGroup;
    }

    public interface OnOpenListener {
        void onOpenAddBudgetPanel();
    }
}