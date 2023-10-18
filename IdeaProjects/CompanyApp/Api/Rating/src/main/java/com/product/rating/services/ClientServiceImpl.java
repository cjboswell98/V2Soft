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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService{

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public String createNewClient(String username, String password) {
        //Generate a random salt, ID, hash the password, then save them to a new client
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        String tokenSalt = Base64.getEncoder().encodeToString(saltBytes);

        String clientId = java.util.UUID.randomUUID().toString();

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hashedPassword = encoder.encode(password);

        Client newClient = new Client(clientId, username, hashedPassword);
        clientRepository.save(newClient);

        return "Client successfully created";
    }

    @Override
    public boolean verifyLoginInformation(String username, String password) {
        Optional<Client> clientOptional = clientRepository.findByUsername(username);

        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();

            // Verify if the provided password matches the hashed password in the database
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            String hashedPassword = client.getPassword();

            return encoder.matches(password, hashedPassword);
        } else {
            // Log the failure to find the client with the provided username
            infoAndDebuglogger.debug("No client found with the username: " + username);
            return false;
        }
    }


    @Override
    public Optional<Client> findClientById(String id) {
        return clientRepository.findById(id);
    }

    @Override
    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }

    @Override
    public String hashClientSecret(String password) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return encoder.encode(password);
    }
}
