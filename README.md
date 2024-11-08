# Data Security Assignment 02

This guide will help you compile and run both the server and client components for the RMI-based application.

# Requirements
* ~~The print server must support operations (print, queue, topQueue, start, restart, status, readConfig, setConfig), could print out the results to console log.~~
* All requests form client to server MUST be authenticated
* Implementation of simple session management mechanism for server requests
   * Generate JWT token on login and return it
   * In client use the jwt token for each request. Store the session/jwt in Java object
   * Throw LoggedOutException from server in case the jwt is expired.
* Implementation of secure communication

# Delimitations
* The server operations does't have to be implemented
* Enrollment of users are not in the scope of the assignment, the users are already enrolled

## Server Setup

1. **Compile the Server Code**

   Use the following command to compile the server code:

   ```sh
   javac UserServerTest.java
   ```

2. **Run the Server**

   After compiling, start the server with the command below:

   ```sh
   java UserServerTest
   ```

   > **Note**: Ensure that the server is running before attempting to connect with the client.

## Client Setup

1. **Compile the Client Code**

   Use the following command to compile the client code:

   ```sh
   javac ClientTest.java
   ```

2. **Run the Client**

   With the server running, you can start the client by executing:

   ```sh
   java ClientTest
   ```
