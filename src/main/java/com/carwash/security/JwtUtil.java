package com.carwash.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.carwash.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final String secret_key="mysecretkeyisthisonemysecretkeyyyyyyyyyyyyyyyyyyy";
	
	private final long jwtExpirationMs = 86400000;
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secret_key.getBytes(StandardCharsets.UTF_8));
	}
	public String generateToken(User user) {
		return Jwts.builder() 
				.setSubject(user.getEmail())
				.claim("userId", user.getUserId())
				.claim("role", user.getRole().name())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()+jwtExpirationMs))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	public String extractUsername(String token) {
		return extractClaims(token).getSubject();
	}
	
	public boolean validateToken(String token, User user) {
		return extractUsername(token).equals(user.getEmail()) && !isTokenExpired(token);
	}
	private boolean isTokenExpired(String token) {
		return extractClaims(token).getExpiration().before(new Date()); 
			
	}
	private Claims extractClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
}
