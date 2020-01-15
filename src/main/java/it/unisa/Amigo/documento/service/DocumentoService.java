package it.unisa.Amigo.documento.service;

import it.unisa.Amigo.documento.domain.Documento;
import org.springframework.core.io.Resource;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Questa interfaccia definisce i metodi  per la logica di business del sottositema "Documento"
 */

public interface DocumentoService {
    Documento addDocumento(String fileName, byte[] bytes, String mimeType);
    Documento updateDocumento(Documento documento);
    Documento findDocumentoById(Integer idDocumento);
    List<Documento> searchDocumenti(Documento example);
    Resource loadAsResource(Documento documento);
    List<Documento> approvedDocuments(int idSupergruppo);
}

