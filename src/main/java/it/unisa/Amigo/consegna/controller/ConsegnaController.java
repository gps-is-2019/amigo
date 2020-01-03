package it.unisa.Amigo.consegna.controller;

import it.unisa.Amigo.consegna.services.ConsegnaService;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ConsegnaController {
    @Autowired
    private ConsegnaService consegnaService;

    @Autowired
    private GruppoService gruppoService;

    @GetMapping("/consegna/{ruolo}")
    public String repository(Model model, @PathVariable("ruolo") String ruolo) {
        model.addAttribute("destinatari", gruppoService.findAllByRuolo(ruolo));
        return "consegna/consegna-invio";
    }

    @PostMapping("/consegna/{idDestinatario}")
    public String sendDocumento(Model model, @RequestParam("file") MultipartFile file, @PathVariable("idDestinatario") int idDestinatario) {
        consegnaService.sendDocumento(idDestinatario, file);
        model.addAttribute("flagInviato", 1);
        model.addAttribute("documentoNome", file.getOriginalFilename());
        return "consegna/consegna-invio";
    }

}
