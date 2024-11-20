package org.example.server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.common.RemoteLogin;
import org.example.common.Session;
import org.example.common.exceptions.*;
import org.example.server.authorization.Operation;
import org.example.server.authorization.AclStrategy;
import org.example.server.authorization.AuthorizationStrategy;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.*;
import java.util.Date;

public class Server implements RemoteLogin {

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/printer_server";
    private static final String DB_USER = "myuser";
    private static final String DB_PASSWORD = "mypassword";

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final JWTVerifier verifier;
    private final Algorithm alg;
    private final AuthorizationStrategy authorization = new AclStrategy();

    public Server() throws RemoteException, MalformedURLException, NoSuchAlgorithmException {
        System.setProperty("java.rmi.server.hostname", "localhost");
        Registry reg = LocateRegistry.createRegistry(1099);
        UnicastRemoteObject.exportObject(this, 0);
        Naming.rebind("//localhost:1099/Login", this);
        System.out.println("Server started on localhost.");
        System.out.println("Generating public private key pair.");
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        KeyPair gp = kpg.generateKeyPair();
        privateKey = (RSAPrivateKey) gp.getPrivate();
        publicKey = (RSAPublicKey) gp.getPublic();
        System.out.println("Public/private key pair generated.");
        alg = Algorithm.RSA256(publicKey, privateKey);
        verifier = JWT.require(alg).withIssuer("printer-server").build();

        try (Connection conn = getConnection()) {
            authorization.load(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error bootstrapping database: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Override
    public void register(String username, String hashedPassword, String salt) throws RemoteException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, hashed_password, salt) VALUES (?, ?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, salt);
            stmt.executeUpdate();
            System.out.println("User " + username + " registered successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error registering user: " + e.getMessage());
        }
    }

    @Override
    public String requestSalt(String username) throws RemoteException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT salt FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("salt");
                } else {
                    throw new RemoteException("User not found");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error retrieving salt: " + e.getMessage());
        }
    }

    @Override
    public Session login(String username, String hashedPassword) throws RemoteException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT hashed_password FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("hashed_password");
                    if (hashedPassword.equals(storedHashedPassword)) {
                        // Proceed to create the JWT token
                        Date currentDate = new Date();
                        Date expiration = Date.from(currentDate.toInstant().plusSeconds(20000));
                        String token = JWT.create()
                                .withIssuer("printer-server")
                                .withExpiresAt(expiration)
                                .withClaim("username", username)
                                .withClaim("access", authorization.getAccess(username))
                                .sign(alg);
                        return new Session(token);
                    } else {
                        throw new RemoteException("Invalid password");
                    }
                } else {
                    throw new RemoteException("User not found");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error during login: " + e.getMessage());
        }
    }

    @Override
    public void print(String filename, String printer, Session session) throws Exception {
        performAuthorization(session, Operation.PRINT);
        System.out.println("Printing " + filename + " to " + printer);
    }

    @Override
    public void queue(String printer, Session session) throws Exception {
        performAuthorization(session, Operation.QUEUE);
        System.out.println("Queueing " + printer);
    }

    @Override
    public void topQueue(String printer, int job, Session session) throws Exception {
        performAuthorization(session, Operation.TOP_QUEUE);
        System.out.println("Top queue " + printer + " " + job);
    }

    @Override
    public void start(Session session) throws Exception {
        performAuthorization(session, Operation.START);
        System.out.println("Server started.");
    }

    @Override
    public void stop(Session session) throws Exception {
        performAuthorization(session, Operation.STOP);
        System.out.println("Server stopped.");
    }

    @Override
    public void restart(Session session) throws Exception {
        performAuthorization(session, Operation.RESTART);
        System.out.println("Server restarted.");
    }

    @Override
    public void status(String printer, Session session) throws Exception {
        performAuthorization(session, Operation.STATUS);
        System.out.println("Printing " + printer);
    }

    @Override
    public void readConfig(String parameter, Session session) throws Exception {
        performAuthorization(session, Operation.READ_CONFIG);
        System.out.println("Reading config file " + parameter);
    }

    @Override
    public void setConfig(String parameter, String value, Session session) throws Exception {
        performAuthorization(session, Operation.SET_CONFIG);
        System.out.println("Setting config " + parameter + " to " + value);
    }

    private void performAuthorization(Session session, String operation) throws Exception {
        if (session == null) {
            System.out.println("Checking logged failed: Session not present");
            throw new SessionNotPresentException();
        }
        try {
            DecodedJWT jwt = verifier.verify(session.getValue());
            session = new Session(jwt);
        } catch (JWTVerificationException e) {
            System.out.println("Checking logged failed: Invalid JWT");
            throw new InvalidJwtException();
        }
        if (session.getExpiration().before(new Date())) {
            System.out.println("Checking logged failed: Session expired for " + session.getUsername());
            throw new LoggedOutException(session.getUsername());
        }
        if (!session.getAccess().contains(operation)) {
            System.out.println("Checking logged failed: No permission for " + session.getUsername());
            throw new UnauthorisedException(session.getUsername());
        }
    }
}
