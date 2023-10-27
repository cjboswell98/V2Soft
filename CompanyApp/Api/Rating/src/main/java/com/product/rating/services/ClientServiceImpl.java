package com.product.rating.services;

import com.product.rating.domain.Client;
import com.product.rating.domain.Role;
import com.product.rating.repository.ClientRepository;
import com.product.rating.repository.RoleRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

// Service class responsible for handling Client operations
@Service
public class ClientServiceImpl implements ClientService {

    // Logger for logging information and debug messages
    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");

    // Dependency injection of the ClientRepository
    @Autowired
    private final ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public void initRolesAndUser() {
        Role adminRole = new Role();
        adminRole.setRoleName("Admin");
        adminRole.setRoleDescription("Admin role");
        roleRepository.save(adminRole);

        Role clientRole = new Role();
        clientRole.setRoleName("Client");
        clientRole.setRoleDescription("Default role for new client");
        roleRepository.save(clientRole);

        Client adminUser = new Client();
        adminUser.setFirstName("admin");
        adminUser.setLastName("admin");
        adminUser.setUserName("admin123");
        adminUser.setPassword("admin");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRole(adminRoles);
        clientRepository.save(adminUser);

//        Client client = new Client();
//        client.setFirstName("Cedric");
//        client.setLastName("Boswell");
//        client.setUserName("cjbos123");
//        client.setPassword("pass");
//        Set<Role> clientRoles = new HashSet<>();
//        clientRoles.add(clientRole);
//        client.setRole(clientRoles);
//        clientRepository.save(client);
    }

    // Constructor to inject the ClientRepository dependency
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Method to create a new client with a unique ID, username, and hashed password
    @Override
    public String createNewClient(String firstName, String lastName, String username, String password) {
        // Generate a random salt, ID, hash the password, then save them to a new client
        SecureRandom random = new SecureRandom(); // Initializing SecureRandom for generating random bytes
        byte[] saltBytes = new byte[16]; // Initializing a byte array for salt
        random.nextBytes(saltBytes); // Generating random bytes for salt
        String tokenSalt = Base64.getEncoder().encodeToString(saltBytes); // Converting salt bytes to a string

        String clientId = java.util.UUID.randomUUID().toString(); // Generating a unique ID for the client

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder(); // Creating a PasswordEncoder instance
        String hashedPassword = encoder.encode(password); // Hashing the provided password

        Client newClient = new Client(clientId, firstName, lastName, username, hashedPassword); // Creating a new client object
        clientRepository.save(newClient); // Saving the new client to the repository

        return "Client successfully created"; // Returning a success message
    }


    // Method to verify login information based on the provided username and password
//    @Override
//    public boolean verifyLoginInformation(String username, String password) {
//        Optional<Client> clientOptional = clientRepository.findByUsername(username); // Retrieving the client based on the username from the repository
//
//        if (clientOptional.isPresent()) { // Checking if the client is present
//            Client client = clientOptional.get(); // Extracting the client from the Optional
//
//            // Verify if the provided password matches the hashed password in the database
//            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder(); // Creating a PasswordEncoder instance
//            String hashedPassword = client.getPassword(); // Retrieving the hashed password from the client
//
//            return encoder.matches(password, hashedPassword); // Returning the result of password comparison
//        } else {
//            // Log the failure to find the client with the provided username
//            infoAndDebuglogger.debug("No client found with the username: " + username); // Logging the failure to find the client
//            return false; // Returning false to indicate the failure
//        }
//    }

    // Method to find a client based on the provided ID
    @Override
    public Optional<Client> findClientById(String id) {
        return clientRepository.findById(id); // Returning the client based on the provided ID
    }

    // Method to fetch all clients from the repository
    @Override
    public List<Client> findAllClients() {
        return clientRepository.findAll(); // Returning all clients from the repository
    }

    // Method to hash a client's secret password
    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
