package com.library.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookRequest {
    private int id;
    private int bookId;
    private int readerId;
    private Integer librarianId;  // Nullable until approved
    private String requestType;    // ISSUE or RE_ISSUE
    private String status;         // PENDING, APPROVED, REJECTED, ON_HOLD
    private LocalDate holdUntilDate;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String notes;

    // Book and Reader references (for UI display)
    private Book book;
    private Reader reader;

    // Constructors
    public BookRequest() {}

    public BookRequest(int bookId, int readerId, String requestType) {
        this.bookId = bookId;
        this.readerId = readerId;
        this.requestType = requestType;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getReaderId() { return readerId; }
    public void setReaderId(int readerId) { this.readerId = readerId; }

    public Integer getLibrarianId() { return librarianId; }
    public void setLibrarianId(Integer librarianId) { this.librarianId = librarianId; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getHoldUntilDate() { return holdUntilDate; }
    public void setHoldUntilDate(LocalDate holdUntilDate) { this.holdUntilDate = holdUntilDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Reader getReader() { return reader; }
    public void setReader(Reader reader) { this.reader = reader; }

    @Override
    public String toString() {
        return "BookRequest{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", readerId=" + readerId +
                ", requestType='" + requestType + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
