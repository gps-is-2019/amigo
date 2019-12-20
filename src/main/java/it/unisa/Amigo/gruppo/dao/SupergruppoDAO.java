package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SupergruppoDAO extends CrudRepository<Supergruppo, Integer> {
    List<Supergruppo> findAllByPersona_id(int id);
}
