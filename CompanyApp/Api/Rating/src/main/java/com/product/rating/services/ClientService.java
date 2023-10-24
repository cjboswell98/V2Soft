package com.product.rating.services;

import com.product.rating.domain.Client;
import java.util.List;
import java.util.Optional;

public interface ClientService {

    String createNewClient(String firstName, String lastName, String username, String password);

    List<Client> findAllClients(); // New method to fetch all clients

    Optional<Client> findClientById(String id);

    String hashClientSecret(String password);

    boolean verifyLoginInformation(String username, String password);
}
