package com.library.service;

import com.library.model.Librarian;
import com.library.model.Reader;
import com.library.model.User;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationService {

    public User authenticate(String username, String password, String userType){
        String sql = "SELECT id, username, password, full_name, role " +
                "FROM users WHERE username = ? AND password = ? AND role = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, userType.toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String fullName = rs.getString("full_name");
                    String role = rs.getString("role");

                    User user;
                    if ("LIBRARIAN".equalsIgnoreCase(role)) {
                        user = new Librarian(username, password, fullName);
                    } else if ("READER".equalsIgnoreCase(role)) {
                        user = new Reader(username, password, fullName);
                    } else {
                        return null;
                    }

                    user.setId(userId);     // only this is needed
                    return user;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateInput(String username, String password) {
        return username != null && !username.trim().isEmpty() &&
                password != null && !password.trim().isEmpty();
    }
}
