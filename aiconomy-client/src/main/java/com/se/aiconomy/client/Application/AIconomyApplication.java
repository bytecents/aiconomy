package com.se.aiconomy.client.Application;

//import atlantafx.base.theme.PrimerDark;

import atlantafx.base.theme.PrimerLight;
import com.se.aiconomy.client.common.MyFXMLLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AIconomyApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    /**
     * Entry point for the JavaFX application
     *
     * @param stage The main window
     * @throws IOException If loading the FXML file fails
     */
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
//        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet()); // TODO: dark mode
        Parent root = new MyFXMLLoader("/fxml/login.fxml").load();

        Scene scene = new Scene(root, 1200, 820);
        stage.setTitle("AIconomy");
        stage.setScene(scene);
        stage.show();
    }
}

