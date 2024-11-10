package org.example.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.server.model.MyDate;

import java.io.Serializable;
import java.util.Date;

public class Session implements Serializable {
    private String value;
    private Date expiration;
    private String[] scopes;
    private String username;

    public Session(String jwt) {
        DecodedJWT decoded = JWT.decode(jwt);

        parseJwt(decoded);
    }

    public Session(DecodedJWT decoded) {
        parseJwt(decoded);
    }

    private void parseJwt(DecodedJWT decoded) {
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
}
