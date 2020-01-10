package it.unisa.Amigo.autenticazione.dao;

import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.data.repository.CrudRepository;

public interface UserDAO extends CrudRepository<User, Long> {
    User findByEmail(String email);
    //TODO
    Persona findByPersona_Id(int id);


}
