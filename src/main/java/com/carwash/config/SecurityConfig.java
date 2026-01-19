package com.carwash.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.carwash.security.JwtAuthorizationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//		return http.build();
//	}

	@Autowired
	private JwtAuthorizationFilter jwtAuthorizationFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http.csrf(csrf -> csrf.disable())
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth 
        .requestMatchers("/api/users/register","/api/users/admin/register","/api/users/service-provider/register").permitAll()
        .requestMatchers("/auth/**").permitAll()
        .requestMatchers("/customers/**").permitAll()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .requestMatchers("/service-provider/**").hasAnyRole("ADMIN","SERVICE_PROVIDER")
        .requestMatchers("/bookings/**").hasRole("CUSTOMER")
        .requestMatchers("/payments/**").hasRole("CUSTOMER")
        .requestMatchers("/feedbacks/**").hasRole("CUSTOMER")
        .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

}
