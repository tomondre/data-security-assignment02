package org.example.server;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.common.RemoteLogin;
import org.example.common.Session;
import org.example.common.exceptions.InvalidJwtException;
import org.example.common.exceptions.LoggedOutException;
import org.example.common.exceptions.SessionNotPresentException;
import org.example.common.exceptions.UnauthorisedException;
import org.example.server.model.ACL;
import org.example.server.model.AclOperation;
import org.example.server.model.Model;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

public class Server implements RemoteLogin {
    private Model model;
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final JWTVerifier verifier;
    private final Algorithm alg;
    private final ACL acl = new ACL();

    public Server(Model model) throws RemoteException, MalformedURLException, NoSuchAlgorithmException {
        this.model = model;
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
    }

    @Override
    public Session login(String username, String password) {
        Date currentDate = new Date();

//        TODO Set correct expiration. Maybe we can create a case from the client for this?
//        Date expiration = Date.from(currentDate.toInstant().plusSeconds(20));
        Date expiration = Date.from(currentDate.toInstant().plusSeconds(20000));
        String token = JWT.create()
                .withIssuer("printer-server")
                .withExpiresAt(expiration)
                .withClaim("username", username)
                .withClaim("access", acl.getRights(username))
                .sign(alg);
        return new Session(token);
    }

    @Override
    public void print(String filename, String printer, Session session) throws Exception {
        performAuthorization(session, AclOperation.PRINT);
        System.out.println("Printing " + filename + " to " + printer);
    }

    @Override
    public void queue(String printer, Session session) throws Exception {
        performAuthorization(session, AclOperation.QUEUE);
        System.out.println("Queueing " + printer);
    }

    @Override
    public void topQueue(String printer, int job, Session session) throws Exception {
        performAuthorization(session, AclOperation.TOP_QUEUE);
        System.out.println("Top queue " + printer + " " + job);
    }

    @Override
    public void start(Session session) throws Exception {
        performAuthorization(session, AclOperation.START);
        System.out.println("Server started.");
    }

    @Override
    public void stop(Session session) throws Exception {
        performAuthorization(session, AclOperation.STOP);
        System.out.println("Server stopped.");
    }

    @Override
    public void restart(Session session) throws Exception {
        performAuthorization(session, AclOperation.RESTART);
        System.out.println("Server restarted.");
    }

    @Override
    public void status(String printer, Session session) throws Exception {
        performAuthorization(session, AclOperation.STATUS);
        System.out.println("Printing " + printer);
    }

    @Override
    public void readConfig(String parameter, Session session) throws Exception {
        performAuthorization(session, AclOperation.READ_CONFIG);
        System.out.println("Reading config file " + parameter);
    }

    @Override
    public void setConfig(String parameter, String value, Session session) throws Exception {
        performAuthorization(session, AclOperation.SET_CONFIG);
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
        } if (!session.getAclMap().contains(operation)) {
            System.out.println("Checking logged failed: No permission for " + session.getUsername());
            throw new UnauthorisedException(session.getUsername());
        }
    }
}
