package org.sid.secservice;

import java.util.ArrayList;

import org.sid.secservice.entities.AppRole;
import org.sid.secservice.entities.AppUser;
import org.sid.secservice.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true ,securedEnabled=true )
public class ProjetSecuriteSpringBoot2Application {

	public static void main(String[] args) {
		SpringApplication.run(ProjetSecuriteSpringBoot2Application.class, args);
	}
	@Bean
	PasswordEncoder passwordEncoder() {
		return new  BCryptPasswordEncoder();
	}
	@Bean
	CommandLineRunner start(AccountService accountService) {
		return args -> {
			accountService.addNewRole(new AppRole(null,"USER"));
			accountService.addNewRole(new AppRole(null,"ADMIN"));
			accountService.addNewUser(new AppUser(null,"user1","1234",new ArrayList<>()));
			accountService.addNewUser(new AppUser(null,"user2","1234",new ArrayList<>()));
			accountService.addNewUser(new AppUser(null,"user3","1234",new ArrayList<>()));
			accountService.addNewUser(new AppUser(null,"user4","1234",new ArrayList<>()));
			accountService.addRoleToUser("user1","USER");
			accountService.addRoleToUser("user1","ADMIN");
			accountService.addRoleToUser("user2","ADMIN");
			accountService.addRoleToUser("user3","USER");
			accountService.addRoleToUser("user4","ADMIN");
			
		
	};

	}
}

