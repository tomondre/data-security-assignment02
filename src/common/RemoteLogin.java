package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteLogin extends Remote {
    void login(String username, String password) throws RemoteException;
    void print(String filename, String printer) throws RemoteException;
    void queue(String printer) throws RemoteException;
    void topQueue(String printer, int job) throws RemoteException;
    void start() throws RemoteException;
    void stop() throws RemoteException;
    void restart() throws RemoteException;
    void status(String printer) throws RemoteException;
    void readConfig(String parameter) throws RemoteException;
    void setConfig(String parameter, String value) throws RemoteException;
}
