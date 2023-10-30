package com.product.rating.model;

public class JwtRetrieveRequest {
    private String clientSecret;

    public JwtRetrieveRequest() {
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
