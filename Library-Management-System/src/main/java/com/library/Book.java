package com.library;

import java.time.LocalDate;

public class Book {
    String isbn, name, author;
    LocalDate publishDate;

    public Book() {}

    Book(String isbn, String name, String author, LocalDate publishDate) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.publishDate = publishDate;
    }

    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    public String getISBN() {
        return isbn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    @Override
    public String toString() {
        return "| ISBN : " + isbn + " | Name : " + name + " | Author : " + author + " | Publish Date : " + publishDate + " |";
    }
}
