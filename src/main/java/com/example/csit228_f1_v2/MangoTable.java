package com.example.csit228_f1_v2;

import java.sql.*;

public class MangoTable {
    public static void addMangoVariety() {
        try (Connection connection = MySQLConnector.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(
                     "SELECT variety FROM mangoes WHERE variety = ?");
             PreparedStatement insertStatement = connection.prepareStatement(
                     "INSERT INTO mangoes (variety, stocks) VALUES (?, ?)")) {

            String[] varieties = {"Kalabaw", "Katchamita", "Apple Mango", "Pico Mango", "Francis", "Miyazaki"};

            for (String variety : varieties) {
                selectStatement.setString(1, variety);
                ResultSet resultSet = selectStatement.executeQuery();

                if (!resultSet.next()) {
                    insertStatement.setString(1, variety);
                    insertStatement.setInt(2, 0);
                    insertStatement.executeUpdate();
                    System.out.println("Variety '" + variety + "' inserted successfully.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void restockMango() {
        try (Connection connection = MySQLConnector.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(
                     "UPDATE mangoes SET stocks = ? WHERE variety = ?")) {

            String[] varieties = {"Kalabaw", "Katchamita", "Apple Mango", "Pico Mango", "Francis", "Miyazaki"};
            int[] stocks = {25, 30, 50, 60, 25, 10};

            for (int i = 0; i < varieties.length; i++) {
                updateStatement.setInt(1, stocks[i]);
                updateStatement.setString(2, varieties[i]);
                updateStatement.executeUpdate();
                System.out.println("Restocked '" + varieties[i] + "' successfully.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
