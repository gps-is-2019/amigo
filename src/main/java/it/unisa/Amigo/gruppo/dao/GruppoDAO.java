package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.Gruppo;
import org.springframework.data.repository.CrudRepository;

public interface GruppoDAO extends CrudRepository<Gruppo, Integer> {
    Gruppo findById(int idGruppo);
}
