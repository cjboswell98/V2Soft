package com.product.rating.repository;

import com.product.rating.domain.FileData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileDataRepository extends MongoRepository<FileData, Integer> {
}
