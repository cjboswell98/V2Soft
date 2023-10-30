package com.product.rating.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Class for JWT authentication filter that extends UsernamePasswordAuthenticationFilter

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // Configuration for JWT
    private final JwtConfig jwtConfig;

    // Injecting the secret key from application properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Constructor for JwtAuthenticationFilter
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig) {
        this.setAuthenticationManager(authenticationManager);
        this.jwtConfig = jwtConfig;
    }

    // Attempting authentication based on the JWT token
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // Retrieve the authorization header
        String authorizationHeader = request.getHeader(jwtConfig.getHeader());

        if (authorizationHeader != null && authorizationHeader.startsWith(jwtConfig.getTokenPrefix())) {
            // Extract the token from the header
            String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "").trim();

            try {
                // Parse the token and extract its claims
                Claims claims = Jwts.parser()
                        .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                        .parseClaimsJws(token)
                        .getBody();

                // Define the user's authorities
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // Add the appropriate roles

                // Extract the username from the claims
                String username = claims.getSubject();

                // If the username exists, create and return an authentication token
                if (username != null) {
                    return new UsernamePasswordAuthenticationToken(username, null, authorities);
                }
            } catch (Exception e) {
                // Handle token validation exception
            }
        }

        return null; // Return null if authentication fails
    }

    // Handling successful authentication
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        UserDetails principal = (UserDetails) authResult.getPrincipal();

        // Generate a secure key for HS256

        // Create a new JWT token with the user's details and authorities
        String token = Jwts.builder()
                .setSubject(principal.getUsername())
                .claim("authorities", authResult.getAuthorities())
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))
                .compact();

        // Add the JWT token to the response header
        response.addHeader(jwtConfig.getHeader(), jwtConfig.getTokenPrefix() + token);
    }
}
