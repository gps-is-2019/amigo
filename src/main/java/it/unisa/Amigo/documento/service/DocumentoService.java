package it.unisa.Amigo.documento.service;

import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.task.domain.Task;

import java.util.List;

public interface DocumentoService {
    boolean addDocToTask(Documento documento, Task task);
    boolean addDocToConsegna(Documento documento, Consegna consegna);
    void addDocToRepository(Documento documento);
    Documento downloadDocumentoFromRepository(int idDocumento);
    Documento downloadDocumentoFromConsegna(int idDocumento);
    Documento downloadDocumentoFromTask(int idDocumento);
    List<Documento> searchDocumentoFromRepository(String nameDocumento);
}
