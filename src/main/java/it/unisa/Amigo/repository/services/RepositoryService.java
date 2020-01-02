package it.unisa.Amigo.repository.services;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface RepositoryService {
    boolean addDocumentoInRepository(MultipartFile file);
    ResponseEntity <Resource> downloadDocumento(int idDocumento);
}
