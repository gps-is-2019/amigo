package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GruppoController
{
    @Autowired
    private GruppoService gruppoService;

    @GetMapping("/gruppo/visualizzaMembriSupergruppo={id}")
    public String visualizzaMembriSupergruppo(Model model, @PathVariable(name = "id")int idSupergruppo){
        List<Persona> result =  gruppoService.visualizzaListaMembriSupergruppo(idSupergruppo);
        model.addAttribute("persone" ,result);
        model.addAttribute("idSupergruppo", idSupergruppo);
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        model.addAttribute("idConsiglio", gruppoService.findConsiglioBySupergruppo(idSupergruppo).getId());
        return "gruppo/paginaVisualizzaMembri";
    }

    @GetMapping("/gruppo/visualizzaMembriConsiglio={id}")
    public String visualizzaMembriConsiglio(Model model, @PathVariable(name = "id")int idConsiglio){
        List<Persona> result =  gruppoService.visualizzaListaMembriConsiglioDidattico(idConsiglio);
        model.addAttribute("persone" ,result);
        return "gruppo/paginaVisualizzaMembri";
    }

    @GetMapping("/gruppo/visualizzaMembriDipartimento={id}")
    public String visualizzaMembriDipartimento(Model model, @PathVariable(name = "id")int idDipartimento){
        List<Persona> result =  gruppoService.visualizzaListaMembriDipartimento(idDipartimento);
        model.addAttribute("persone" ,result);
        return "gruppo/paginaVisualizzaMembri";
    }


    @GetMapping("/gruppo/visualizzaGruppi={idPersona}")
    public String visualizzaGruppi(Model model, @PathVariable("idPersona")  int idPersona){
        model.addAttribute("supergruppi", gruppoService.visualizzaSupergruppi(idPersona));
        model.addAttribute("consigli",gruppoService.visualizzaConsigliDidattici(idPersona));
        model.addAttribute("dipartimenti", gruppoService.visualizzaDipartimenti(idPersona));
        return "gruppo/paginaIMieiGruppi";
    }

    @GetMapping("/gruppo/id={id}&supergruppo={id2}")
    public String findAllMembriInConsiglioDidatticoNoSupergruppo(@PathVariable(name = "id") int id,@PathVariable(name = "id2") int id2, Model model){
        List<Persona> persone = new ArrayList<>();
        persone = gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(id,id2);
        model.addAttribute("persone",persone);
        model.addAttribute("idSupergruppo",id2);
        return "gruppo/aggiunta-membro";
    }
    @GetMapping("/gruppo/aggiungi/id={id}&supergruppo={id2}")
    public String addMembro(@PathVariable(name = "id") int id,@PathVariable(name = "id2") int id2, Model model){
        Persona persona = gruppoService.findPersona(id);
        Supergruppo supergruppo = gruppoService.findSupergruppo(id2);
        gruppoService.addMembro(persona,supergruppo);
        return "gruppo/aggiungi";
    }
    @GetMapping("/gruppo/supergruppo={id}")
    public String findAllMembriInSupergruppo(@PathVariable(name = "id") int id, Model model) {
        List<Persona> persone = new ArrayList<>();
        persone = gruppoService.visualizzaListaMembriSupergruppo(id);
        model.addAttribute("persone", persone);
        model.addAttribute("idSupergruppo", id);
        return "gruppo/rimozione-membro";
    }

    @GetMapping("/gruppo/rimuovi/id={id}&supergruppo={id2}")
    public String removeMembro(@PathVariable(name = "id") int id,@PathVariable(name = "id2") int id2, Model model){
        Persona persona = gruppoService.findPersona(id);
        Supergruppo supergruppo = gruppoService.findSupergruppo(id2);
        gruppoService.removeMembro(persona,supergruppo);
        return "gruppo/rimuovi";
    }
}
