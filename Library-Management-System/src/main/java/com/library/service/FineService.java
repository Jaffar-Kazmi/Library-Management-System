package com.library.service;

import com.library.model.Fine;
import com.library.model.Loan;
import com.library.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FineService {

    /**
     * Fine structure:
     * Days 1-5: 100 per day
     * Days 6-10: 200 per day
     * Days 11+: 500 per day
     */
    public double calculateFine(LocalDate dueDate) {
        LocalDate today = LocalDate.now();

        if (today.isBefore(dueDate) || today.equals(dueDate)) {
            return 0; // No fine yet
        }

        long daysOverdue = ChronoUnit.DAYS.between(dueDate, today);
        double fine = 0;

        if (daysOverdue <= 5) {
            fine = daysOverdue * 100;
        } else if (daysOverdue <= 10) {
            fine = (5 * 100) + ((daysOverdue - 5) * 200);
        } else {
            fine = (5 * 100) + (5 * 200) + ((daysOverdue - 10) * 500);
        }

        return fine;
    }

    public double calculateFineForLoan(Loan loan) {
        if (loan == null || loan.getDueDate() == null) {
            return 0;
        }
        return calculateFine(loan.getDueDate());
    }

    public boolean addFine(Fine fine) {
        String sql = "INSERT INTO fines (loan_id, reader_id, amount, status, created_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, fine.getLoanId());
            ps.setInt(2, fine.getReaderId());
            ps.setDouble(3, fine.getAmount());
            ps.setString(4, fine.getStatus());
            ps.setDate(5, Date.valueOf(fine.getCreatedDate()));

            int affected = ps.executeUpdate();
            if (affected == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    fine.setFineId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Fine> findUnpaidFinesByReaderId(int readerId) {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT id, loan_id, reader_id, amount, status, created_date, paid_date " +
                "FROM fines WHERE reader_id = ? AND status = 'UNPAID'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, readerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Fine fine = new Fine();
                fine.setFineId(rs.getInt("id"));
                fine.setLoanId(rs.getInt("loan_id"));
                fine.setReaderId(rs.getInt("reader_id"));
                fine.setAmount(rs.getDouble("amount"));
                fine.setStatus(rs.getString("status"));
                fine.setCreatedDate(rs.getDate("created_date").toLocalDate());

                Date paidDate = rs.getDate("paid_date");
                if (paidDate != null) {
                    fine.setPaidDate(paidDate.toLocalDate());
                }

                fines.add(fine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    public List<Fine> findAllFinesByReaderId(int readerId) {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT id, loan_id, reader_id, amount, status, created_date, paid_date " +
                "FROM fines WHERE reader_id = ? ORDER BY created_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, readerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Fine fine = new Fine();
                fine.setFineId(rs.getInt("id"));
                fine.setLoanId(rs.getInt("loan_id"));
                fine.setReaderId(rs.getInt("reader_id"));
                fine.setAmount(rs.getDouble("amount"));
                fine.setStatus(rs.getString("status"));
                fine.setCreatedDate(rs.getDate("created_date").toLocalDate());

                Date paidDate = rs.getDate("paid_date");
                if (paidDate != null) {
                    fine.setPaidDate(paidDate.toLocalDate());
                }

                fines.add(fine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    public double getTotalUnpaidFinesByReaderId(int readerId) {
        String sql = "SELECT SUM(amount) FROM fines WHERE reader_id = ? AND status = 'UNPAID'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, readerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double total = rs.getDouble(1);
                if (rs.wasNull()) return 0;
                return total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean payFine(int fineId) {
        String sql = "UPDATE fines SET status = 'PAID', paid_date = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, fineId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFine(int fineId) {
        String sql = "DELETE FROM fines WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, fineId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
