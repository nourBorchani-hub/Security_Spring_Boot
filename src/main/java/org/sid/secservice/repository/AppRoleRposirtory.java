package org.sid.secservice.repository;

import org.sid.secservice.entities.AppRole;
import org.sid.secservice.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRposirtory extends JpaRepository<AppRole, Long>{
	AppRole findByRoleName(String roleName);
}
