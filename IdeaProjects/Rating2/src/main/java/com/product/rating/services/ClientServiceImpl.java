package com.product.rating.services;

import com.product.rating.domain.Client;
import com.product.rating.repository.ClientRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService{

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public String createNewClient(String clientSecret) {
        //Generate a random salt, ID, hash the secret, then save them to a new client
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        String tokenSalt = Base64.getEncoder().encodeToString(saltBytes);

        String clientID = java.util.UUID.randomUUID().toString();

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hashedSecret = encoder.encode(clientSecret);

        Client newClient = new Client(clientID, tokenSalt, hashedSecret);
        clientRepository.save(newClient);

        return "Client successfully created";
    }

    @Override
    public Optional<Client> findClientById(String id) {
        return clientRepository.findById(id);
    }

    @Override
    public String hashClientSecret(String clientSecret) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return encoder.encode(clientSecret);
    }
}
