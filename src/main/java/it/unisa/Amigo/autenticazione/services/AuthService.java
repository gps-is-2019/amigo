package it.unisa.Amigo.autenticazione.services;

import it.unisa.Amigo.autenticazione.domain.Role;
import it.unisa.Amigo.autenticazione.domain.User;

import java.util.Set;

public interface AuthService {

    User getCurrentUser();

    Set<Role> getCurrentUserRoles();

}
