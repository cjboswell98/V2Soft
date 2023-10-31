package com.product.rating.repository;

import com.product.rating.domain.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends MongoRepository<Image, Integer> {
    Optional<Image> findByName(String fileName);
}
