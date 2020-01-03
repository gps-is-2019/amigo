package it.unisa.Amigo.consegna.dao;

import it.unisa.Amigo.consegna.domain.Consegna;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConsegnaDAO extends CrudRepository<Consegna, Integer> {
}
