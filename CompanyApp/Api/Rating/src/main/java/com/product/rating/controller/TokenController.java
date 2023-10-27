package com.product.rating.controller;
// Import necessary packages and libraries
import com.product.rating.services.ClientVerificationDTO;
import com.product.rating.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jwt")
public class TokenController {

    // Service dependency for token handling
    private final TokenService tokenService;

    // Constructor for dependency injection
    @Autowired
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    // REST API endpoint to generate a JWT token
    @PostMapping("/generate/{tokenFunction}")
    public ResponseEntity<Object> generateJwt(@PathVariable String tokenFunction, @RequestBody String clientId) {
        return tokenService.generateJWT(tokenFunction, clientId);
    }

    // REST API endpoint to retrieve a JWT token with client verification
    @PostMapping("/retrieve/{tokenFunction}")
    @PreAuthorize("@authorizationServiceImpl.verifyClient(#clientVerificationDTO.clientId, #clientVerificationDTO.clientSecret)")
    public ResponseEntity<Object> retrieveJwt(@PathVariable String tokenFunction, @RequestBody ClientVerificationDTO clientVerificationDTO) {
        return tokenService.retrieveJWT(tokenFunction, clientVerificationDTO.getClientId());
    }
}
