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


    public Session(String jwt) {
        DecodedJWT decoded = JWT.decode(jwt);

        this.value = jwt;
        this.expiration = decoded.getExpiresAt();
        this.scopes = decoded.getClaim("scopes").asArray(String.class);
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
}
