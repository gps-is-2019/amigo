package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Questa classe implementa i metodi per la logica di Business del sottosistema "Repository"
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RepositoryServiceImpl implements RepositoryService {

    private final DocumentoService documentoService;

    /**
     * Aggiunge un documento @{@link Documento} alla repository.
     *
     * @param file da aggiungere alla repository.
     * @return true se il documento è stato aggiunto alla repository.
     */
    @Override
    public boolean addDocumentoInRepository(MultipartFile file) {
        if (file.getSize() < 10485760) {
            Documento documento = documentoService.addDocumento(file);
            documento.setInRepository(true);
            documentoService.updateDocumento(documento);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Permette il download di un documento @{@Link Documento}.
     *
     * @param idDocument del documento da scaricare.
     * @return documento scaricato.
     */
    @Override
    public ResponseEntity<Resource> downloadDocumento(int idDocument) {
        Documento documento = documentoService.findDocumento(idDocument);
        Resource resource = documentoService.loadAsResource(documento);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documento.getFormat()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + documento.getNome() + "\"")
                .body(resource);
    }

    /**
     * Permette la ricerca di un documento @{@Link Documento} nella repository.
     *
     * @param nameDocumento nome del documento da ricercare.
     * @return lista di documenti il cui nome contiene il nome passato come parametro.
     */

    @Override
    public List<Documento> searchDocumentInRepository(String nameDocumento) {
        Documento documentoExample = new Documento();
        documentoExample.setInRepository(true);
        if (nameDocumento != null) {
            documentoExample.setNome(nameDocumento);
        }
        List<Documento> documenti = documentoService.searchDocumenti(documentoExample);
        return documenti;
    }
}
