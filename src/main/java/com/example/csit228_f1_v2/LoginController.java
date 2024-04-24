package com.example.csit228_f1_v2;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField tf_username;
    @FXML
    private PasswordField pf_password;
    @FXML
    private Button btn_login;
    @FXML
    private Button btn_tosignup;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btn_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Check if all credential are all filled in
                if(!tf_username.getText().trim().isEmpty() && !pf_password.getText().isEmpty()) {
                    try(Connection connection = MySQLConnector.getConnection();
                        PreparedStatement preparedStatement = connection.prepareStatement("SELECT password FROM users WHERE username = ?")) {
                        preparedStatement.setString(1, tf_username.getText());
                        ResultSet resultSet = preparedStatement.executeQuery();

                        if(!resultSet.isBeforeFirst()) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Provided credentials are incorrect!");
                            alert.show();
                        } else {
                            while(resultSet.next()) {
                                String retrievedPassword = resultSet.getString("password");
                                if(retrievedPassword.equals(pf_password.getText())) {
                                    changeScene("home-view.fxml", "Home", event);
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setContentText("Password does not match!");
                                    alert.show();
                                }
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please fill in all credentials.");
                    alert.show();
                }
            }
        });
        btn_tosignup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeScene("signup-view.fxml", "Sign up", event);
            }
        });
    }

    private void changeScene(String fxml, String title, Event event) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
            root = loader.load();
            if(fxml.equals("home-view.fxml")) {
                HomeController homeController = loader.getController();
                homeController.setUserInformation(tf_username.getText());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
