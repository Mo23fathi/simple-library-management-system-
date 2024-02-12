Library Management System
This is a simple Java application for managing a library system. It provides functionalities to add books, issue books to users, return books, update user information, and update book information.

Features
Add Book: Allows users to add new books to the library system by providing book details such as name, genre, and price.
Issue Book: Enables users to issue books to library members by entering details like book ID, user ID, period, and issue date.
Return Book: Allows users to return books by providing the issue ID and return date. It calculates fines for late returns.
Update User: Provides functionality to update user information such as username and password.
Update Book: Allows users to update book information including book name, genre, and price.
Usage
Add Book:

Click on the "Add Book" button.
Enter the book details including name, genre, and price.
Click "Submit" to add the book to the system.
Issue Book:

Click on the "Issue Book" button.
Enter the details including book ID, user ID, period, and issue date.
Click "Submit" to issue the book to the user.
Return Book:

Click on the "Return Book" button.
Enter the issue ID and return date.
Click "Return" to mark the book as returned and calculate any fines if applicable.
Update User:

Click on the "Update User" button.
Enter the user ID, new username, and new password.
Click "Update" to save the changes.
Update Book:

Click on the "Update Book" button.
Enter the book ID, new book name, new genre, and new price.
Click "Update" to save the changes.


Dependencies
Java Swing for the GUI components.
JDBC for database connectivity.


Database
This application assumes the existence of a database named "LIBRARY" with the following tables:

DATABASE SCHEMA :
use LIBRARY;

-- CREATE TABLE USERS(
-- UID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
-- USERNAME VARCHAR(50) NOT NULL , 
-- PASSWORD VARCHAR(50) NOT NULL ,
-- ADMIN BOOLEAN DEFAULT FALSE );

-- CREATE TABLE BOOKS(
-- BID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
-- BNAME VARCHAR(50) NOT NULL ,
-- GENRE VARCHAR(50)NOT NULL ,
-- PRICE INT NOT NULL );

-- CREATE TABLE ISSUED(
-- IID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
-- UID INT NOT NULL ,
-- BID INT NOT NULL,
-- ISSUED_DATE VARCHAR(20) NOT NULL ,
-- RETURN_DATE VARCHAR(20),
-- PERIOD INT NOT NULL ,
-- FINE INT,
-- FOREIGN KEY (UID) REFERENCES Users(UID),
-- FOREIGN KEY (BID) REFERENCES Books(BID)) 

BOOKS:

Columns: BID (Book ID), BNAME (Book Name), GENRE, PRICE
USERS:

Columns: UID (User ID), USERNAME, PASSWORD
ISSUED:

Columns: IID (Issue ID), UID (User ID), BID (Book ID), ISSUED_DATE, RETURN_DATE, PERIOD, FINE
