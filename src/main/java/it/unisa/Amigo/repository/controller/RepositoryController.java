package it.unisa.Amigo.repository.controller;

import it.unisa.Amigo.autenticazione.domain.Role;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.repository.services.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RepositoryController {

    private final RepositoryService repositoryService;
    private final GruppoService gruppoService;

    private static final int MIN_SIZE_FILE = 0;
    private static final int MAX_SIZE_FILE = 10485760;

    private boolean checkFile(final MultipartFile file) {
        return file.getSize() != MIN_SIZE_FILE && file.getSize() <= MAX_SIZE_FILE;
    }

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
        if (file.getOriginalFilename().contains(".rar")) {
            return true;
        }
        return false;
    }

    /**
     * Permette di verificare se l'utente il Responsabile del PQA.
     *
     * @return true se l'utente è il Responsabile del PQA.
     */
    private boolean isResponsabilePQA() {
        return gruppoService.findAllRoleOfPersona(gruppoService.getAuthenticatedUser().getId()).contains(Role.PQA_ROLE);
    }

    /**
     * Permette la ricerca di un documento @{@Link Documento} nella repository.
     *
     * @param model per salvare le informazioni da recuperare nell'html.
     * @param name  nome del documento da cercare.
     * @return il path della pagina su cui eseguire il redirect.
     */
    @GetMapping("/repository")
    public String getRepository(final Model model, @RequestParam(required = false) final String name) {
        if (isResponsabilePQA()) {
            model.addAttribute("flagPQA", 1);
        }
        List<Documento> documenti = repositoryService.searchDocumentInRepository(name);
        model.addAttribute("documenti", documenti);
        return "repository/repository";
    }

    /**
     * Permette di caricare il documento @{@Link Documento} nella repository.
     *
     * @return il path della pagina su cui eseguire il redirect.
     */
    @GetMapping("/repository/uploadDocumento")
    public String uploadDocumento() {
        if (gruppoService.getAuthenticatedUser() == null) {
            return "redirect:/";
        } else if (!isResponsabilePQA()) {
            return "redirect:/dashboard";
        }
        return "repository/aggiunta_documento_repository";
    }

    /**
     * Permette di caricare il documento @{@Link Documento} nella repository.
     *
     * @param model per salvare le informazioni da recuperare nell'html.
     * @param file  file da caricare.
     * @return il path della pagina su cui eseguire il redirect
     */
    @PostMapping("/repository/uploadDocumento")
    public String uploadDocumento(final Model model, @RequestParam("file") final MultipartFile file) {
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
        model.addAttribute("flagPQA", 1);
        List<Documento> documenti = repositoryService.searchDocumentInRepository("");
        model.addAttribute("documenti", documenti);
        return "repository/repository";
    }

    /**
     * Permette di scaricare un documento @{@Link Documento} dalla repository.
     *
     * @param idDocument id del documento da scaricare.
     * @return documento.
     */
    @GetMapping("/repository/{idDocument}")
    public ResponseEntity<Resource> downloadDocumento(@PathVariable("idDocument") final int idDocument) {

        Documento documento = repositoryService.findDocumentoById(idDocument);
        Resource resource = repositoryService.downloadDocumento(documento);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documento.getFormat()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + documento.getNome() + "\"")
                .body(resource);
    }
}
