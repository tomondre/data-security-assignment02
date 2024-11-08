package org.example;


import org.example.common.RemoteLogin;
import org.example.server.UserServer;
import org.example.server.model.ModelManager;

import java.io.IOException;

public class UserServerTest {
    public static void main(String[] args) throws IOException {
        RemoteLogin server = new UserServer(new ModelManager());
    }
}
