package com.se.aiconomy.client.controller.ai;

import com.se.aiconomy.client.Application.StyleClassFixer;
import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.langchain.common.config.Locale;
import com.se.aiconomy.server.langchain.service.chat.ChatService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for the AI chat panel.
 * Handles user input, message sending, and UI updates for the AI assistant.
 */
public class AiController extends BaseController implements Initializable {

    /**
     * List of all messages in the chat.
     */
    public ArrayList<Message> messageList = new ArrayList<>();

    /**
     * Service for handling chat interactions with the AI.
     */
    ChatService chatService = new ChatService();

    /**
     * Send button in the chat panel.
     */
    @FXML
    private Button sendBtn;

    /**
     * Input field for user messages.
     */
    @FXML
    private TextField inputField;

    /**
     * Container for quick question buttons.
     */
    @FXML
    private FlowPane questionBtnContainer;

    /**
     * VBox containing all chat messages.
     */
    @FXML
    private VBox messageContent;

    /**
     * VBox for displaying AI insights.
     */
    @FXML
    private VBox insightContent;

    /**
     * ScrollPane for the chat message area.
     */
    @FXML
    private ScrollPane messagePanel;

    /**
     * Indicates if the AI is currently generating a response.
     */
    private boolean isGenerating = false;

    /**
     * Listener for closing the AI panel.
     */
    @Setter
    private OnCloseListener onCloseListener;

    /**
     * Handles the send button click event.
     *
     * @param mouseEvent the mouse event triggered by clicking the send button
     */
    public void handleSend(MouseEvent mouseEvent) {
        String input = inputField.getText();
        if (input != null && !input.trim().isEmpty()) {
            sendMessage(input);
            inputField.clear();
        }
    }

    /**
     * Updates the disabled state of the send button based on input and AI state.
     */
    public void updateSendDisable() {
        String input = inputField.getText();
        sendBtn.setDisable(isGenerating || input == null || input.isEmpty());
    }

    /**
     * Handles the Enter key press event in the input field.
     *
     * @param keyEvent the key event triggered by pressing a key
     */
    public void handleEnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().getName().equals("Enter")) {
            String input = inputField.getText();
            if (input != null && !input.trim().isEmpty() && !isGenerating) {
                sendMessage(input);
                inputField.clear();
            }
        }
    }

    /**
     * Initializes the controller and UI components.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if unknown
     * @param resources the resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inputField.textProperty().addListener((obs, oldText, newText) -> {
            updateSendDisable();
        });
        updateSendDisable();

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

    /**
     * Sends a user message to the AI and updates the chat UI.
     *
     * @param content the message content to send
     */
    public void sendMessage(String content) {
        Message message = new Message(content, true);
        messageList.add(message);
        isGenerating = true;
        updateSendDisable();
        Message aiMessage = new Message("Generating...", false);
        messageList.add(aiMessage);
        chatService.setUserId(this.getUserInfo().getId());
        chatService.stream(content, Locale.EN,
                partialResponse -> {
                    if (Objects.equals(messageList.getLast().content, "Generating...")) {
                        messageList.getLast().content = "";
                    }
                    messageList.getLast().content += partialResponse;
                    Platform.runLater(() -> {
                        refreshMessageList();
                        Platform.runLater(() -> messagePanel.setVvalue(1.0));
                    });
                },
                chatResponse -> {
                    messageList.getLast().content = chatResponse.aiMessage().text();
                    isGenerating = false;
                    updateSendDisable();
                    Platform.runLater(() -> {
                        refreshMessageList();
                        Platform.runLater(() -> messagePanel.setVvalue(1.0));
                    });

                },
                throwable -> {
                    messageList.getLast().content = throwable.getMessage();
                    isGenerating = false;
                    updateSendDisable();
                    Platform.runLater(() -> {
                        refreshMessageList();
                        Platform.runLater(() -> messagePanel.setVvalue(1.0));
                    });
                });
        refreshMessageList();
    }

    /**
     * Refreshes the chat message list in the UI.
     */
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
                } catch (Exception e) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handles the click event for a quick question button.
     *
     * @param questionBtn the button that was clicked
     */
    private void handleQuestionButtonClick(Button questionBtn) {
        String question = questionBtn.getText().trim();
        sendMessage(question);
        // Additional logic for handling the question button click can be added here
    }

    /**
     * Closes the AI panel.
     */
    @FXML
    public void closeAiPanel() {
        if (onCloseListener != null) {
            onCloseListener.onCloseAiPanel();
        }
    }

    /**
     * Listener interface for closing the AI panel.
     */
    public interface OnCloseListener {
        /**
         * Called when the AI panel should be closed.
         */
        void onCloseAiPanel();
    }

    /**
     * Represents a chat message.
     */
    public static class Message {
        /**
         * The content of the message.
         */
        public String content;

        /**
         * Whether this message is from the user.
         */
        public boolean isUserMessage;

        /**
         * Constructs a new Message.
         *
         * @param content        the message content
         * @param isUserMessage  true if the message is from the user, false if from AI
         */
        public Message(String content, boolean isUserMessage) {
            this.content = content;
            this.isUserMessage = isUserMessage;
        }
    }
}