package com.product.rating.Jwt;

import com.product.rating.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// Configuration class for defining security configurations
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // Autowired user details service
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    // Autowired JwtRequestFilter
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    // Autowired JwtConfig
    @Autowired
    private JwtConfig jwtConfig;

    // Autowired CorsConfigurationSource for handling CORS
    @Autowired
    private CorsConfigurationSource corsConfigurationSource; // Inject the CorsConfigurationSource

    // Method to configure HTTP security settings
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Create JwtAuthenticationFilter for handling authentication
        JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), jwtConfig);
        authenticationFilter.setFilterProcessesUrl("/login");

        http.cors().configurationSource(corsConfigurationSource) // Set the CorsConfigurationSource
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/authenticate").permitAll()
                .antMatchers("/**").permitAll()// Allow unauthenticated access
                .antMatchers("/reviews/deleteReview/**").permitAll() // Secure the "delete by ID" endpoint
                .antMatchers("/reviews/updateReview/**").authenticated()// Allow unauthenticated access to all other endpoints
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class) // Add this line
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class); // If needed
    }

    // Bean for exposing the AuthenticationManager
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // Bean for configuring the password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // Note: This implementation should not be used in production, consider using BCryptPasswordEncoder or other secure implementations
    }
}
