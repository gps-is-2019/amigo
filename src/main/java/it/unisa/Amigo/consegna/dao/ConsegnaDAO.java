package it.unisa.Amigo.consegna.dao;

import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Questa interfaccia si occupa di fornire un accesso astratto  all'oggetto di dominio "Consegna"
 */
@Repository
public interface ConsegnaDAO extends CrudRepository<Consegna, Integer> {
    List<Consegna> findAllByMittente(Persona mittente);

    List<Consegna> findAllByDestinatario(Persona mittente);

    List<Consegna> findAllByLocazione(String locazione);

    Consegna findByDocumento_Id(Integer id);

    Consegna findByDocumento_IdAndDestinatario_Id(Integer idDocumento, Integer idDestinatario);
}
