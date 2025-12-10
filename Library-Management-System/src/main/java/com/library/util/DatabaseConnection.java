package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        String url = DatabaseConfig.getUrl();
        String user = DatabaseConfig.getUsername();
        String pass = DatabaseConfig.getPassword();

        return DriverManager.getConnection(url, user, pass);
    }
}
