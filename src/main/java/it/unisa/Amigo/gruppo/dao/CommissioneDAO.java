package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.Commissione;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * L'interfaccia si occupa di fornire un accesso astratto all'oggetto di dominio "Commissione"
 */
public interface CommissioneDAO extends CrudRepository<Commissione, Integer> {
    List<Commissione> findAllByGruppo_id(int idGruppo);
    Commissione findById(int idCommissione);
}
