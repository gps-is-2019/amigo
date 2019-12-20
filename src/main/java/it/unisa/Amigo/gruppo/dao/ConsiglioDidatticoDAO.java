package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConsiglioDidatticoDAO extends CrudRepository<ConsiglioDidattico,Integer> {
    ConsiglioDidattico findById(int id);
}
