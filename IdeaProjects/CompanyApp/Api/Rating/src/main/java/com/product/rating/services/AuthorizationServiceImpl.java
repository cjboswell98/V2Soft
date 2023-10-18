package com.product.rating.services;

import com.product.rating.domain.Client;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Value("${jwt.update.pepper}")
    private String updatePepper;

    @Value("${jwt.delete.pepper}")
    private String deletePepper;

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");

    final ClientService clientService;

    @Autowired
    public AuthorizationServiceImpl(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public boolean verifyToken(String token, String clientId, String tokenFunction) {
        infoAndDebuglogger.info("Reached the verifyToken Method");

        //If no client was found with the matching id, fail the authorization and log
        Optional<Client> clientOptional = clientService.findClientById(clientId);
        if (clientOptional.isEmpty()) {
            infoAndDebuglogger.debug("Unable to find client with id: " + clientId);
            return false;
        }

        Client client = clientOptional.get();
        String tokenSalt = client.getClientId(); // Assuming the clientId is now used as the tokenSalt

        //If the tokenFunction string does not match a value, then it is not a proper request: fail authorization
        String tokenPepper;
        if ("delete".equalsIgnoreCase(tokenFunction)) {
            tokenPepper = deletePepper;
        } else if ("update".equalsIgnoreCase(tokenFunction)) {
            tokenPepper = updatePepper;
        } else {
            infoAndDebuglogger.debug("Invalid tokenFunction provided: " + tokenFunction);
            return false;
        }

        // Decode tokenSalt and tokenPepper
        byte[] saltBytesDecoded = tokenSalt.getBytes();
        byte[] pepperBytesDecoded = Base64.getDecoder().decode(tokenPepper);

        // Concatenate the two byte arrays to create the secret key bytes
        byte[] secretKeyBytes = new byte[saltBytesDecoded.length + pepperBytesDecoded.length];
        System.arraycopy(saltBytesDecoded, 0, secretKeyBytes, 0, saltBytesDecoded.length);
        System.arraycopy(pepperBytesDecoded, 0, secretKeyBytes, saltBytesDecoded.length, pepperBytesDecoded.length);
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        Jws<Claims> jws;
        try {
            jws = Jwts.parser()
                    .setSigningKey(secretKey) // Use setSigningKey instead of verifyWith
                    .parseClaimsJws(token);

            infoAndDebuglogger.info("Jwt verified");
            return true;
        } catch (JwtException ex) {
            infoAndDebuglogger.debug(ex);
            return false;
        }
    }

    @Override
    public boolean verifyClient(String clientId, String password) {
        infoAndDebuglogger.info("Reached the verifyClient Method");

        //If no client was found with the matching id, fail the authorization and log
        Optional<Client> clientOptional = clientService.findClientById(clientId);
        if (clientOptional.isEmpty()) {
            infoAndDebuglogger.debug("Unable to find client with id: " + clientId);
            return false;
        }

        Client client = clientOptional.get();

        //Retrieve the hashed password and check if it matches the password provided during request
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hashedPassword = client.getPassword();

        if(!encoder.matches(password, hashedPassword)) {
            infoAndDebuglogger.debug("Provided password did not match hashed password.");
            return false;
        } else {
            infoAndDebuglogger.info("Client authenticated");
            return true;
        }
    }
}
