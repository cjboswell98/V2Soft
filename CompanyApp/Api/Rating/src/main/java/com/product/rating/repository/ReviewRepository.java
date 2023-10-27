package com.product.rating.repository;

import com.product.rating.domain.ReviewDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<ReviewDomain, Integer> {
    long deleteByReviewId(Integer reviewId);

}

