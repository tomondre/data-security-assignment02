package org.example.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteLogin extends Remote {
    Session login(String username, String password) throws RemoteException;
    void print(String filename, String printer, Session session) throws RemoteException;
    void queue(String printer, Session session) throws RemoteException;
    void topQueue(String printer, int job, Session session) throws RemoteException;
    void start(Session session) throws RemoteException;
    void stop(Session session) throws RemoteException;
    void restart(Session session) throws RemoteException;
    void status(String printer, Session session) throws RemoteException;
    void readConfig(String parameter, Session session) throws RemoteException;
    void setConfig(String parameter, String value, Session session) throws RemoteException;
}
