package org.example.common;

import org.example.common.exceptions.LoggedOutException;
import org.example.common.exceptions.SessionNotPresentException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteLogin extends Remote {
    Session login(String username, String password) throws Exception;
    void print(String filename, String printer, Session session) throws Exception;
    void queue(String printer, Session session) throws Exception;
    void topQueue(String printer, int job, Session session) throws Exception;
    void start(Session session) throws Exception;
    void stop(Session session) throws Exception;
    void restart(Session session) throws Exception;
    void status(String printer, Session session) throws Exception;
    void readConfig(String parameter, Session session) throws Exception;
    void setConfig(String parameter, String value, Session session) throws Exception;
}
