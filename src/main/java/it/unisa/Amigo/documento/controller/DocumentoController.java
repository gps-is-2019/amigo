package it.unisa.Amigo.documento.controller;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DocumentoController {
    private final DocumentoService documentoService;
    /**
     * Permette di scaricare un documento @{@Link Documento} dalla repository.
     *
     * @param idDocument id del documento da scaricare.
     * @return documento.
     */
    @GetMapping("/documento/{idDocument}")
    public ResponseEntity<Resource> downloadDocumento(@PathVariable("idDocument") final int idDocument) {

        Documento documento = documentoService.findDocumentoById(idDocument);
        Resource resource = documentoService.loadAsResource(documento);
        System.out.println(resource);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documento.getFormat()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + documento.getNome() + "\"")
                .body(resource);
    }
}
