package com.library.util;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDbConnection {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Connection OK: " + (conn != null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
