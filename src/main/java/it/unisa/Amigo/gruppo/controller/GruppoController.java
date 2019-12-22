package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    /***
     * Ritorna ad una pagina i membri @{@link Persona} di un supergruppo @{@link Supergruppo}
     * @param model per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo di cui si vogliono visualizzare i membri
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppo/visualizzaMembriSupergruppo={id}")
    public String visualizzaMembriSupergruppo(Model model, @PathVariable(name = "id")int idSupergruppo){
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        List<Persona> result =  gruppoService.visualizzaListaMembriSupergruppo(idSupergruppo);
        model.addAttribute("personaLoggata",gruppoService.visualizzaPersonaLoggata().getId());
        model.addAttribute("persone" ,result);
        model.addAttribute("supergruppo", supergruppo);
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));
        model.addAttribute("idConsiglio", gruppoService.findConsiglioBySupergruppo(idSupergruppo).getId());
        return "gruppo/paginaVisualizzaMembri";
    }

    /***
     * Ritorna ad una pagina i gruppi @{@link Supergruppo} di una persona @{@link Persona}
     * @param model per salvare informazioni da recuperare nell'html
     * @param idPersona id della persona di cui si vogliono visualizzare i gruppo
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppo/visualizzaGruppi={idPersona}")
    public String visualizzaGruppi(Model model, @PathVariable("idPersona")  int idPersona){
        model.addAttribute("supergruppi", gruppoService.visualizzaSupergruppi(idPersona));
        model.addAttribute("personaLoggata",gruppoService.visualizzaPersonaLoggata().getId());
        return "gruppo/paginaIMieiGruppi";
    }

    /***
     * Ritorna ad una pagina i membri @{@link Persona} di una consiglio didattico @{@link it.unisa.Amigo.gruppo.domain.ConsiglioDidattico} ma non nel @{@link Supergruppo}
     * @param id id del consiglio didattico
     * @param id2 id del supergruppo
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppo/id={id}&supergruppo={id2}")
    public String findAllMembriInConsiglioDidatticoNoSupergruppo(@PathVariable(name = "id") int id,@PathVariable(name = "id2") int id2, Model model){
        List<Persona> persone = new ArrayList<>();
        persone = gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(id,id2);
        model.addAttribute("persone",persone);
        model.addAttribute("supergruppo",gruppoService.findSupergruppo(id2));
        model.addAttribute("personaLoggata",gruppoService.visualizzaPersonaLoggata().getId());
        return "gruppo/aggiunta-membro";
    }

    /***
     * Aggiunge un membro @{@link Persona} al supergruppo @{@link Supergruppo}
     * @param id id della persona da aggiungere
     * @param id2 id del supergruppo in cui aggiungere la persona
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppo/aggiungi/id={id}&supergruppo={id2}")
    public String addMembro(@PathVariable(name = "id") int id,@PathVariable(name = "id2") int id2, Model model){
        Persona persona = gruppoService.findPersona(id);
        Supergruppo supergruppo = gruppoService.findSupergruppo(id2);
        gruppoService.addMembro(persona,supergruppo);
        return "gruppo/aggiungi";
    }

    /***
     * Rimuove un membro @{@link Persona} dal supergruppo @{@link Supergruppo}
     * @param id id della persona da rimuovere
     * @param id2 id del supergruppo da cui rimuovere la persona
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppo/rimuovi/id={id}&supergruppo={id2}")
    public String removeMembro(@PathVariable(name = "id") int id,@PathVariable(name = "id2") int id2, Model model){
        Persona persona = gruppoService.findPersona(id);
        Supergruppo supergruppo = gruppoService.findSupergruppo(id2);
        gruppoService.removeMembro(persona,supergruppo);
        return "gruppo/rimuovi";
    }
}
