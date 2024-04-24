package com.example.csit228_f1_v2;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.mysql.cj.xdevapi.Table;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class OrderController implements Initializable {
    private String name;
    @FXML
    private TextField tf_id;
    @FXML
    private Button btn_delete_order;
    @FXML
    private Button btn_returnhome;
    @FXML
    private TableView<Order> table_orders;
    @FXML
    private TableColumn tc_orderid;
    @FXML
    private TableColumn tc_variety;
    @FXML
    private TableColumn tc_quantity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btn_returnhome.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeScene("home-view.fxml", "Home", event);
            }
        });

        btn_delete_order.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Warning!");
                alert.setContentText("Do you really want to delete this order?.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK && isNumeric(tf_id.getText())) {
                    try (Connection connection = MySQLConnector.getConnection()) {

                        // Delete Order
                        try (PreparedStatement deleteUserStatement = connection.prepareStatement(
                                "DELETE FROM orders WHERE orderid = ?")) {
                            deleteUserStatement.setInt(1, Integer.parseInt(tf_id.getText()));

                            int rowsAffected = deleteUserStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                table_orders.getItems().clear();
                                tf_id.clear();
                                showTable();
                            } else {
                                System.out.println("Order not found.");
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
                homeController.setUserInformation(name);
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
        name = username;
        showTable();
    }

    private void showTable() {
        try (Connection connection = MySQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT userid FROM users WHERE username=?")) {

            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userid = resultSet.getInt("userid");

                tc_orderid.setCellValueFactory(new PropertyValueFactory<>("orderId"));
                tc_variety.setCellValueFactory(new PropertyValueFactory<>("variety"));
                tc_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT orderid, variety, quantity FROM orders WHERE userid=?");
                preparedStatement.setInt(1, userid);
                ResultSet orderResultSet = preparedStatement.executeQuery();

                while (orderResultSet.next()) {
                    int orderid = orderResultSet.getInt("orderid");
                    String variety = orderResultSet.getString("variety");
                    int quantity = orderResultSet.getInt("quantity");

                    // Add a row to the table using the retrieved data
                    table_orders.getItems().add(new Order(orderid, variety, quantity));
                }
            } else {
                System.out.println("User not found.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
