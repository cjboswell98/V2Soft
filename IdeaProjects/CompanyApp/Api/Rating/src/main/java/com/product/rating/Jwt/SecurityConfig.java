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

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource; // Inject the CorsConfigurationSource

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), jwtConfig);
        authenticationFilter.setFilterProcessesUrl("/login");

        http.cors().configurationSource(corsConfigurationSource) // Set the CorsConfigurationSource
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/authenticate").permitAll()// Allow unauthenticated access
                .antMatchers("/reviews/deleteReview/**").authenticated() // Secure the "delete by ID" endpoint
                .antMatchers("/reviews/updateReview/**").authenticated()// Allow unauthenticated access to all other endpoints
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class) // Add this line
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class); // If needed
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
