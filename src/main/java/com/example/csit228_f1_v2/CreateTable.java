package com.example.csit228_f1_v2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable {
    public static void createTable() {
        try(Connection connection = MySQLConnector.getConnection();
             Statement statement = connection.createStatement()) {
            String query1 = "CREATE TABLE IF NOT EXISTS users (" +
                    "userid INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL," +
                    "password VARCHAR(100) NOT NULL)";
            statement.execute(query1);

            String query2 = "CREATE TABLE IF NOT EXISTS orders (" +
                    "orderid INT AUTO_INCREMENT PRIMARY KEY," +
                    "userid INT," +
                    "FOREIGN KEY (userid) REFERENCES users(userid)," +
                    "variety VARCHAR(50) NOT NULL," +
                    "quantity INT NOT NULL)";
            statement.execute(query2);

            String query3 = "CREATE TABLE IF NOT EXISTS mangoes (" +
                    "mangoid INT AUTO_INCREMENT PRIMARY KEY," +
                    "variety VARCHAR(50) NOT NULL," +
                    "stocks INT)";
            statement.execute(query3);
            System.out.println("Table created successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
