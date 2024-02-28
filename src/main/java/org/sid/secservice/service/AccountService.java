package org.sid.secservice.service;

import java.util.List;

import org.sid.secservice.entities.AppRole;
import org.sid.secservice.entities.AppUser;

public interface AccountService {
	 AppUser addNewUser(AppUser appUser);
	 AppRole addNewRole (AppRole appRole);
	 void addRoleToUser (String username,String rolename);
	 AppUser loadUserByUsername(String username);
	 List <AppUser> listUsers();
	 List <AppRole> listRoles();
}
