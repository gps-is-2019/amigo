package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonaDAO extends CrudRepository<Persona,Integer> {
    Persona findById(int id);
    List<Persona> findByConsigliDidattici_id(int id);
    List<Persona> findBySupergruppi_id(int id);
    List<Persona> findByDipartimento_id(int id);
}
