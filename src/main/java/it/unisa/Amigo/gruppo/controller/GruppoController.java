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
        model.addAttribute("message", "La lista dei membri è la seguente: ");
        List<Persona> result =  gruppoService.visualizzaListaMembriSupergruppo(idSupergruppo);
        model.addAttribute("membri" ,result);
        return "/paginaVisualizzaMembri";
    }

    @GetMapping("/visualizzaMembriConsiglio/{id}")
    public String visualizzaMembriConsiglio(Model model, @PathVariable(name = "id")int idConsiglio){
        model.addAttribute("message", "La lista dei membri è la seguente: ");
        List<Persona> result =  gruppoService.visualizzaListaMembriConsiglioDidattico(idConsiglio);
        model.addAttribute("membri" ,result);
        System.out.println("I miei consigli didattici :\n" + result);
        return "/paginaVisualizzaMembri";
    }

    @GetMapping("/visualizzaMembriDipartimento/{id}")
    public String visualizzaMembriDipartimento(Model model, @PathVariable(name = "id")int idDipartimento){
        model.addAttribute("message", "La lista dei membri è la seguente: ");
        List<Persona> result =  gruppoService.visualizzaListaMembriDipartimento(idDipartimento);
        model.addAttribute("membri" ,result);
        System.out.println("Il mio dipartimento bello bello :\n" + result);
        return "/paginaVisualizzaMembri";
    }





}
