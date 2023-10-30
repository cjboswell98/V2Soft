package com.product.rating.services;

public interface AuthorizationService {
    boolean verifyToken(String token, String tokenFunction, String clientId);

    boolean verifyClient(String clientId, String clientSecret);
}
