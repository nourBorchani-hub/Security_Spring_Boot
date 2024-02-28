package org.sid.secservice.filters;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTAuthentificationFilter extends UsernamePasswordAuthenticationFilter{
	private AuthenticationManager authenticationManager; //fournit par spring Security 
	public JWTAuthentificationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
		
	
	@Override
	public org.springframework.security.core.Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
	
		String username=request.getParameter("username");
		String password =request.getParameter("password");
		System.out.println(username);
		System.out.println(password);
		UsernamePasswordAuthenticationToken authenticationToken =new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authenticationToken); //verifier le mot de passe
	}
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			org.springframework.security.core.Authentication authResult) throws IOException, ServletException {
		System.out.println("susccessAuthentication");
		User user=(User) authResult.getPrincipal();//retourner le user authentifié
		Algorithm algo1=Algorithm.HMAC256("mySecret1234");	//génere le jwt
		String jwtAccessToken=JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+5*60*1000))
				.withIssuer(request.getRequestURI().toString())
				.withClaim("roles",user.getAuthorities().stream().map(ga->ga.getAuthority()).collect(Collectors.toList()))
				.sign(algo1);
		response.setHeader("Authorization",jwtAccessToken); // envoyer cette jwt au client dans header
		
		String jwtRefreshToken=JWT.create()
				.withSubject(user.getUsername()) //Refresh Token valable pour long duré
				.withExpiresAt(new Date(System.currentTimeMillis()+1*60*1000))
				.withIssuer(request.getRequestURI().toString())
				.sign(algo1);
		Map<String,String> idToken=new HashMap<>();
		idToken.put("accessToken", jwtAccessToken);
		idToken.put("refreshToken", jwtRefreshToken);
		response.setContentType("application/json");
		
		new  ObjectMapper().writeValue(response.getOutputStream(),idToken); // inseret les deux tokens dans le body de request 
		
		super.successfulAuthentication(request, response, chain, authResult);
	}
}

