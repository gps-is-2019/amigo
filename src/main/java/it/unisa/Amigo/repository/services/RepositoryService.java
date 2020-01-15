package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.domain.Documento;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * Questa interfaccia definisce i metodi  per la logica di Business del sottositema "Repository"
 */

public interface RepositoryService {
    boolean addDocumentoInRepository(String fileName, byte[] bytes, String mimeType);
    Resource getDocumentoAsResource(Documento documento);
    List<Documento> searchDocumentInRepository(String nameDocumento);
    Documento findDocumentoById(int idDocumento);
    boolean isPQA();
}
