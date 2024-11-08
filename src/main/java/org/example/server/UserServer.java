package org.example.server;


import org.example.common.RemoteLogin;
import org.example.server.model.Model;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class UserServer implements RemoteLogin {

    private Model model;

    public UserServer(Model model) throws RemoteException, MalformedURLException {
        this.model = model;
        System.setProperty("java.rmi.server.hostname", "localhost");
        Registry reg = LocateRegistry.createRegistry(1099);
        UnicastRemoteObject.exportObject(this, 0);
        Naming.rebind("//localhost:1099/Login", this);
        System.out.println("Server started on localhost.");
    }

    @Override
    public void login(String username, String password) {
        model.addUser(username, password);
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
