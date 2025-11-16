package com.library.service;

import com.library.model.Book;
import com.library.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookService {

    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "select id, isbn, title, author, category, publisher, published_date, total_copies, available_copies, created_at from books";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = mapRowToBook(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> findAvailable() {
        List<Book> books = new ArrayList<>();
        String sql = "select id, isbn, title, author, category, publisher, published_date, total_copies, available_copies, created_at from books where available_copies > 0";


        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = mapRowToBook(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public Book findByISBN(String isbn) {
        String sql = "select id, isbn, title, author, category, publisher, published_date, total_copies, available_copies, created_at from books where isbn = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, isbn);

            try {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean add(Book book) {
        String sql = "INSERT INTO books " +
                "(isbn, title, author, publisher, published_date, category, total_copies, available_copies) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getPublisher());
            if (book.getPublicationDate() != null) {
                ps.setDate(5, Date.valueOf(book.getPublicationDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setString(6, book.getCategory());
            ps.setInt(7, book.getTotalCopies());
            ps.setInt(8, book.getAvailableCopies());

            int affected = ps.executeUpdate();
            if (affected == 0) return false;

            try {
                ResultSet keys = ps.getGeneratedKeys();
                    if (keys.next()) {
                        book.setBookId(keys.getInt(1));
                    }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Book book) {
        String sql = "UPDATE books SET " +
                "isbn = ?, title = ?, author = ?, publisher = ?, " +
                "published_date = ?, category = ?, total_copies = ?, available_copies = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getPublisher());
            if (book.getPublicationDate() != null) {
                ps.setDate(5, Date.valueOf(book.getPublicationDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setString(6, book.getCategory());
            ps.setInt(7, book.getTotalCopies());
            ps.setInt(8, book.getAvailableCopies());
            ps.setInt(9, book.getBookId());

            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 7) Helpers to adjust available copies (for issue/return)
    public boolean decrementAvailableCopies(int bookId) {
        String sql = "UPDATE books SET available_copies = available_copies - 1 " +
                "WHERE id = ? AND available_copies > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean incrementAvailableCopies(int bookId) {
        String sql = "UPDATE books SET available_copies = available_copies + 1 " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Book mapRowToBook(ResultSet rs) throws SQLException {
        Book book = new Book(
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("publisher")
        );

        book.setBookId(rs.getInt("id"));

        Date pubDate = rs.getDate("published_date");
        if (pubDate != null) {
            book.setPublicationDate(pubDate.toLocalDate());
        }

        book.setCategory(rs.getString("category"));
        book.setTotalCopies(rs.getInt("total_copies"));
        book.setAvailableCopies(rs.getInt("available_copies"));

        return book;
    }
}
