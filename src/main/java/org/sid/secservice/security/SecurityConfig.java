package org.sid.secservice.security;

import java.util.ArrayList;
import java.util.Collection;

import org.sid.secservice.entities.AppUser;
import org.sid.secservice.filters.JWTAuthentificationFilter;
import org.sid.secservice.filters.JwtAuthorizationFilter;
import org.sid.secservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private  AccountService accountService ;
	public SecurityConfig(AccountService accountService ) {
		this.accountService=accountService ; 	}
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.userDetailsService(new UserDetailsService() {
		
		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			AppUser appUser=accountService.loadUserByUsername(username);
			 if (appUser == null) {
		            throw new UsernameNotFoundException("User not found with username: " + username);
		        }
			Collection <GrantedAuthority> authorities =new ArrayList<>();
		
			       appUser.getAppRoles().forEach(r -> {
			            authorities.add(new SimpleGrantedAuthority(r.getRoleName()));
			       });
			
			return new User(appUser.getUsername(), appUser.getPassword(),authorities);
		}
	});
}
@Override
	protected void configure(HttpSecurity http) throws Exception {
	http.headers().frameOptions().disable();
	http.csrf().disable();
	http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//a ce moment j'utilise le authentification statless
//	http.formLogin(); //permet pour l'utilisateur d'authentifierS
	http.authorizeRequests().antMatchers("/h2-console/**","/refreshToken/**","/login/**").permitAll();// toutes les ressources nécessitent une authentification sauf la console H2
	//http.authorizeRequests().antMatchers(HttpMethod.POST,"/users/**").hasAuthority("ADMIN");
//	http.authorizeRequests().antMatchers(HttpMethod.GET,"/users/**").hasAuthority("USER");
	http.authorizeRequests().anyRequest().authenticated();
	
	http.addFilter( new JWTAuthentificationFilter(authenticationManagerBean()));
	http.addFilterBefore(new JwtAuthorizationFilter(),UsernamePasswordAuthenticationFilter.class);
	
}
@Bean
@Override
public AuthenticationManager authenticationManagerBean() throws Exception {
	// TODO Auto-generated method stub
	return super.authenticationManagerBean();
}
}

