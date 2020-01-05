package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.domain.Documento;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RepositoryService {
    boolean addDocumentoInRepository(MultipartFile file);
    ResponseEntity <Resource> downloadDocumento(int idDocumento);
    List<Documento> searchDocumentInRepository(String nameDocumento);

}
