package org.example;


import org.example.server.Server;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ServerMain {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        new Server();
    }
}
