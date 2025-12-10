package com.library.model;

import java.time.LocalDate;

public class Fine {
    private int fineId;
    private int loanId;
    private int readerId;
    private double amount;
    private String status; // "UNPAID", "PAID"
    private LocalDate createdDate;
    private LocalDate paidDate;

    // Constructors
    public Fine() {}

    public Fine(int loanId, int readerId, double amount) {
        this.loanId = loanId;
        this.readerId = readerId;
        this.amount = amount;
        this.status = "UNPAID";
        this.createdDate = LocalDate.now();
    }

    // Getters and Setters
    public int getFineId() {
        return fineId;
    }

    public void setFineId(int fineId) {
        this.fineId = fineId;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getReaderId() {
        return readerId;
    }

    public void setReaderId(int readerId) {
        this.readerId = readerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    @Override
    public String toString() {
        return "Fine{" +
                "fineId=" + fineId +
                ", loanId=" + loanId +
                ", readerId=" + readerId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                ", paidDate=" + paidDate +
                '}';
    }
}