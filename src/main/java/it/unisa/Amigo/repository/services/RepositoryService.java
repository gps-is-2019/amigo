package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.domain.Documento;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RepositoryService {
    boolean addDocumentoInRepository(MultipartFile file);
    Resource downloadDocumento(Documento documento);
    List<Documento> searchDocumentInRepository(String nameDocumento);
    Documento findDocumentoById(int idDocumento);

}
