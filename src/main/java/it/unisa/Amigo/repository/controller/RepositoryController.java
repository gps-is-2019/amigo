package it.unisa.Amigo.repository.controller;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.repository.services.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Questa classe si occupa della logica di controllo del sottosistema Repository
 */

@Controller
@RequiredArgsConstructor
public class RepositoryController {

    private final RepositoryService repositoryService;

    private static final int MIN_SIZE_FILE = 0;
    private static final int MAX_SIZE_FILE = 10485760;

    /**
     * Controlla se il file rispetta i limiti di dimensione imposti dal sistema
     *
     * @param file da caricare
     * @return true se il file rispetta i limiti, false altrimenti
     */
    private boolean checkFile(final MultipartFile file) {
        return file.getSize() != MIN_SIZE_FILE && file.getSize() <= MAX_SIZE_FILE;
    }


    /**
     * Controlla se il formato del file rispetta i vincoli del sistema
     *
     * @param file da caricare
     * @return true se il file rispetta i vincoli di formato, false altrimenti
     */
    private boolean formatoFile(final MultipartFile file) {
        if (file.getOriginalFilename().contains(".pdf")) {
            return true;
        }
        if (file.getOriginalFilename().contains(".zip")) {
            return true;
        }
        if (file.getOriginalFilename().contains(".txt")) {
            return true;
        }
        return file.getOriginalFilename().contains(".rar");
    }

    /**
     * Permette la ricerca di un documento @{@link Documento} nella repository.
     *
     * @param model per salvare le informazioni da recuperare nell'html.
     * @param name  nome del documento da cercare.
     * @return il path della pagina su cui eseguire il redirect.
     */
    @GetMapping("/repository")
    public String getRepository(final Model model, @RequestParam(required = false) final String name) {

        model.addAttribute("flagPQA", repositoryService.isPQA());
        List<Documento> documenti = repositoryService.searchDocumentInRepository(name);
        model.addAttribute("documenti", documenti);
        return "repository/repository";
    }

    /**
     * Permette di caricare il documento @{@link Documento} nella repository.
     *
     * @return il path della pagina su cui eseguire il redirect.
     */
    @GetMapping("/repository/uploadDocumento")
    public String uploadDocumento() {
        return "repository/aggiunta_documento_repository";
    }

    /**
     * Permette di caricare il documento @{@link Documento} nella repository.
     *
     * @param model per salvare le informazioni da recuperare nell'html.
     * @param file  file da caricare.
     * @return il path della pagina su cui eseguire il redirect
     */
    @PostMapping("/repository/uploadDocumento")
    public String uploadDocumento(final Model model, @RequestPart("file") final MultipartFile file) {
        boolean addFlag = false;
        if (checkFile(file) && formatoFile(file)) {
            try {
                addFlag = repositoryService.addDocumentoInRepository(file.getOriginalFilename(), file.getBytes(), file.getContentType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("addFlag", addFlag);
        if (addFlag) {
            model.addAttribute("documentoNome", file.getOriginalFilename());
        } else {
            if (!checkFile(file)) {
                model.addAttribute("errorMessage", "Dimensioni del file non supportate");
            } else {
                model.addAttribute("errorMessage", "Formato del file non supportato");
            }
        }
        model.addAttribute("flagPQA", true);
        List<Documento> documenti = repositoryService.searchDocumentInRepository("");
        model.addAttribute("documenti", documenti);
        return "repository/repository";
    }

    /**
     * Ottiene il file relativo ad un documento @{@link Documento} della repository
     *
     * @param idDocument documento da cui prelevare il file
     * @return response di tipo resource contenente una risorsa al file richiesto
     */
    @GetMapping("/documento/{idDocument}")
    public ResponseEntity<Resource> downloadDocumento(@PathVariable("idDocument") final int idDocument) {
        Documento documento = repositoryService.findDocumentoById(idDocument);
        if (documento == null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "https://i.makeagif.com/media/6-18-2016/i4va3h.gif");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
        Resource resource = repositoryService.getDocumentoAsResource(documento);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documento.getFormat()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + documento.getNome() + "\"")
                .body(resource);
    }

}
