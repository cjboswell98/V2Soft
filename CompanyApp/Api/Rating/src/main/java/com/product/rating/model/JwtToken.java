package com.product.rating.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "jwtTokens")
public class JwtToken {

    @Id
    private String token;
    private Date expirationDate;

    public JwtToken() {
    }

    public JwtToken(String token, Date expiration) {
        this.token = token;
        this.expirationDate = expiration;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}