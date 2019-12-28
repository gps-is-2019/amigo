package it.unisa.Amigo.documento.service;

import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.task.domain.Task;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentoService {
    boolean addDocToTask(MultipartFile file, Task task);
    boolean addDocToConsegna(MultipartFile file, Consegna consegna);
    void addDocToRepository(MultipartFile documento);
    Documento downloadDocumentoFromRepository(int idDocumento);
    Documento downloadDocumentoFromConsegna(int idDocumento);
    Documento downloadDocumentoFromTask(int idDocumento);
    List<Documento> searchDocumentoFromRepository(String nameDocumento);
    public Resource loadAsResource(Documento documento);
}
