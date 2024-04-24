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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private Label lb_name;
    @FXML
    private Label lb_quantity;
    @FXML
    private Button btn_myorders;
    @FXML
    private Button btn_profile;
    @FXML
    private Button btn_decrease;
    @FXML
    private Button btn_increase;
    @FXML
    private Button btn_min;
    @FXML
    private Button btn_max;
    @FXML
    private Button btn_logout;
    @FXML
    private Button btn_makeorder;
    @FXML
    private RadioButton rbtn_1, rbtn_2, rbtn_3, rbtn_4, rbtn_5, rbtn_6;
    private String mango_variety;
    private int mango_quantity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mango_quantity = 0;
        btn_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeScene("login-view.fxml", "Log in", event);
            }
        });

        btn_increase.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(mango_quantity < 100) {
                    mango_quantity = Integer.parseInt(lb_quantity.getText()) + 1;
                }
                lb_quantity.setText(String.valueOf(mango_quantity));
            }
        });

        btn_decrease.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(mango_quantity > 0) {
                    mango_quantity = Integer.parseInt(lb_quantity.getText()) - 1;
                }
                lb_quantity.setText(String.valueOf(mango_quantity));
            }
        });

        btn_max.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mango_quantity = 100;
                lb_quantity.setText(String.valueOf(mango_quantity));
            }
        });

        btn_min.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mango_quantity = 0;
                lb_quantity.setText(String.valueOf(mango_quantity));
            }
        });

        btn_makeorder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try(Connection connection = MySQLConnector.getConnection()) {
                    connection.setAutoCommit(false); // Start a transaction

                    try (PreparedStatement selectStatement = connection.prepareStatement(
                            "SELECT userid FROM users WHERE username = ?");
                         PreparedStatement updateStockStatement = connection.prepareStatement(
                                 "UPDATE mangoes SET stocks = stocks - ? WHERE variety = ? AND stocks >= ?");
                         PreparedStatement insertStatement = connection.prepareStatement(
                                 "INSERT INTO orders (userid, variety, quantity) VALUES (?, ?, ?)")) {

                        String username = lb_name.getText();
                        selectStatement.setString(1, username);
                        ResultSet resultSet = selectStatement.executeQuery();

                        int userid;
                        if (resultSet.next()) {
                            userid = resultSet.getInt("userid");
                        } else {
                            System.out.println("User not found.");
                            return; // Exit the method if user not found
                        }

                        String mango_variety = "";
                        if(rbtn_1.isSelected()) {
                            mango_variety = rbtn_1.getText();
                        } else if(rbtn_2.isSelected()) {
                            mango_variety = rbtn_2.getText();
                        } else if(rbtn_3.isSelected()) {
                            mango_variety = rbtn_3.getText();
                        } else if(rbtn_4.isSelected()) {
                            mango_variety = rbtn_4.getText();
                        } else if(rbtn_5.isSelected()) {
                            mango_variety = rbtn_5.getText();
                        } else if(rbtn_6.isSelected()) {
                            mango_variety = rbtn_6.getText();
                        }

                        int mango_quantity = Integer.parseInt(lb_quantity.getText());

                        // Check if quantity exceeds available stocks
                        updateStockStatement.setInt(1, mango_quantity);
                        updateStockStatement.setString(2, mango_variety);
                        updateStockStatement.setInt(3, mango_quantity);
                        int rowsUpdated = updateStockStatement.executeUpdate();

                        if (rowsUpdated == 1) {
                            // Stock updated successfully, proceed with the order
                            insertStatement.setInt(1, userid);
                            insertStatement.setString(2, mango_variety);
                            insertStatement.setInt(3, mango_quantity);
                            insertStatement.executeUpdate();
                            connection.commit(); // Commit the transaction
                            System.out.println("Order inserted successfully.");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Success!");
                            alert.setContentText("Order inserted successfully.");
                            alert.show();
                        } else {
                            // Quantity exceeds available stocks, rollback the transaction
                            connection.rollback();
                            System.out.println("Quantity exceeds available stocks. Order canceled.");
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Error!");
                            alert.setContentText("Quantity exceeds available stocks. Order canceled.");
                            alert.show();
                        }

                    } catch (SQLException e) {
                        connection.rollback(); // Rollback the transaction on any SQL exception
                        throw new RuntimeException(e);
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btn_profile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeScene("profile-view.fxml", "My Profile", event);
            }
        });

        btn_myorders.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeScene("order-view.fxml", "My Orders", event);
            }
        });
    }

    private void changeScene(String fxml, String title, Event event) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
            root = loader.load();
            if(fxml.equals("profile-view.fxml")) {
                ProfileController profileController = loader.getController();
                profileController.setUserInformation(lb_name.getText());
            } else if(fxml.equals("order-view.fxml")) {
                OrderController orderController = loader.getController();
                orderController.setUserInformation(lb_name.getText());
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