package com.library.model;

public class Librarian extends User {
    public Librarian(String username, String password, String fullName, String email) {
        super(username, password, fullName, email);
    }

    @Override
    public String getUserType() {
        return "Librarian";
    }

    @Override
    public String getRole() {
        return "LIBRARIAN";
    }
}
