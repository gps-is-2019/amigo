package it.unisa.Amigo.consegna.dao;

import it.unisa.Amigo.consegna.domain.Consegna;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ConsegnaDAO extends CrudRepository<Consegna, Integer> {
    List<Consegna> findAllByMittente_Id(int idMittente);
}
