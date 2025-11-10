package com.library.service;

import com.library.model.Librarian;
import com.library.model.Reader;
import com.library.model.User;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {
    private static Map<String, User> users = new HashMap<>();

    static {
        users.put("admin", new Librarian("admin", "admin@123", "Admin Librarian"));
        users.put("reader1", new Reader("reader", "reader@123", "Jaffar Raza"));
    }

    public User authenticate(String username, String password, String userType){
        User user = users.get(username);

        if(user == null) {
            return null;
        }

        if(!user.getPassword().equals(password)){
            return null;
        }

        // Verify user type matches
        if (userType.equalsIgnoreCase("Librarian") && !(user instanceof Librarian)) {
            return null;
        }

        if (userType.equalsIgnoreCase("Reader") && !(user instanceof Reader)) {
            return null;
        }

        return user;
    }

    public boolean validateInput(String username, String password) {
        return username != null && !username.trim().isEmpty() &&
                password != null && !password.trim().isEmpty();
    }
}
