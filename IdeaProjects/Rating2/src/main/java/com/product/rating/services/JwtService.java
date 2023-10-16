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

@Service
public class JwtService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenRepository jwtTokenRepository;

    @Autowired
    public JwtService(JwtTokenRepository jwtTokenRepository, PasswordEncoder passwordEncoder) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveTokenToDatabase(String token) {
        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(token);

        // Set the expiration date to 1 minute from the current time
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        Date expirationDate = calendar.getTime();
        jwtToken.setExpirationDate(expirationDate);

        jwtTokenRepository.save(jwtToken);
    }



    public String retrieveJwt(ClientModel client) {
        // Convert the Long type to a String type
        String clientId = String.valueOf(client.getClientId());
        Optional<JwtToken> jwtTokenOptional = jwtTokenRepository.findById(clientId);
        return jwtTokenOptional.map(JwtToken::getToken).orElse(null);
    }




}
