package org.example.client;


import org.example.common.RemoteLogin;
import org.example.common.Session;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class Client implements RemoteLogin {

    private RemoteLogin server;
    private Session storedSession;

    public Client() throws IOException {
        try {
            server = (RemoteLogin) Naming.lookup("rmi://localhost:1099/Login");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override public Session login(String userName, String password) throws RemoteException {
        storedSession = server.login(userName, password);
        return storedSession;
    }

    @Override
    public void print(String filename, String printer) throws RemoteException {
        server.print(filename, printer);
    }

    @Override
    public void queue(String printer) throws RemoteException {
        server.queue(printer);
    }

    @Override
    public void topQueue(String printer, int job) throws RemoteException {
        server.topQueue(printer, job);
    }

    @Override
    public void start() throws RemoteException {
        server.start();
    }

    @Override
    public void stop() throws RemoteException {
        server.stop();
    }

    @Override
    public void restart() throws RemoteException {
        server.restart();
    }

    @Override
    public void status(String printer) throws RemoteException {
        server.status(printer);
    }

    @Override
    public void readConfig(String parameter) throws RemoteException {
        server.readConfig(parameter);
    }

    @Override
    public void setConfig(String parameter, String value) throws RemoteException {
        server.setConfig(parameter, value);
    }
}
