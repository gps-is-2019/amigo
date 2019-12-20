package it.unisa.Amigo.gruppo.dao;

import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SupergruppoDAO extends CrudRepository<Supergruppo,Integer> {
    Supergruppo findById(int id);



}
