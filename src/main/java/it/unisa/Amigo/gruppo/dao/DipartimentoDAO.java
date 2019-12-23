package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.Dipartimento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Questa interfaccia si occupa di fornire un accesso astratto  all'oggetto di dominio "Dipartimneto"
 */
@Repository
public interface DipartimentoDAO extends CrudRepository<Dipartimento, Integer> {
    List<Dipartimento> findAllByPersone_id(int idPersona);
    Dipartimento findById(int idDipartimento);
}
