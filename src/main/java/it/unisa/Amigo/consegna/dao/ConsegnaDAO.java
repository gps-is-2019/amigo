package it.unisa.Amigo.consegna.dao;

import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ConsegnaDAO extends CrudRepository<Consegna, Integer> {
    List<Consegna> findAllByMittente(Persona mittente);

    List<Consegna> findAllByDestinatario(Persona mittente);
}
