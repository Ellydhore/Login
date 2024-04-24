package com.example.csit228_f1_v2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        /*
            FXML
        */
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("Sign up");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        /*
            [See MySQLConnector class]
            Generate table
        */
        CreateTable.createTable();
        MangoTable.addMangoVariety();
        MangoTable.restockMango();
    }

    public static void main(String[] args) {
        launch();
    }
}