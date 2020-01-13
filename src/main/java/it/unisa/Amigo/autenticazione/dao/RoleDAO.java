package it.unisa.Amigo.autenticazione.dao;

import it.unisa.Amigo.autenticazione.domain.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleDAO extends CrudRepository<Role, Integer> {
}
