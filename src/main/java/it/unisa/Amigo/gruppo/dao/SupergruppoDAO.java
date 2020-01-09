package it.unisa.Amigo.gruppo.dao;

import com.sun.xml.bind.v2.model.core.ID;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Questa interfaccia si occupa di fornire un accesso astratto  all'oggetto di dominio "Supergruppo"
 */
@Repository
public interface SupergruppoDAO extends CrudRepository<Supergruppo, Integer> {
    List<Supergruppo> findAllByPersone_id(Integer idPersona);
}
