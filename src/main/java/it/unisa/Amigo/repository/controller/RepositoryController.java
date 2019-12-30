package it.unisa.Amigo.repository.controller;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoService;
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


@Controller
public class RepositoryController {
    @Autowired
    private DocumentoService documentoService;

    //show form
    @GetMapping("/repository")
    public String repository(Model model) {
        List<Documento> documenti = documentoService.searchDocumenti("");
        model.addAttribute("documenti", documenti);
        return "repository/repository";
    }

    //submit form
    @PostMapping("/repository/uploadDocumento")
    public String uploadDocumento(Model model, @RequestParam("file") MultipartFile file) {
        documentoService.addDocumento(file);
        model.addAttribute("flagAggiunta", 1); //cambiare
        model.addAttribute("documentoNome", file.getOriginalFilename());
        List<Documento> documenti = documentoService.searchDocumenti("");
        model.addAttribute("documenti", documenti);
        return "repository/repository";
    }

    @GetMapping("/repository/{idDocument}")
    public ResponseEntity<Resource> downloadDocumento(Model model, @PathVariable("idDocument") int idDocument) {
        Documento documento = documentoService.findDocumento(idDocument);
        Resource resource = documentoService.loadAsResource(documento);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documento.getFormat()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + documento.getNome() + "\"")
                .body(resource);
    }
}