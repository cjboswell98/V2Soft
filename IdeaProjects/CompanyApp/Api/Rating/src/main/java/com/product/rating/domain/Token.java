package com.product.rating.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;


@Data
public class Token {

    @Id
    String id;
    String clientId; //Should match a valid clientId
    String jwtString;
    Date expirationDate;

    public Token(String id, String clientId, String jwtString, Date expirationDate) {
        this.id = id;
        this.clientId = clientId;
        this.jwtString = jwtString;
        this.expirationDate = expirationDate;
    }

    public Token() {
    }
}