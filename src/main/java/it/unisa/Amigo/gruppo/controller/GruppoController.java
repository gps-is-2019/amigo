package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class GruppoController {
    @Autowired
    private GruppoService service;

    @GetMapping("/gruppo/id={id}&supergruppo={id2}")
    public String findAllMembriInConsiglioDidatticoNoSupergruppo(@PathVariable(name = "id") int id,@PathVariable(name = "id2") int id2, Model model){
        List<Persona> persone = service.findAllMembriInConsiglioDidatticoNoSupergruppo(id,id2);
        model.addAttribute("persone",persone);
        return "gruppo/aggiunta-membro";
    }
    @GetMapping("/gruppo/aggiungi/id={id}&supergruppo={id2}")
    public String addMembro(@PathVariable(name = "id") int id,@PathVariable(name = "id2") int id2, Model model){
        Persona persona = service.findPersona(id);
        Supergruppo supergruppo = service.findSupergruppo(id2);
        service.addMembro(persona,supergruppo);
        return "gruppo/aggiunta-membro";
    }

}
