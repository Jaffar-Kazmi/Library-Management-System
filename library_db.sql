-- Create database if not exists
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Disable foreign key checks for clean setup
SET FOREIGN_KEY_CHECKS = 0;

-- Drop tables if they exist
DROP TABLE IF EXISTS fines;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

-- Enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Create users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role ENUM('LIBRARIAN', 'READER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE'
);

-- Create books table
CREATE TABLE books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    category VARCHAR(50),
    published_date DATE,
    total_copies INT DEFAULT 1,
    available_copies INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create loans table
CREATE TABLE loans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    reader_id INT NOT NULL,
    librarian_id INT,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status ENUM('ISSUED', 'RETURNED', 'OVERDUE') DEFAULT 'ISSUED',
    notes TEXT,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (reader_id) REFERENCES users(id),
    FOREIGN KEY (librarian_id) REFERENCES users(id)
);

-- Create fines table
CREATE TABLE fines (
    id INT PRIMARY KEY AUTO_INCREMENT,
    loan_id INT NOT NULL,
    reader_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status ENUM('UNPAID', 'PAID') DEFAULT 'UNPAID',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_date TIMESTAMP NULL,
    FOREIGN KEY (loan_id) REFERENCES loans(id),
    FOREIGN KEY (reader_id) REFERENCES users(id)
);

-- Insert sample users
INSERT INTO users (username, password, full_name, email, role, status) VALUES
('admin', 'admin123', 'System Administrator', 'admin@library.com', 'LIBRARIAN', 'ACTIVE'),
('john', 'pass123', 'John Doe', 'john@email.com', 'READER', 'ACTIVE'),
('jane', 'pass123', 'Jane Smith', 'jane@email.com', 'READER', 'ACTIVE'),
('mike', 'pass123', 'Mike Johnson', 'mike@email.com', 'READER', 'ACTIVE'),
('sarah', 'pass123', 'Sarah Wilson', 'sarah@email.com', 'LIBRARIAN', 'ACTIVE'),
('david', 'pass123', 'David Brown', 'david@email.com', 'READER', 'INACTIVE'),
('librarian', 'password', 'Default Librarian', 'lib@library.com', 'LIBRARIAN', 'ACTIVE'),
('reader', 'password', 'Default Reader', 'reader@library.com', 'READER', 'ACTIVE');

-- Insert sample books
INSERT INTO books (title, author, isbn, category, published_date, total_copies, available_copies) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 'Fiction', '1925-04-10', 5, 5),
('To Kill a Mockingbird', 'Harper Lee', '978-0446310789', 'Fiction', '1960-07-11', 3, 3),
('1984', 'George Orwell', '978-0451524935', 'Science Fiction', '1949-06-08', 4, 4),
('Pride and Prejudice', 'Jane Austen', '978-0141439518', 'Romance', '1813-01-28', 4, 4),
('The Catcher in the Rye', 'J.D. Salinger', '978-0316769488', 'Fiction', '1951-07-16', 3, 3),
('The Hobbit', 'J.R.R. Tolkien', '978-0547928227', 'Fantasy', '1937-09-21', 6, 6),
('Fahrenheit 451', 'Ray Bradbury', '978-1451673319', 'Science Fiction', '1953-10-19', 4, 4),
('The Lord of the Rings', 'J.R.R. Tolkien', '978-0544003415', 'Fantasy', '1954-07-29', 3, 3),
('Animal Farm', 'George Orwell', '978-0451526342', 'Political Satire', '1945-08-17', 5, 5),
('Jane Eyre', 'Charlotte Bronte', '978-0141441146', 'Romance', '1847-10-16', 3, 3),
('Brave New World', 'Aldous Huxley', '978-0060850524', 'Science Fiction', '1932-01-01', 4, 4),
('The Chronicles of Narnia', 'C.S. Lewis', '978-0066238500', 'Fantasy', '1950-10-16', 5, 5),
('Wuthering Heights', 'Emily Bronte', '978-0141439556', 'Romance', '1847-12-01', 3, 3),
('The Alchemist', 'Paulo Coelho', '978-0062315007', 'Fiction', '1988-01-01', 6, 6),
('The Da Vinci Code', 'Dan Brown', '978-0307474278', 'Thriller', '2003-03-18', 4, 4),
('Harry Potter and the Sorcerer''s Stone', 'J.K. Rowling', '978-0590353427', 'Fantasy', '1997-06-26', 8, 8);
