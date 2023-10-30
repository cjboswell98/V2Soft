package com.product.rating.repository;

import com.product.rating.domain.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ClientRepository extends MongoRepository<Client, String> {
    Optional<Client> findByUsername(String username);
    Optional<Client> findByClientId(String clientId);
}
