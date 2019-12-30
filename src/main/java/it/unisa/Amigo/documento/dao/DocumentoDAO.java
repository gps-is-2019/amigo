package it.unisa.Amigo.documento.dao;

import it.unisa.Amigo.documento.domain.Documento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Questa interfaccia si occupa di fornire un accesso astratto  all'oggetto di dominio "Documento"
 */
@Repository
public interface DocumentoDAO extends CrudRepository<Documento, Integer> {
    List<Documento> findAllByNomeContains(String nameDocumento);
}