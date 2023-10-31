// Package declaration indicating the location of the file
package com.product.rating.controller;

// Import necessary classes and packages
import com.product.rating.Jwt.JwtUtil;
import com.product.rating.requestResponse.*;
import com.product.rating.services.JwtService;
import com.product.rating.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
// Annotate the class as a REST controller
@RestController
public class JwtController {

    // Dependency injections
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtTokenUtil;
    @Autowired
    private JwtService jwtUtilService;

    // Define a request mapping for the authentication endpoint
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    // Method for creating authentication with request body as input
    public ResponseEntity<?> createAuthentication(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            // Attempt to authenticate the user using the provided credentials
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            // Throw an exception if the credentials are invalid
            throw new Exception("Incorrect username or password", e);
        }
        // Retrieve user details for the authenticated user
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        // Generate a JSON Web Token for the authenticated user
        final String jwt = jwtTokenUtil.generateToken(userDetails);

        // Save the generated token to the database using JwtUtilService
        jwtUtilService.saveTokenToDatabase(jwt);

        // Return the JWT token as part of the response
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
