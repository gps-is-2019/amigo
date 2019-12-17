package it.unisa.Amigo.autenticazione.dao;

import it.unisa.Amigo.autenticazione.domanin.User;
import org.springframework.data.repository.CrudRepository;

public interface UserDAO extends CrudRepository <User,Long> {
    //@Query("select u from User u left join fetch u.roles where u.email = :email")
    User findByEmail(String email);

}