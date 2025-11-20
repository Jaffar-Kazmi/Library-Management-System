package com.library.model;

public abstract class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String status; // Add this

    public User(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.status = "ACTIVE"; // default
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; } // Add this
    public void setStatus(String status) { this.status = status; } // Add this

    public abstract String getUserType();
    public abstract String getRole();
}
