package org.example.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Session implements Serializable {
    private String value;
    private Date expiration;
    private String[] scopes;
    private String username;
    private List<String> access;

    public Session(String jwt) {
        DecodedJWT decoded = JWT.decode(jwt);

        parseJwt(decoded);
    }

    public Session(DecodedJWT decoded) {
        parseJwt(decoded);
    }

    private void parseJwt(DecodedJWT decoded) {
        this.access = decoded.getClaim("access").asList(String.class);
        this.value = decoded.getToken();
        this.expiration = decoded.getExpiresAt();
        this.scopes = decoded.getClaim("scopes").asArray(String.class);
        this.username = decoded.getClaim("username").asString();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String[] getScopes() {
        return scopes;
    }

    public void setScopes(String[] scopes) {
        this.scopes = scopes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getAccess() {
        return access;
    }

    public void setAccess(List<String> access) {
        this.access = access;
    }
}
