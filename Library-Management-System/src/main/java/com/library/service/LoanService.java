package com.library.service;

import com.library.model.Fine;
import com.library.model.Loan;
import com.library.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanService {

    public List<Loan> findActiveLoansByReaderId(int readerId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id, l.book_id, l.reader_id, l.issue_date, l.due_date, l.return_date, l.status, " +
                "b.title, b.author, b.isbn " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.id " +
                "WHERE l.reader_id = ? AND l.status = 'ISSUED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, readerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("id"));
                loan.setBookId(rs.getInt("book_id"));
                loan.setReaderId(rs.getInt("reader_id"));
                loan.setBorrowedDate(rs.getDate("issue_date").toLocalDate());
                loan.setDueDate(rs.getDate("due_date").toLocalDate());

                Date returnDate = rs.getDate("return_date");
                if (returnDate != null) {
                    loan.setReturnDate(returnDate.toLocalDate());
                }

                loan.setStatus(rs.getString("status"));
                loan.setBookTitle(rs.getString("title"));
                loan.setBookAuthor(rs.getString("author"));
                loan.setBookIsbn(rs.getString("isbn"));

                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public List<Loan> findLoanHistoryByReaderId(int readerId) {
        List<Loan> history = new ArrayList<>();
        String sql = "SELECT l.id, l.book_id, l.reader_id, l.issue_date, l.due_date, l.return_date, l.status, " +
                "b.title, b.author, b.isbn " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.id " +
                "WHERE l.reader_id = ? AND l.status = 'RETURNED' " +
                "ORDER BY l.return_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, readerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("id"));
                loan.setBookId(rs.getInt("book_id"));
                loan.setReaderId(rs.getInt("reader_id"));
                loan.setBorrowedDate(rs.getDate("issue_date").toLocalDate());
                loan.setDueDate(rs.getDate("due_date").toLocalDate());

                Date returnDate = rs.getDate("return_date");
                if (returnDate != null) {
                    loan.setReturnDate(returnDate.toLocalDate());
                }

                loan.setStatus(rs.getString("status"));
                loan.setBookTitle(rs.getString("title"));
                loan.setBookAuthor(rs.getString("author"));
                loan.setBookIsbn(rs.getString("isbn"));

                history.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    public int countActiveLoansByReaderId(int readerId) {
        String sql = "SELECT COUNT(*) FROM loans WHERE reader_id = ? AND status = 'ISSUED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, readerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countDueSoonByReaderId(int readerId, int daysThreshold) {
        String sql = "SELECT COUNT(*) FROM loans " +
                "WHERE reader_id = ? AND status = 'ISSUED' " +
                "AND due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, readerId);
            ps.setInt(2, daysThreshold);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countOverdueByReaderId(int readerId) {
        String sql = "SELECT COUNT(*) FROM loans " +
                "WHERE reader_id = ? AND status = 'ISSUED' " +
                "AND due_date < CURDATE()";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, readerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countTotalReadByReaderId(int readerId) {
        String sql = "SELECT COUNT(*) FROM loans WHERE reader_id = ? AND status = 'RETURNED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, readerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void generateFinesForOverdueLoans() {
        FineService fineService = new FineService();

        // Get all overdue issued loans without existing fines
        String sql = "SELECT l.id, l.reader_id, l.due_date FROM loans l " +
                "WHERE l.status = 'ISSUED' AND l.due_date < CURDATE() " +
                "AND NOT EXISTS (SELECT 1 FROM fines f WHERE f.loan_id = l.id)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int loanId = rs.getInt("id");
                int readerId = rs.getInt("reader_id");
                LocalDate dueDate = rs.getDate("due_date").toLocalDate();

                double fineAmount = fineService.calculateFine(dueDate);

                if (fineAmount > 0) {
                    Fine fine = new Fine(loanId, readerId, fineAmount);
                    fineService.addFine(fine);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean issueBook(int bookId, int readerId, LocalDate borrowDate, LocalDate dueDate) {
        return issueBook(bookId, readerId, 0, borrowDate, dueDate, null);
    }

    public boolean issueBook(int bookId, int readerId, int librarianId, LocalDate issueDate, LocalDate dueDate, String notes) {
        String sql = "INSERT INTO loans (book_id, reader_id, librarian_id, issue_date, due_date, status, notes) " +
                "VALUES (?, ?, ?, ?, ?, 'ISSUED', ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, bookId);
            ps.setInt(2, readerId);
            ps.setInt(3, librarianId);
            ps.setDate(4, Date.valueOf(issueDate));
            ps.setDate(5, Date.valueOf(dueDate));
            ps.setString(6, notes);

            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean returnBook(int loanId) {
        String sql = "UPDATE loans SET status = 'RETURNED', return_date = CURDATE() WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loanId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Loan findActiveLoanByBookId(int bookId) {
        String sql = "SELECT l.id, l.book_id, l.reader_id, l.issue_date, l.due_date, l.return_date, l.status, " +
                "b.title, b.author, b.isbn, u.full_name as reader_name " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN users u ON l.reader_id = u.id " +
                "WHERE l.book_id = ? AND l.status = 'ISSUED' " +
                "LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("id"));
                loan.setBookId(rs.getInt("book_id"));
                loan.setReaderId(rs.getInt("reader_id"));
                loan.setBorrowedDate(rs.getDate("issue_date").toLocalDate());
                loan.setDueDate(rs.getDate("due_date").toLocalDate());

                Date returnDate = rs.getDate("return_date");
                if (returnDate != null) {
                    loan.setReturnDate(returnDate.toLocalDate());
                }

                loan.setStatus(rs.getString("status"));
                loan.setBookTitle(rs.getString("title"));
                loan.setBookAuthor(rs.getString("author"));
                loan.setBookIsbn(rs.getString("isbn"));
                loan.setReaderName(rs.getString("reader_name"));

                return loan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
