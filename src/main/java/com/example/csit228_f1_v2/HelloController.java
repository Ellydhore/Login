package com.example.csit228_f1_v2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HelloController {
    public GridPane pnLogin;
    public AnchorPane pnMain;
    public VBox pnHome;
    @FXML
    private TextField tf_user;
    @FXML
    private TextField tf_password;
    @FXML
    private Button btn_login;
    @FXML
    private Label welcomeText;

}