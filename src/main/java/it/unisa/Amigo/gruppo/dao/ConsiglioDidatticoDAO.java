package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsiglioDidatticoDAO extends CrudRepository<ConsiglioDidattico, Integer> {
}