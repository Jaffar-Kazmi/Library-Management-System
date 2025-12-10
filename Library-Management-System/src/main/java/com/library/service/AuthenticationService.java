package com.library.service;

import com.library.model.Librarian;
import com.library.model.Reader;
import com.library.model.User;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {

    public User authenticate(String username, String password, String userType){
        String sql = "select username, password, full_name, role from users where username = ? and password = ? and role = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, userType.toUpperCase());

                try {
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        String fullName = rs.getString("full_name");
                        String role = rs.getString("role");

                        if ("LIBRARIAN".equalsIgnoreCase(role)) {
                            return new Librarian(username, password, fullName);
                        } else if ("READER".equalsIgnoreCase(role)) {
                            return new Reader(username, password, fullName);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
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
