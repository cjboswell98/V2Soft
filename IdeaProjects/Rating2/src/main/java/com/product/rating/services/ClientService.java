package com.product.rating.services;

import com.product.rating.domain.Client;

import java.util.Optional;

public interface ClientService {

    String createNewClient(String clientSecret);

    Optional<Client> findClientById(String id);

    String hashClientSecret(String clientSecret);
}
