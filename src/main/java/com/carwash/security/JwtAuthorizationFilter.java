
package com.carwash.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.carwash.entity.User;
import com.carwash.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter{

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserRepository userRepository;
	
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
//		String path=request.getServletPath();
//		if(path.startsWith("/auth")) {
//			filterChain.doFilter(request,response);
//			return;
//		}
		String authHeader = request.getHeader("Authorization");
		if(authHeader != null && authHeader.startsWith("Bearer")) {
			String token=authHeader.substring(7);
			String email = jwtUtil.extractUsername(token);
			
			if(email !=null && SecurityContextHolder.getContext().getAuthentication()==null) {
				User user = userRepository.findByEmail(email).orElse(null);
				
						if(user!=null &&jwtUtil.validateToken(token, user)) {
						     UsernamePasswordAuthenticationToken authentication = 
						    		 new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name()))	
						
						);
						     SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
	}
		filterChain.doFilter(request, response);
	}
}
