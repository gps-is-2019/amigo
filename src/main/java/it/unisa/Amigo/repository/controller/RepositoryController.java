package it.unisa.Amigo.repository.controller;

import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoService;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.repository.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;
import java.util.Set;


@Controller
public class RepositoryController {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private GruppoService gruppoService;


    private boolean isResponsabilePQA() {
        Set<Role> ruoli = gruppoService.getAuthenticatedUser().getUser().getRoles();
        for (Role ruolo : ruoli){
            if (ruolo.getName().equals(Role.PQA_ROLE))
                return true;
        }
        return false;
    }

    //show form
    @GetMapping("/repository")
    public String repository(Model model, @RequestParam(defaultValue = "") String name) {
        if(isResponsabilePQA()==true)
            model.addAttribute("flagPQA",1);
        List<Documento> documenti = documentoService.searchDocumenti(name); // da fare
        model.addAttribute("documenti", documenti);
        return "repository/repository";
    }

    @GetMapping("/repository/uploadDocumento")
    public String uploadDocumento(Model model) {
        if (gruppoService.getAuthenticatedUser() == null)
            return "redirect:/";
        else if(isResponsabilePQA()== false)
            return "dashboard";
        return "repository/aggiunta_documento_repository";
    }
    //submit form
    @PostMapping("/repository/uploadDocumento")
    public String uploadDocumento(Model model, @RequestParam("file") MultipartFile file) {
        model.addAttribute("flagAggiunta", repositoryService.addDocumentoInRepository(file));
        model.addAttribute("documentoNome", file.getOriginalFilename());
        return "repository/aggiunta_documento_repository";
    }

    @GetMapping("/repository/{idDocument}")
    public ResponseEntity<Resource> downloadDocumento(Model model, @PathVariable("idDocument") int idDocument) {
       return  repositoryService.downloadDocumento(idDocument);
    }
}