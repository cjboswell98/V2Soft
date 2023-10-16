package com.product.rating.repository;

import com.product.rating.model.RatingModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<RatingModel, String> {
}

