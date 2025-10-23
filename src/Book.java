package src;

import java.time.LocalDate;

public class Book {
    String isbn, name, author;
    LocalDate publishDate;

    Book(String isbn, String name, String author, LocalDate publishDate) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.publishDate = publishDate;
    }

    void setISBN(String isbn) {
        this.isbn = isbn;
    }

    String getISBN() {
        return isbn;
    }

    void setName(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    String getAuthor() {
        return author;
    }

    void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    LocalDate getPublishDate() {
        return publishDate;
    }

    @Override
    public String toString() {
        return "| ISBN : " + isbn + " | Name : " + name + " | Author : " + author + " | Publish Date : " + publishDate + " |";
    }
}
