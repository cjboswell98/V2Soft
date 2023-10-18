package com.product.rating.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "clients")
public class ClientModel {

    //implement pipe directives to angular
    //also routing concepts
    //admin login and functionality only for admin
    //implement authgod secure can activate and can deactivate
    @Id
    private Long clientId;
    @Field(name = "tokenSalt")
    private String tokenSalt;

    @Field(name = "hashedSecret")
    private String hashedSecret;
    public ClientModel(String clientID, String tokenSalt, String hashedSecret) {
    }

    public ClientModel() {

    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getTokenSalt() {
        return tokenSalt;
    }

    public void setTokenSalt(String tokenSalt) {
        this.tokenSalt = tokenSalt;
    }

    public String getHashedSecret() {
        return hashedSecret;
    }

    public void setHashedSecret(String hashedSecret) {
        this.hashedSecret = hashedSecret;
    }
}
