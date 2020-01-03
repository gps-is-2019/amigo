package it.unisa.Amigo.consegna.controller;

import it.unisa.Amigo.consegna.services.ConsegnaService;
import it.unisa.Amigo.gruppo.domain.Persona;
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
    public String sendDocumento(Model model, @PathVariable("ruolo") String ruolo) {
        List<Persona> destinatari = gruppoService.findAllByRuolo(ruolo);
        Persona p = new Persona("a", "b", "CPDS");
        destinatari.add(p);
        model.addAttribute("destinatari", destinatari);
        return "consegna/invio-consegna";
    }

    @PostMapping("/consegna")
    public String sendDocumento(Model model, @RequestParam MultipartFile file, @RequestParam String destinatariPost) {
        String[] destinatariString = destinatariPost.split(",");
        int[] destinatariInt = new int[destinatariString.length];
        int i = 0;

        for (String p : destinatariString)
            destinatariInt[i++] = Integer.parseInt(p);

        consegnaService.sendDocumento(destinatariInt, file);

        model.addAttribute("flagInviato", 1);
        model.addAttribute("documentoNome", file.getOriginalFilename());
        model.addAttribute("destinatari", destinatariPost);
        return "consegna/invio-consegna";
    }

    //TODO Aggiungere gente al NdV, PQA. Più ruoli a una persona. Controllo permessi di invio. Aggiornare navbar. Documenti ricevuti/inviati.
}