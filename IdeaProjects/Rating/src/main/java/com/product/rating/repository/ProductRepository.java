package com.product.rating.repository;

import com.product.rating.model.RatingModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<RatingModel, String> {
}

