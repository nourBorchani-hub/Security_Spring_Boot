package org.sid.secservice.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sid.secservice.entities.AppRole;
import org.sid.secservice.entities.AppUser;
import org.sid.secservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@RestController
public class AccountRestController {
	
	private AccountService accountService ;

	public AccountRestController(AccountService accountService) {
		this.accountService=accountService;
	}
	@GetMapping(path ="users")
	@PostAuthorize("hasAuthority('USER')")
 public  List<AppUser> getallUSers(){
	 return accountService.listUsers();
	 
 }
	@PostMapping(path ="users")
	@PostAuthorize("hasAuthority('ADMIN')")
	public AppUser saveUser (@RequestBody AppUser appUser) {
		return accountService.addNewUser(appUser);
	}
	@GetMapping(path ="roles")
 public  List<AppRole> gettAllrole(){
	 return accountService.listRoles();
	 
 }
	@PostMapping(path ="roles")
	public AppRole saveRole (@RequestBody AppRole appRole) {
		return accountService.addNewRole(appRole);
	}
	@PostMapping(path ="addRoleToUser")
	public void addRoleToUser (@RequestBody RoleUserForm roleUserForm) {

 accountService.addRoleToUser(roleUserForm.getUsername(),roleUserForm.getRoleName());
	}
	@GetMapping(path ="/refreshToken")
	public void refreshToken( HttpServletRequest request,HttpServletResponse response)  throws Exception{
		String authToken=request.getHeader("Authorization");
		if(authToken !=null && authToken .startsWith("Bearer")) {
			try {
				String jwt=authToken.substring(7); // apres 7 caactere de la chaine il'ya jwt valable
				Algorithm algorithm =Algorithm.HMAC256("mySecret1234");//decodé le signature
				JWTVerifier jwtVerifier =JWT.require(algorithm).build();//clé privé
				DecodedJWT decodedJWt=jwtVerifier.verify(jwt);//récupere le jwt
				String username=decodedJWt.getSubject();
				AppUser appUser=accountService.loadUserByUsername(username);//vérifier le black list
				String jwtAccessToken=JWT.create()
						.withSubject(appUser.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis()+5*60*1000))
						.withIssuer(request.getRequestURI().toString())
						.withClaim("roles",appUser.getAppRoles().stream().map(r->r.getRoleName()).collect(Collectors.toList()))
						.sign(algorithm);
				Map<String,String> idToken=new HashMap<>();
				idToken.put("accessToken", jwtAccessToken);
				idToken.put("refreshToken", jwt);
				response.setContentType("application/json");
				
				new  ObjectMapper().writeValue(response.getOutputStream(),idToken);
				
			} catch (Exception e) {
				response.setHeader("errorMessage", e.getMessage());
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
		}
		else {
			throw new RuntimeException("RefreshToken Raquired !");
		}
		
	}
}
@Data 
class RoleUserForm{// class interne
	private String  username;
	private String roleName;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
}

