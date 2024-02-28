package org.sid.secservice.service;

import java.util.List;

import org.sid.secservice.entities.AppRole;
import org.sid.secservice.entities.AppUser;
import org.sid.secservice.repository.AppRoleRposirtory;
import org.sid.secservice.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountServcieImp implements AccountService {
	
	private AppUserRepository appUserRepo;
	private AppRoleRposirtory appRoleRepo;
	private PasswordEncoder passwordEncoder;
	public AccountServcieImp(AppRoleRposirtory appRoleRepo ,AppUserRepository appUserRepo, PasswordEncoder passwordEncoder) {
		this.appUserRepo=appUserRepo;
		this.appRoleRepo=appRoleRepo;
		this.passwordEncoder= passwordEncoder;
	}
	@Override
	public AppUser addNewUser(AppUser appUser) {
		String pw =appUser.getPassword();
		if(pw!= null) {
			appUser.setPassword(passwordEncoder.encode(pw));	
	}

	
	return appUserRepo.save(appUser);
	}

	@Override
	public AppRole addNewRole(AppRole appRole) {
		
	return appRoleRepo.save(appRole);
	}

	@Override
	public void addRoleToUser(String username, String rolename) {
		  AppUser appUser = appUserRepo.findByUsername(username);
		  AppRole appRole=appRoleRepo.findByRoleName(rolename);
		 if(appUser != null && appRole!=null) 
		 { appUser.getAppRoles().add(appRole);}
		
	}

	@Override
	public AppUser loadUserByUsername(String username) {
		return appUserRepo.findByUsername(username)
;	}

	@Override
	public List<AppUser> listUsers() {
		return appUserRepo.findAll()
;	}
	@Override
	public List<AppRole> listRoles() {
		return appRoleRepo.findAll()
;	}

}
