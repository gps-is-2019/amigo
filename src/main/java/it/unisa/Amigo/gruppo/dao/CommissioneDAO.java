package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.Commissione;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommissioneDAO extends CrudRepository<Commissione, Integer> {
    List<Commissione> findAllByGruppo_id(int idGruppo);
    Commissione findById(int idCommissione);
}
