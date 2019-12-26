package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.gruppo.domain.Commissione;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Questa classe si occupa della logoca di controllo del sottosistema gruppo
 */
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
    @GetMapping("/gruppi/{id}")
    public String findAllMembriInSupergruppo(Model model, @PathVariable(name = "id")int idSupergruppo){
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        prepareCandidateList(idSupergruppo,model,gruppoService.findAllMembriInSupergruppo(idSupergruppo));
        model.addAttribute("isCapogruppo", gruppoService.isCapogruppo(personaLoggata.getId()));
        model.addAttribute("commissioni", gruppoService.findAllCommissioniByGruppo(idSupergruppo));
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));
        return "gruppo/gruppo_detail";
    }

    /***
     * Ritorna ad una pagina i gruppi @{@link Supergruppo} di una persona @{@link Persona}
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi")
    public String findAllSupergruppi(Model model){

        int idPersona = gruppoService.getAuthenticatedUser().getId();
        model.addAttribute("supergruppi", gruppoService.findAllSupergruppi(idPersona));
        model.addAttribute("personaLoggata",idPersona);
        return "gruppo/miei_gruppi";
    }

    /***
     * Ritorna ad una pagina i membri @{@link Persona} di una consiglio didattico @{@link it.unisa.Amigo.gruppo.domain.ConsiglioDidattico} ma non nel @{@link Supergruppo}
     * @param idSupergruppo id del consiglio didattico
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/candidati")
    public String findAllMembriInConsiglioDidatticoNoSupergruppo(@PathVariable(name = "idSupergruppo") int idSupergruppo, Model model){
        prepareCandidateList(idSupergruppo, model, gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(idSupergruppo));
        return "gruppo/aggiunta-membro";
    }

    @GetMapping("/gruppi/commissioni/{idSupergruppo}/candidati")
    public String findAllMembriInGruppoNoCommissione(@PathVariable(name = "idSupergruppo") int idSupergruppo, Model model){
        prepareCandidateList(idSupergruppo, model, gruppoService.findAllMembriInGruppoNoCommissione(idSupergruppo));
        return "gruppo/aggiunta-membro-commissione";
    }

    private void prepareCandidateList(int idSupergruppo, Model model, List<Persona> allMembri) {
        model.addAttribute("persone", allMembri);
        model.addAttribute("supergruppo", gruppoService.findSupergruppo(idSupergruppo));
        model.addAttribute("personaLoggata", gruppoService.getAuthenticatedUser().getId());
    }

    /***
     * Aggiunge un membro @{@link Persona} al supergruppo @{@link Supergruppo}
     * @param idPersona id della persona da aggiungere
     * @param idSupergruppo id del supergruppo in cui aggiungere la persona
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/add/{idPersona}")
    public String addMembro(@PathVariable(name = "idPersona") int idPersona,@PathVariable(name = "idSupergruppo") int idSupergruppo, Model model){
        Persona persona = gruppoService.findPersona(idPersona);
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        gruppoService.addMembro(persona,supergruppo);
        prepareCandidateList(idSupergruppo,model,gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(idSupergruppo));
        model.addAttribute("flagAggiunta",1);
        model.addAttribute("personaAggiunta",persona);
        return "gruppo/aggiunta-membro";
    }

    @GetMapping("/gruppi/commissioni/{idSupergruppo}/add/{idPersona}")
    public String addMembroCommissione(@PathVariable(name = "idPersona") int idPersona,@PathVariable(name = "idSupergruppo") int idSupergruppo, Model model){
        Persona persona = gruppoService.findPersona(idPersona);
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        gruppoService.addMembro(persona,supergruppo);
        prepareCandidateList(idSupergruppo,model,gruppoService.findAllMembriInGruppoNoCommissione(idSupergruppo));
        model.addAttribute("flagAggiunta",1);
        model.addAttribute("personaAggiunta",persona);
        for(Supergruppo s : persona.getSupergruppi())
            System.out.println(s.getName());
        return "gruppo/aggiunta-membro-commissione";
    }

    /***
     * Rimuove un membro @{@link Persona} dal supergruppo @{@link Supergruppo}
     * @param idPersona id della persona da rimuovere
     * @param idSupergruppo id del supergruppo da cui rimuovere la persona
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/remove/{idPersona}")
    public String removeMembro(@PathVariable(name = "idPersona") int idPersona,@PathVariable(name = "idSupergruppo") int idSupergruppo, Model model){
        Persona persona = gruppoService.findPersona(idPersona);
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        gruppoService.removeMembro(persona,supergruppo);
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        prepareCandidateList(idSupergruppo,model,gruppoService.findAllMembriInSupergruppo(idSupergruppo));
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));
        model.addAttribute("flagRimozione",1);
        model.addAttribute("personaRimossa",persona);
        model.addAttribute("commissioni", gruppoService.findAllCommissioniByGruppo(idSupergruppo));
        return "gruppo/gruppo_detail";
    }

    @GetMapping("/gruppi/commissioni/{idSupergruppo}/remove/{idPersona}")
    public String removeMembroCommissione(@PathVariable(name = "idPersona") int idPersona,@PathVariable(name = "idSupergruppo") int idSupergruppo, Model model){
        Persona persona = gruppoService.findPersona(idPersona);
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        gruppoService.removeMembro(persona,supergruppo);
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        prepareCandidateList(idSupergruppo,model,gruppoService.findAllMembriInSupergruppo(idSupergruppo));
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));
        model.addAttribute("flagRimozione",1);
        model.addAttribute("personaRimossa",persona);
        model.addAttribute("commissioni", gruppoService.findAllCommissioniByGruppo(idSupergruppo));
        return "gruppo/commissione_detail";
    }

    @GetMapping("/gruppi/{id}/commissione_detail/{id_commissione}")
    public String findAllMembriInCommissione(Model model, @PathVariable(name = "id")int idSupergruppo, @PathVariable(name = "id_commissione")int idCommissione){
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        prepareCandidateList(idCommissione,model,gruppoService.findAllMembriInSupergruppo(idCommissione));
        model.addAttribute("isCapogruppo", gruppoService.isCapogruppo(personaLoggata.getId()));
        model.addAttribute("commissioni", gruppoService.findAllCommissioniByGruppo(idSupergruppo));
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idCommissione));
        return "gruppo/commissione_detail";
    }

}
