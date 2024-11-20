# Data Security Assignment 02

This guide will help you compile and run both the server and client components for the RMI-based application.

# Passwords
Alice, Bob, and Cecilia are the only users that are allowed to log in. Their passwords are Alice123, Bob123, and Cecilia123, respectively.

# Delimitations
* The server operations does't have to be implemented
* Enrollment of users are not in the scope of the assignment, the users are already enrolled

## Server Setup

**Run the Server**

## Client Setup

**Run the Client**

Docker
docker run --name my_postgres -e POSTGRES_USER=myuser -e POSTGRES_PASSWORD=mypassword -e POSTGRES_DB=printer_server -p 5432:5432 -d postgres:15

Code to make table and in postgres:

CREATE TABLE users (
username VARCHAR(50) PRIMARY KEY,
hashed_password VARCHAR(255) NOT NULL,
salt VARCHAR(255) NOT NULL
);
