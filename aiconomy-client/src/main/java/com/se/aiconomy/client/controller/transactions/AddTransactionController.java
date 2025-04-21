package com.se.aiconomy.client.controller.transactions;

import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.dto.transaction.request.GetTransactionByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionImportRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Setter
public class AddTransactionController extends BaseController implements Initializable {
    @FXML private VBox categoryPanel;
    @FXML private ComboBox accountCombox;
    @FXML private TextField descriptionInput;
    @FXML private DatePicker datePicker;
    @FXML private TextField amountInput;
    @FXML private Button incomeBtn;
    @FXML private Button expenseBtn;
    @FXML private StackPane rootPane;
    private TransactionRequestHandler handler = new TransactionRequestHandler();
    private boolean isExpense = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (userInfo == null) {
            Platform.runLater(() -> {
                if (userInfo != null) {
                    init();
                } else {
                    System.out.println("User info is not available yet.");
                }
            });
        } else {
            init();
        }
    }

    @FXML
    public void switchToExpense() {
        isExpense = true;
    }

    @FXML
    public void switchToIncome() {
        isExpense = false;
    }

    @FXML
    private void init() {

    }

    private void closeAddTransaction() {
        if (rootPane != null) {
            rootPane.getChildren().removeIf(node ->
                    node != rootPane.getChildren().getFirst() // 保留主页面，移除弹窗和遮罩
            );
        }
    }

    @FXML
    public void handleSaveTransaction(MouseEvent mouseEvent) {
        String description = descriptionInput.getText();
        String amount = amountInput.getText();
        LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.of(0, 0));
        String account = accountCombox.getSelectionModel().getSelectedItem().toString();

        try {
            handler.handleAddTransactionManually(
                    userInfo.getId(),
                    isExpense ? "expense" : "income",
                    amount,
                    dateTime,
                    "Remark Test",
                    "Food & Dining",
                    account
            );
            System.out.println("Transaction import successful.");

        } catch (ServiceException e) {
            e.printStackTrace();
        }

        closeAddTransaction();
    }
}
