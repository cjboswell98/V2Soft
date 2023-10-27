package com.product.rating.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// Configuration class for Cross-Origin Resource Sharing (CORS)

@Configuration
public class CorsConfig {

    // Bean creation for configuring CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Creating a source for URL-based CORS configuration
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Creating a new CORS configuration
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Set whether the browser should include any cookies associated with the domain
        config.addAllowedOriginPattern("*"); // Specify the allowed origin patterns for requests
        config.addAllowedHeader("*"); // Allow all headers in the request
        config.addAllowedMethod("*"); // Allow all HTTP methods for the request
        source.registerCorsConfiguration("/**", config); // Register the CORS configuration for all paths
        return source; // Return the configured CORS configuration source
    }
}
