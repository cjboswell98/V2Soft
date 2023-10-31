package com.product.rating.repository;

import com.product.rating.domain.Image;
import com.product.rating.domain.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, Integer> {
    int deleteByReviewId(int reviewId);

}

