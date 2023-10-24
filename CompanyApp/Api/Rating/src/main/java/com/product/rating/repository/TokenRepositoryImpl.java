package com.product.rating.repository;

import com.product.rating.domain.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class TokenRepositoryImpl implements TokenRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Token token, String collectionName) {
        mongoTemplate.save(token, collectionName);
    }

    @Override
    public Token findByCollectionAndOwnerId(String collectionName, String ownerId) {
        Date currentDate = new Date(System.currentTimeMillis());

        // Find a Token belonging to ownerId that isn't already expired
        Query query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(ownerId)
                .and("expirationDate").gt(currentDate));

        return mongoTemplate.findOne(query, Token.class, collectionName);
    }
}
