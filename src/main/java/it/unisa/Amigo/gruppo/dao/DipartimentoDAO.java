package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.Dipartimento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DipartimentoDAO extends CrudRepository<Dipartimento, Integer> {
}
