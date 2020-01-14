package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.domain.Documento;
import org.springframework.core.io.Resource;

import java.util.List;

public interface RepositoryService {
    boolean addDocumentoInRepository(String fileName, byte[] bytes, String mimeType);
    Resource downloadDocumento(Documento documento);

    Resource getDocumentoAsResource(Documento documento);

    List<Documento> searchDocumentInRepository(String nameDocumento);
    Documento findDocumentoById(int idDocumento);

}
