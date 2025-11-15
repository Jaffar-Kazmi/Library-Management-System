package com.library.model;

public class Librarian extends User {
    public Librarian(String username, String password, String fullName) {
        super(username, password, fullName);
    }

    @Override
    public String getUserType() {
        return "Librarian";
    }
}
