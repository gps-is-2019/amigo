package it.unisa.Amigo.documento.dao;

import it.unisa.Amigo.documento.domain.Documento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoDAO extends CrudRepository<Documento, Integer> {
    List<Documento> findAllByInRepositoryAndNomeContains(boolean inRepository,String nameDocumento);
    Documento findByIdAndInRepository(int idDocumento, boolean inRepository);
}