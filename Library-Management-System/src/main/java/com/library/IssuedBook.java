package com.library;

import java.time.LocalDate;

public class IssuedBook {

        Book book;
        String issuedTo;
        LocalDate dueDate;

        IssuedBook(Book book, String issuedTo, LocalDate dueDate) {
            this.book = book;
            this.issuedTo = issuedTo;
            this.dueDate = dueDate;
        }

        @Override
        public String toString() {
            return book + " | Issued to " + issuedTo + " | Due Date " + dueDate + " |";

    }
}
