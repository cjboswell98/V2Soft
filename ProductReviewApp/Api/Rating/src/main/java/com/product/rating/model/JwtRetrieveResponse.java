package com.product.rating.model;

public class JwtRetrieveResponse {
    private String jwtToken;

    public JwtRetrieveResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
