package com.product.rating.controller;

import com.product.rating.services.ClientVerificationDTO;
import com.product.rating.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jwt")
public class TokenController {

    private final TokenService tokenService;

    @Autowired
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/generate/{tokenFunction}")
    public ResponseEntity<Object> generateJwt(@PathVariable String tokenFunction, @RequestBody String clientId){
        return tokenService.generateJWT(tokenFunction, clientId);
    }

    @PostMapping("/retrieve/{tokenFunction}")
    @PreAuthorize("@authorizationServiceImpl.verifyClient(#clientVerificationDTO.clientId, #clientVerificationDTO.clientSecret)")
    public ResponseEntity<Object> retrieveJwt(@PathVariable String tokenFunction, @RequestBody ClientVerificationDTO clientVerificationDTO){
        return tokenService.retrieveJWT(tokenFunction, clientVerificationDTO.getClientId());
    }
}
