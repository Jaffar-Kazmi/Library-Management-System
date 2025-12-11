package com.library.model;

public class Reader extends User {
    public Reader(String username, String password, String fullName, String email) {
        super(username, password, fullName, email);
    }

    @Override
    public String getUserType() {
        return "Reader";
    }

    @Override
    public String getRole() {
        return "READER";
    }
}
