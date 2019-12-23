package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Questa interfaccia si occupa di fornire un accesso astratto  all'oggetto di dominio "ConsiglioDidattico"
 */
@Repository
public interface ConsiglioDidatticoDAO extends CrudRepository<ConsiglioDidattico, Integer> {
    List<ConsiglioDidattico> findAllByPersone_id(int idPersona);
    ConsiglioDidattico findById(int idConsiglioDidattico);
    ConsiglioDidattico findBySupergruppo_id(int idSupergruppo);
}