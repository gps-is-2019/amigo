package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaDAO extends CrudRepository<Persona, Integer> {
    List<Persona> findBySupergruppo_id(int idSupergruppo);
    List<Persona> findByDipartimento_id(int idDipartimento);
    List<Persona> findByConsigli_id(int idConsiglioDidattico);
}

