package com.se.aiconomy.client.controller.ai;

import com.se.aiconomy.client.Application.StyleClassFixer;
import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.server.langchain.common.config.Locale;
import com.se.aiconomy.server.langchain.common.model.ChatModelName;
import com.se.aiconomy.server.langchain.service.chat.ChatService;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.se.aiconomy.server.langchain.common.model.ChatModelName.GPT_4_1_MINI;
import static dev.langchain4j.internal.Utils.getOrDefault;

public class AiController implements Initializable {
    @FXML private Button sendBtn;
    @FXML private TextField inputField;
    @FXML private Button[] questionBtns;
    @FXML private FlowPane questionBtnContainer;
    @FXML private VBox messageContent;
    @FXML private VBox insightContent;
    @FXML private ScrollPane messagePanel;

    public void handleInput(InputMethodEvent inputMethodEvent) {
        String input = inputField.getText();
        sendBtn.setDisable(input == null || input.isEmpty());
    }

    public void handleSend(MouseEvent mouseEvent) {
        String input = inputField.getText();
        if (input != null && !input.trim().isEmpty()) {
            sendMessage(input);
            inputField.clear();
        }
    }

    public void handleEnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().getName().equals("Enter")) {
            String input = inputField.getText();
            if (input != null && !input.trim().isEmpty()) {
                sendMessage(input);
                inputField.clear();
            }
        }
    }

    public interface OnCloseListener {
        void onCloseAiPanel();
    }

    ChatService chatService = new ChatService();

    public static class Message {
        public String content;
        public boolean isUserMessage;

        public Message(String content, boolean isUserMessage) {
            this.content = content;
            this.isUserMessage = isUserMessage;
        }
    }

    public ArrayList<Message> messageList = new ArrayList<>();

    private OnCloseListener closeListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        System.out.println(dotenv.get("BASE_URL"));
        // Initialize the AI panel
        questionBtnContainer.getChildren().clear();
        String[] questions = {
                "Budget optimization?",
                "Savings potential?",
                "Weekend spending?"
        };
        for (int i = 0; i < 3; i++) {
            Button questionBtn = new Button(questions[i]);

            questionBtn.getStyleClass().add("p-3-1_5");
            questionBtn.getStyleClass().add("border-none");
            questionBtn.getStyleClass().add("text-xs");
            questionBtn.getStyleClass().add("bg-gray-100");
            questionBtn.getStyleClass().add("text-gray-700");
            questionBtn.getStyleClass().add("rounded-full");
            questionBtn.getStyleClass().add("hover-bg-gray-200");
            questionBtn.getStyleClass().add("cursor-pointer");

            questionBtn.setOnAction(event -> handleQuestionButtonClick(questionBtn));
            questionBtnContainer.getChildren().add(questionBtn);
        }
    }

    public void sendMessage(String content) {
        Message message = new Message(content, true);
        messageList.add(message);
//        String reply = chatService.chat(content, Locale.EN);
        Message aiMessage = new Message("Generating...", false);
        messageList.add(aiMessage);
        chatService.stream(content, Locale.EN,
                partialResponse -> {
                    if (Objects.equals(messageList.getLast().content, "Generating...")) {
                        messageList.getLast().content = "";
                    }
                    messageList.getLast().content += partialResponse;
                    Platform.runLater(() -> {
                        refreshMessageList();
                        messagePanel.setVvalue(1.0);
                    });
                },
                chatResponse -> {
                    messageList.getLast().content = chatResponse.aiMessage().text();
                    Platform.runLater(() -> {
                        refreshMessageList();
                        messagePanel.setVvalue(1.0);
                    });
                },
                throwable -> {
                    System.err.println("Error: " + throwable.getMessage());
                });
        refreshMessageList();
    }

    private void refreshMessageList() {
        messageContent.getChildren().clear();
        messageContent.getStyleClass().add("gap-2_5");
        messageContent.getStyleClass().add("p-3-6-6-6");
        if (!messageList.isEmpty()) {
            insightContent.getChildren().clear();
        }
        for (final Message message : messageList) {
            if (message.isUserMessage) {
                try {
                    MyFXMLLoader loader = new MyFXMLLoader("/fxml/ai/user-message.fxml");
                    Parent root = loader.load();
                    UserMessageController userMessageController = loader.getController();
                    userMessageController.setContent(message.content);

                    messageContent.getChildren().add(root);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ai/ai-message.fxml"));
                    Parent root = loader.load();
                    StyleClassFixer.fixStyleClasses(root);
                    AiMessageController aiMessageController = loader.getController();
                    aiMessageController.setContent(message.content);

                    messageContent.getChildren().add(root);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleQuestionButtonClick(Button questionBtn) {
        // Handle the question button click
        String question = questionBtn.getText().trim();
        sendMessage(question);
        // Add any additional logic for handling the question button click
    }

    @FXML
    public void setOnCloseListener(OnCloseListener listener) {
        this.closeListener = listener;
    }

    @FXML
    public void closeAiPanel() {
        if (closeListener != null) {
            closeListener.onCloseAiPanel();
        }
    }
}
