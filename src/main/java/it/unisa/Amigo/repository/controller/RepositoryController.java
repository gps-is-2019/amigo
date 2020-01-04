package it.unisa.Amigo.repository.controller;

import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.repository.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;


@Controller
public class RepositoryController {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private GruppoService gruppoService;

    /**
     * Permette di verificare se l'utente il Responsabile del PQA.
     *
     * @return true se l'utente è il Responsabile del PQA.
     */

    private boolean isResponsabilePQA() {
        Set<Role> ruoli = gruppoService.getAuthenticatedUser().getUser().getRoles();
        for (Role ruolo : ruoli) {
            if (ruolo.getName().equals(Role.PQA_ROLE))
                return true;
        }
        return false;
    }

    /**
     * Permette la ricerca di un documento @{@Documento} nella repository.
     *
     * @param model per salvare le informazioni da recuperare nell'html.
     * @param name  nome del documento da cercare.
     * @return il path della pagina su cui eseguire il redirect.
     */

    //show form
    @GetMapping("/repository")
    public String repository(Model model, @RequestParam(required = false) String name) {
        if (isResponsabilePQA()) {
            model.addAttribute("flagPQA", 1);
        }
        List<Documento> documenti = repositoryService.serarchDcoumentInRepository(name);
        model.addAttribute("documenti", documenti);
        return "repository/repository";
    }

    /**
     * Permette di caricare il documento @{@Documento} nella repository.
     *
     * @param model per salvare le informazioni da recuperare nell'html.
     * @return il path della pagina su cui eseguire il redirect.
     */

    @GetMapping("/repository/uploadDocumento")
    public String uploadDocumento(Model model) {
        if (gruppoService.getAuthenticatedUser() == null)
            return "redirect:/";
        else if (!isResponsabilePQA())
            return "dashboard";
        return "repository/aggiunta_documento_repository";
    }

    /**
     * Permette di caricare il documento @{@Documento} nella repository.
     *
     * @param model per salvare le informazioni da recuperare nell'html.
     * @param file  file da caricare.
     * @return il path della pagina su cui eseguire il redirect
     */

    //submit form
    @PostMapping("/repository/uploadDocumento")
    public String uploadDocumento(Model model, @RequestParam("file") MultipartFile file) {
        model.addAttribute("flagAggiunta", repositoryService.addDocumentoInRepository(file));
        model.addAttribute("documentoNome", file.getOriginalFilename());
        return "repository/aggiunta_documento_repository";
    }

    /**
     * Permette di scaricare un documento @{@Documento} dalla repository.
     *
     * @param model      per salvare le informazioni da recuperare nell'html.
     * @param idDocument id del documento da scaricare.
     * @return documento.
     */

    @GetMapping("/repository/{idDocument}")
    public ResponseEntity<Resource> downloadDocumento(Model model, @PathVariable("idDocument") int idDocument) {
        return repositoryService.downloadDocumento(idDocument);
    }
}