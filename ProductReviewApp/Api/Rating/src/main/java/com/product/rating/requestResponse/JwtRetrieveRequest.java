package com.product.rating.requestResponse;

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
