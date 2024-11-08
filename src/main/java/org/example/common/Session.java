package org.example.common;

import java.io.Serializable;

public class Session implements Serializable {
    private String jwt;

    public Session(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
