package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonaDAO extends CrudRepository<Persona, Integer> {

    List<Persona> findBySupergruppo_id(int idSupergruppo);
}

