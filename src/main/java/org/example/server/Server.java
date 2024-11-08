package org.example.server;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.example.common.RemoteLogin;
import org.example.common.Session;
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

public class Server implements RemoteLogin {
    private Model model;
    private JWT jwt;
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

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
    }

    @Override
    public Session login(String username, String password) {
        model.addUser(username, password);
        Algorithm alg = Algorithm.RSA256(publicKey, privateKey);
        String token = JWT.create().withIssuer("printer-server").sign(alg);
        return new Session(token);
    }

    @Override
    public void print(String filename, String printer) {
        System.out.println("Printing " + filename + " to " + printer);
    }

    @Override
    public void queue(String printer) {
        System.out.println("Queueing " + printer);
    }

    @Override
    public void topQueue(String printer, int job) {
        System.out.println("Top queue " + printer + " " + job);
    }

    @Override
    public void start() {
        System.out.println("Server started.");
    }

    @Override
    public void stop() {
        System.out.println("Server stopped.");
    }

    @Override
    public void restart() {
        System.out.println("Server restarted.");
    }

    @Override
    public void status(String printer) {
        System.out.println("Printing " + printer);
    }

    @Override
    public void readConfig(String parameter) {
        System.out.println("Reading config file " + parameter);
    }

    @Override
    public void setConfig(String parameter, String value) {
        System.out.println("Setting config " + parameter + " to " + value);
    }
}
