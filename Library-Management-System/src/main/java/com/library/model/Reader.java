package com.library.model;

public class Reader extends User {
    public Reader(String username, String password, String fullName) {
        super(username, password, fullName);
    }

    public Reader() {
        super();
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
