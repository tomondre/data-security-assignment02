package org.example;


import org.example.common.RemoteLogin;
import org.example.server.Server;
import org.example.server.model.ModelManager;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ServerMain {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        new Server(new ModelManager());
    }
}
