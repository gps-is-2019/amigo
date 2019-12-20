package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Dipartimento;
import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.data.repository.CrudRepository;

public interface DipartimentoDAO extends CrudRepository<Dipartimento,Integer> {
    Dipartimento findById(int id);
}
