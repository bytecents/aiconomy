package fxml;

public class AddBudget {
    @FXML private StackPane rootPane;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private void onAddBudgetClick() {
        private StackPane rootPane; // ✅ 传入的外层容器
        public void setRootPane(StackPane rootPane) {
            this.rootPane = rootPane;
        }

        @FXML
        private void onCancel(ActionEvent event) {
            closeDialog();
        }

        @FXML
        private void onSave(ActionEvent event) {
            // TODO: 保存逻辑
            closeDialog();
        }

        private void closeDialog() {
            if (rootPane != null) {
                rootPane.getChildren().removeIf(node ->
                        node != rootPane.getChildren().get(0) // 保留主页面，移除弹窗和遮罩
                );
            }
        }
        try {
            // 加载 add_budget.fxml 内容
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_budget.fxml"));
            Parent dialogContent = loader.load();

            // 设置背景遮罩
            Region overlay = new Region();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

            // 居中弹窗容器
            StackPane dialogWrapper = new StackPane(dialogContent);
            dialogWrapper.setMaxWidth(400);
            dialogWrapper.setMaxHeight(600); // height > width
            dialogWrapper.setStyle("-fx-background-color: white; -fx-background-radius: 0; -fx-padding: 20;");

            // 添加遮罩 + 弹窗
            rootPane.getChildren().addAll(overlay, dialogWrapper);

            // 点击遮罩关闭弹窗
            overlay.setOnMouseClicked(e -> {
                rootPane.getChildren().removeAll(overlay, dialogWrapper);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
