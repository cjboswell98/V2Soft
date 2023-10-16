package com.product.rating.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document("clients")
public class Client {
    @Id
    String clientId;
    String tokenSalt;
    String hashedSecret;

    public Client() {
    }

    public Client(String clientId, String tokenSalt, String hashedSecret) {
        this.clientId = clientId;
        this.tokenSalt = tokenSalt;
        this.hashedSecret = hashedSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
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
