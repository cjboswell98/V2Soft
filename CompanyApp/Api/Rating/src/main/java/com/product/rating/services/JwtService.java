package com.product.rating.services;

import com.product.rating.security.JwtUtil;
import com.product.rating.domain.Client;
import com.product.rating.model.JwtRequest;
import com.product.rating.model.JwtResponse;
import com.product.rating.model.JwtToken;
import com.product.rating.repository.ClientRepository;
import com.product.rating.repository.JwtTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service // Indicates that the class is a service component
public class JwtService implements UserDetailsService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private final JwtTokenRepository jwtTokenRepository; // Field to store the JwtTokenRepository instance

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired // Annotation for constructor-based dependency injection
    public JwtService(JwtTokenRepository jwtTokenRepository) {
        this.jwtTokenRepository = jwtTokenRepository; // Initializing the JwtTokenRepository field

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client client = clientRepository.findById(username).get();

        if (client != null) {
            return new User(
                    client.getUsername(),
                    client.getPassword(),
                    getAuthorities(client)
            );
        } else {
            throw new UsernameNotFoundException("Username is not valid");
        }
    }

    // Method to create a token with specific claims and subject
    public JwtResponse createJwtToken(JwtRequest jwtRequest) throws Exception {
        String username = jwtRequest.getUsername();
        String password = jwtRequest.getPassword();
        authenticate(username,password);

        final UserDetails userDetails = loadUserByUsername(username);

        String newGeneratedToken = jwtUtil.generateToken(userDetails);

        Client client = clientRepository.findById(username).get();

        return new JwtResponse(newGeneratedToken);
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
    public void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("User is disabled");
        } catch (BadCredentialsException e) {
            throw new Exception("Bad credentials from user");
        }
    }

    private Set getAuthorities(Client client) {
        Set authorities = new HashSet();

        client.getRole().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
        });

        return authorities;
    }
}
