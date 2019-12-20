package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaDAO extends CrudRepository<Persona, Integer> {
    List<Persona> findBySupergruppi_id(int idSupergruppo);
    List<Persona> findByDipartimenti_id(int idDipartimento);
    List<Persona> findByConsigli_id(int idConsiglioDidattico);
    Persona findByUser_email(String email);
}
