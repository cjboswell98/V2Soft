package com.product.rating.repository;


import com.product.rating.domain.Token;

public interface TokenRepository {
    void save(Token token, String collectionName);

   Token findByCollectionAndOwnerId(String collectionName, String ownerId);
}
