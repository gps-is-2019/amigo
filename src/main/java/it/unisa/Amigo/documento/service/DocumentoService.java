package it.unisa.Amigo.documento.service;

import it.unisa.Amigo.documento.domain.Documento;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DocumentoService {
    Documento addDocumento(String fileName, byte[] bytes, String mimeType);
    Documento updateDocumento(Documento documento);
    Documento findDocumentoById(Integer idDocumento);
    List<Documento> searchDocumenti(Documento example);
    Resource loadAsResource(Documento documento);
    //TODO comunicare l'agginta della funzione per visualizzare lista documenti approvati
    List<Documento> approvedDocuments(int idSupergruppo);
}

