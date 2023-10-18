package com.product.rating.repository;

import com.product.rating.model.JwtToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JwtTokenRepository extends MongoRepository<JwtToken, String> {
}
