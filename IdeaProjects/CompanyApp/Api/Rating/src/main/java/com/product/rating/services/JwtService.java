package com.product.rating.services;

import com.product.rating.model.ClientModel;
import com.product.rating.model.JwtToken;
import com.product.rating.repository.JwtTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
@Service // Indicates that the class is a service component
public class JwtService {

    private final PasswordEncoder passwordEncoder; // Field to store the PasswordEncoder instance
    private final JwtTokenRepository jwtTokenRepository; // Field to store the JwtTokenRepository instance

    @Autowired // Annotation for constructor-based dependency injection
    public JwtService(JwtTokenRepository jwtTokenRepository, PasswordEncoder passwordEncoder) {
        this.jwtTokenRepository = jwtTokenRepository; // Initializing the JwtTokenRepository field
        this.passwordEncoder = passwordEncoder; // Initializing the PasswordEncoder field
    }

    // Method to save the JWT token to the database
    public void saveTokenToDatabase(String token) {
        JwtToken jwtToken = new JwtToken(); // Creating a new JwtToken object
        jwtToken.setToken(token); // Setting the token for the JwtToken

        // Set the expiration date to 1 minute from the current time
        Calendar calendar = Calendar.getInstance(); // Creating a new Calendar instance
        calendar.add(Calendar.MINUTE, 5); // Adding 5 minutes to the current time
        Date expirationDate = calendar.getTime(); // Obtaining the expiration date
        jwtToken.setExpirationDate(expirationDate); // Setting the expiration date for the JwtToken

        jwtTokenRepository.save(jwtToken); // Saving the JwtToken to the JwtTokenRepository
    }

    // Method to retrieve a JWT token for the provided ClientModel
    public String retrieveJwt(ClientModel client) {
        // Convert the Long type to a String type
        String clientId = String.valueOf(client.getClientId()); // Converting the client ID to a String
        Optional<JwtToken> jwtTokenOptional = jwtTokenRepository.findById(clientId); // Fetching the JwtToken by ID
        return jwtTokenOptional.map(JwtToken::getToken).orElse(null); // Returning the token if present, or null otherwise
    }
}
