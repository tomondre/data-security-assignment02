package org.example.common;

import com.auth0.jwt.interfaces.DecodedJWT;

public class Session {
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
