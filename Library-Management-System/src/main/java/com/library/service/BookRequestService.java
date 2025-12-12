package com.library.service;

import com.library.model.BookRequest;
import com.library.model.Book;
import com.library.model.Reader;
import com.library.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookRequestService {

    /**
     * Create a new book request (ISSUE or RE_ISSUE).
     * Called by Reader dashboard when requesting a book.
     */
    public boolean createRequest(int bookId, int readerId, String requestType) {
        String sql = "INSERT INTO book_requests (book_id, reader_id, request_type, status, created_at) " +
                "VALUES (?, ?, ?, 'PENDING', NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, readerId);
            stmt.setString(3, requestType);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all PENDING requests (for librarian dashboard).
     */
    public List<BookRequest> getPendingRequests() {
        List<BookRequest> requests = new ArrayList<>();
        String sql = "SELECT br.id, br.book_id, br.reader_id, br.librarian_id, " +
                "       br.request_type, br.status, br.hold_until_date, br.created_at, br.resolved_at, br.notes, " +
                "       b.title, b.author, u.full_name " +
                "FROM book_requests br " +
                "JOIN books b ON br.book_id = b.id " +
                "JOIN users u ON br.reader_id = u.id " +
                "WHERE br.status = 'PENDING' " +
                "ORDER BY br.created_at ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                BookRequest req = new BookRequest();
                req.setId(rs.getInt("id"));
                req.setBookId(rs.getInt("book_id"));
                req.setReaderId(rs.getInt("reader_id"));
                req.setLibrarianId(rs.getInt("librarian_id"));
                req.setRequestType(rs.getString("request_type"));
                req.setStatus(rs.getString("status"));
                req.setHoldUntilDate(rs.getDate("hold_until_date") != null ?
                        rs.getDate("hold_until_date").toLocalDate() : null);
                req.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                req.setResolvedAt(rs.getTimestamp("resolved_at") != null ?
                        rs.getTimestamp("resolved_at").toLocalDateTime() : null);
                req.setNotes(rs.getString("notes"));

                // Populate book and reader names (we'll use these in the UI)
                Book book = new Book();
                book.setBookId(req.getBookId());  // ✅ Changed from setId() to setBookId()
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                req.setBook(book);

                Reader reader = new Reader();
                reader.setId(req.getReaderId());
                reader.setFullName(rs.getString("full_name"));
                req.setReader(reader);

                requests.add(req);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /**
     * Approve a request: update status to APPROVED and set librarian_id.
     * This does NOT automatically issue the book—just records the librarian's approval.
     * The actual loan is created when librarian clicks "Issue" on the approved request.
     */
    public boolean approveRequest(int requestId, int librarianId) {
        String sql = "UPDATE book_requests SET status = 'APPROVED', librarian_id = ?, resolved_at = NOW() WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, librarianId);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reject a request: update status to REJECTED.
     */
    public boolean rejectRequest(int requestId, int librarianId, String reason) {
        String sql = "UPDATE book_requests SET status = 'REJECTED', librarian_id = ?, notes = ?, resolved_at = NOW() WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, librarianId);
            stmt.setString(2, reason != null ? reason : "No reason provided");
            stmt.setInt(3, requestId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Put a request on hold until a specific date.
     */
    public boolean holdRequest(int requestId, int librarianId, LocalDate holdUntilDate) {
        String sql = "UPDATE book_requests SET status = 'ON_HOLD', hold_until_date = ?, librarian_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(holdUntilDate));
            stmt.setInt(2, librarianId);
            stmt.setInt(3, requestId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get a single request by ID.
     */
    public BookRequest getRequestById(int requestId) {
        String sql = "SELECT * FROM book_requests WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BookRequest req = new BookRequest();
                req.setId(rs.getInt("id"));
                req.setBookId(rs.getInt("book_id"));
                req.setReaderId(rs.getInt("reader_id"));
                req.setLibrarianId(rs.getInt("librarian_id"));
                req.setRequestType(rs.getString("request_type"));
                req.setStatus(rs.getString("status"));
                req.setHoldUntilDate(rs.getDate("hold_until_date") != null ?
                        rs.getDate("hold_until_date").toLocalDate() : null);
                req.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                req.setResolvedAt(rs.getTimestamp("resolved_at") != null ?
                        rs.getTimestamp("resolved_at").toLocalDateTime() : null);
                req.setNotes(rs.getString("notes"));
                return req;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
