package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class GruppoController
{

    @Autowired
    private GruppoService gruppoService;

    @GetMapping("/visualizzaMembriSupergruppo")
    public String visualizzaMembriSupergruppo(Model model , int id)
    {
        model.addAttribute("message", "la lista dei membri è la seguente: ");
        List<Persona> ris =  gruppoService.visualizzaListaMembriSupergruppo(id);
        model.addAttribute("membri" ,ris);
        return "/paginaVisualizzaMembri";
    }









}
