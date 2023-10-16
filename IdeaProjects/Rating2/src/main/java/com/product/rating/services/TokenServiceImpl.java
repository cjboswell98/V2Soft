package com.product.rating.services;

import com.product.rating.domain.Client;

import com.product.rating.domain.Token;
import com.product.rating.repository.TokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");

    final TokenRepository tokenRepository;
    final ClientService clientService;

    public TokenServiceImpl(TokenRepository tokenRepository, ClientService clientService) {
        this.tokenRepository = tokenRepository;
        this.clientService = clientService;
    }

    @Value("${jwt.update.pepper}")
    private String updatePepper;

    @Value("${jwt.delete.pepper}")
    private String deletePepper;

    @Value("${delete.token.collection}")
    private String deleteCollection;

    @Value("${update.token.collection}")
    private String updateCollection;


    @Override
    public ResponseEntity<Object> generateJWT(String tokenFunction, String clientId) {
        Optional<Client> clientOptional = clientService.findClientById(clientId);
        if (clientOptional.isEmpty()) {
            infoAndDebuglogger.debug("Unable to find client with id: " + clientId);
            return new ResponseEntity<>("No client found with that id", HttpStatus.NOT_FOUND);
        }

        Client client = clientOptional.get();

        String tokenSalt = client.getTokenSalt();
        String tokenPepper;
        String collectionName;

        // Determine which pepper & collectionName to use
        switch (tokenFunction.toLowerCase()) {
            case "delete":
                tokenPepper = deletePepper;
                collectionName = deleteCollection;
                break;
            case "update":
                tokenPepper = updatePepper;
                collectionName = updateCollection;
                break;
            default:
                return new ResponseEntity<>("Not a valid token request", HttpStatus.BAD_REQUEST);
        }

        Date expiration = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));

        // Decode tokenSalt and tokenPepper
        byte[] saltBytesDecoded = Base64.getDecoder().decode(tokenSalt);
        byte[] pepperBytesDecoded = Base64.getDecoder().decode(tokenPepper);

        // Concatenate the two byte arrays to create the secret key bytes
        byte[] secretKeyBytes = new byte[saltBytesDecoded.length + pepperBytesDecoded.length];
        System.arraycopy(saltBytesDecoded, 0, secretKeyBytes, 0, saltBytesDecoded.length);
        System.arraycopy(pepperBytesDecoded, 0, secretKeyBytes, saltBytesDecoded.length, pepperBytesDecoded.length);

        // Create the SecretKey using the combined bytes
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        String jwtID = java.util.UUID.randomUUID().toString();
        String jwtString = Jwts.builder()
                .setExpiration(expiration) // Set the expiration time here
                .setSubject(clientId)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

       Token newJwt = new Token(jwtID, clientId, jwtString, expiration);
        tokenRepository.save(newJwt, collectionName);

        return ResponseEntity.ok(newJwt);
    }

    @Override
    public ResponseEntity<Object> retrieveJWT(String tokenFunction, String clientId) {
        Optional<Client> clientOptional = clientService.findClientById(clientId);
        if (clientOptional.isEmpty()) {
            infoAndDebuglogger.debug("Unable to find client with id: " + clientId);
            return new ResponseEntity<>("No client found with that id", HttpStatus.NOT_FOUND);
        }

        //Determine which collectionName to use for searching
        String collectionName;
        switch (tokenFunction.toLowerCase()) {
            case "delete":
                collectionName = deleteCollection;
                break;
            case "update":
                collectionName = updateCollection;
                break;
            default:
                return new ResponseEntity<>(tokenFunction + " is not a proper function. Please try again", HttpStatus.NOT_FOUND);
        }

       Token foundToken = tokenRepository.findByCollectionAndOwnerId(collectionName, clientId);

        if(foundToken != null) {
            return ResponseEntity.ok(foundToken);
        } else {
            infoAndDebuglogger.debug("No unexpired token found, attempting to generate a new one");
            return generateJWT(tokenFunction, clientId);
        }
    }
}