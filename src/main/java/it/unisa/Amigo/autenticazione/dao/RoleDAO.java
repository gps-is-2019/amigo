package it.unisa.Amigo.autenticazione.dao;

import it.unisa.Amigo.autenticazione.domanin.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleDAO extends CrudRepository<Role, Integer> {

}
