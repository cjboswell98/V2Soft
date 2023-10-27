package com.product.rating.model;

import com.product.rating.domain.Client;

public class JwtResponse {

    private String jwtToken;
    private Client client;

    public JwtResponse(String jwtToken) {
        this.client = client;
        this.jwtToken = jwtToken;

    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
