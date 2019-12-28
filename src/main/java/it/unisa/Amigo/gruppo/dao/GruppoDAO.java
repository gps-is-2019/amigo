package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.Gruppo;
import org.springframework.data.repository.CrudRepository;

/**
 * L'interfaccia si occupa di fornire un accesso astratto all'oggetto di dominio "Gruppo"
 */
public interface GruppoDAO extends CrudRepository<Gruppo, Integer> {
    Gruppo findById(int idGruppo);
    Gruppo findByCommissioni_id(int idCommissione);
}
