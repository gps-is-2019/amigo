package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class GruppoController
{
    @Autowired
    private GruppoService gruppoService;

    @GetMapping("/visualizzaMembriSupergruppo/{id}")
    public String visualizzaMembriSupergruppo(Model model, @PathVariable(name = "id")int idSupergruppo){
        List<Persona> result =  gruppoService.visualizzaListaMembriSupergruppo(idSupergruppo);
        model.addAttribute("membri" ,result);
        return "/paginaVisualizzaMembri";
    }

    @GetMapping("/visualizzaMembriConsiglio/{id}")
    public String visualizzaMembriConsiglio(Model model, @PathVariable(name = "id")int idConsiglio){
        List<Persona> result =  gruppoService.visualizzaListaMembriConsiglioDidattico(idConsiglio);
        model.addAttribute("membri" ,result);
        return "/paginaVisualizzaMembri";
    }

    @GetMapping("/visualizzaMembriDipartimento/{id}")
    public String visualizzaMembriDipartimento(Model model, @PathVariable(name = "id")int idDipartimento){
        List<Persona> result =  gruppoService.visualizzaListaMembriDipartimento(idDipartimento);
        model.addAttribute("membri" ,result);
        return "/paginaVisualizzaMembri";
    }


    @GetMapping("/visualizzaGruppi/{idPersona}")
    public String visualizzaGruppi(Model model, @PathVariable("idPersona")  int idPersona){
        model.addAttribute("supergruppi", gruppoService.visualizzaSupergruppi(idPersona));
        model.addAttribute("consigli",gruppoService.visualizzaConsigliDidattici(idPersona));
        model.addAttribute("dipartimenti", gruppoService.visualizzaDipartimenti(idPersona));
        return "/paginaIMieiGruppi";
    }

}
