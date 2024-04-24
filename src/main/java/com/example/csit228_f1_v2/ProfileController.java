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
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    @FXML
    private PasswordField pf_password;
    @FXML
    private Label lb_name;
    @FXML
    private Button btn_returnhome;
    @FXML
    private Button btn_update;
    @FXML
    private Button btn_deleteaccount;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btn_update.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Check if all credential are all filled in
                if(!pf_password.getText().isEmpty()) {
                    try(Connection connection = MySQLConnector.getConnection();
                        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET password=? WHERE username=?")) {
                        preparedStatement.setString(1, pf_password.getText());
                        preparedStatement.setString(2, lb_name.getText());
                        int rowsUpdated = preparedStatement.executeUpdate();

                        if (rowsUpdated > 0) {
                            pf_password.clear();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Success!");
                            alert.setContentText("Password successfully changed!.");
                            alert.show();
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Enter new password!");
                    alert.show();
                }
            }
        });
        btn_returnhome.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeScene("home-view.fxml", "Home", event);
            }
        });

        btn_deleteaccount.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Warning!");
                alert.setContentText("Do you really want to delete this account?.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try (Connection connection = MySQLConnector.getConnection()) {
                        // Delete associated orders
                        try (PreparedStatement deleteOrdersStatement = connection.prepareStatement(
                                "DELETE FROM orders WHERE userid = (SELECT userid FROM users WHERE username = ?)")) {
                            deleteOrdersStatement.setString(1, lb_name.getText());
                            deleteOrdersStatement.executeUpdate();
                        }

                        // Delete user account
                        try (PreparedStatement deleteUserStatement = connection.prepareStatement(
                                "DELETE FROM users WHERE username = ?")) {
                            deleteUserStatement.setString(1, lb_name.getText());
                            int rowsDeleted = deleteUserStatement.executeUpdate();
                            if (rowsDeleted > 0) {
                                changeScene("signup-view.fxml", "Sign up", event);
                            } else {
                                System.out.println("User not found.");
                            }
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
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
                homeController.setUserInformation(lb_name.getText());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setUserInformation(String username) {
        lb_name.setText(username);
    }
}
