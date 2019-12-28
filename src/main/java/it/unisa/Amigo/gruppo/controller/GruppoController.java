package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.gruppo.domain.Commissione;
import it.unisa.Amigo.gruppo.domain.Gruppo;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
        model.addAttribute("isCapogruppo", gruppoService.isResponsabile(personaLoggata.getId(), idSupergruppo));
        prepareCandidateList(idSupergruppo,model,gruppoService.findAllMembriInSupergruppo(idSupergruppo));
        model.addAttribute("commissioni", gruppoService.findAllCommissioniByGruppo(idSupergruppo));
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
        return "gruppo/aggiunta_membro";
    }

    /***
     * Ritorna ad una pagina i membri @{@link Persona} di una gruppo @{@link Gruppo} che non sono ancora stati assegnati ad una commissione@{@link Commissione}
     * @param idSupergruppo id della commissione
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/commissioni/{idSupergruppo}/candidati")
    public String findAllMembriInGruppoNoCommissione(@PathVariable(name = "idSupergruppo") int idSupergruppo, Model model){
        prepareCandidateList(idSupergruppo, model, gruppoService.findAllMembriInGruppoNoCommissione(idSupergruppo));
        return "gruppo/aggiunta_membro_commissione";
    }

    /**
     * Recupera le informazioni sui membri componenti un supergruppo, la persona loggata e li inserisce nel model
     * @param idSupergruppo da cui recuperare la lista dei membri
     * @param model per salvare informazioni da recuperare nell'html
     * @param allMembri la lista di persone che verrà inserita nel model
     */
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
        model.addAttribute("isCapogruppo", gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), idSupergruppo));
        model.addAttribute("flagAggiunta",1);
        model.addAttribute("personaAggiunta",persona);
        return "gruppo/aggiunta_membro";
    }

    /**
     * Aggiunge un nuovo membro  @{@link Persona} ad una commissione @{@link Commissione
     * @param idPersona id della persona da aggiungere
     * @param idSupergruppo id della commissione in cui aggiungere la persona
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/commissioni/{idSupergruppo}/add/{idPersona}")
    public String addMembroCommissione(@PathVariable(name = "idPersona") int idPersona,@PathVariable(name = "idSupergruppo") int idSupergruppo, Model model){
        Persona persona = gruppoService.findPersona(idPersona);
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        gruppoService.addMembro(persona,supergruppo);
        prepareCandidateList(idSupergruppo,model,gruppoService.findAllMembriInGruppoNoCommissione(idSupergruppo));
        model.addAttribute("flagAggiunta",1);
        model.addAttribute("personaAggiunta",persona);
        return "gruppo/aggiunta_membro_commissione";
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
        model.addAttribute("isCapogruppo", gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), idSupergruppo));
        model.addAttribute("flagRimozione",1);
        model.addAttribute("personaRimossa",persona);
        model.addAttribute("commissioni", gruppoService.findAllCommissioniByGruppo(idSupergruppo));
        return "gruppo/gruppo_detail";
    }

    /***
     * Rimuove un membro @{@link Persona} da una commissione @{@link Commissione}
     * @param idPersona id della persona da rimuovere
     * @param idSupergruppo id della commissione da cui rimuovere la persona
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/commissioni/{idSupergruppo}/remove/{idPersona}")
    public String removeMembroCommissione(@PathVariable(name = "idPersona") int idPersona,@PathVariable(name = "idSupergruppo") int idSupergruppo, Model model){
        Persona persona = gruppoService.findPersona(idPersona);
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        gruppoService.removeMembro(persona,supergruppo);
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        prepareCandidateList(idSupergruppo,model,gruppoService.findAllMembriInSupergruppo(idSupergruppo));
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));
        model.addAttribute("isCapogruppo", gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), gruppoService.findGruppoByCommissione(idSupergruppo).getId()));
        model.addAttribute("flagRimozione",1);
        model.addAttribute("personaRimossa",persona);
        model.addAttribute("commissioni", gruppoService.findAllCommissioniByGruppo(idSupergruppo));
        return "gruppo/commissione_detail";
    }

    /***
     * Ritorna ad una pagina i membri @{@link Persona} di una commissione @{@link Commissione}
     * @param model per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id della commissione di cui si vogliono visualizzare i membri
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{id}/commissione_detail/{id_commissione}")
    public String findAllMembriInCommissione(Model model, @PathVariable(name = "id")int idSupergruppo, @PathVariable(name = "id_commissione")int idCommissione){
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        prepareCandidateList(idCommissione,model,gruppoService.findAllMembriInSupergruppo(idCommissione));
        model.addAttribute("isCapogruppo", gruppoService.isResponsabile(personaLoggata.getId(), gruppoService.findGruppoByCommissione(idCommissione).getId()));
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idCommissione));
        return "gruppo/commissione_detail";
    }

    /**
     * Esegue la chiusura di una commissione @{@link Commissione}
     * @param model per salvare informazioni da recuperare nell'html
     * @param idCommissione l'id della commissione della quale si vuole effettuare la chiusura
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/commissioni/{id2}/chiusura")
    public String closeCommissione(Model model, @PathVariable(name = "id2")int idCommissione){
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        prepareCandidateList(idCommissione,model,gruppoService.findAllMembriInSupergruppo(idCommissione));
        model.addAttribute("isCapogruppo", gruppoService.isResponsabile(personaLoggata.getId(), gruppoService.findGruppoByCommissione(idCommissione).getId()));
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idCommissione));
        model.addAttribute("flagChiusura",1);
        gruppoService.closeCommissione(idCommissione);
        return "gruppo/commissione_detail";
    }

    /**
     * Gestisce il form necessario alla creazione di una nuova commissione @{@link Commissione}
     * @param model per salvare informazioni da recuperare nell'html
     * @param idSupergruppo l'id del gruppo al quale verrà assegnata la nuova commissione
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{id}/commissioni/create")
    public String createCommissioneForm(Model model, @PathVariable(name = "id")int idSupergruppo){
        model.addAttribute("idGruppo", idSupergruppo);
        model.addAttribute("command", new GruppoFormCommand());
        return "gruppo/crea_commissione";
    }

    /**
     * Gestisce la creazione di una commissione @{@link Commissione}
     * @param gruppoFormCommand il gestore del form
     * @param model per salvare informazioni da recuperare nell'html
     * @param idGruppo l'id del gruppo al quale verrà assegnata la nuova commissione
     * @return il path della pagina su cui eseguire il redirect
     */
    @PostMapping("/gruppi/{idGruppo}/commissioni/createCommissione")
    public String createCommissione(@ModelAttribute("command") GruppoFormCommand gruppoFormCommand, Model model,@PathVariable(name = "idGruppo")int idGruppo ){
        Commissione commissione = new Commissione(gruppoFormCommand.getName(), "Commissione", true, gruppoFormCommand.getDescrizione());
        gruppoService.createCommissione(commissione, idGruppo);

        model.addAttribute("idCommissione",commissione.getId());
        model.addAttribute("idGruppo", idGruppo);

        List<Persona> persone = gruppoService.findAllMembriInSupergruppo(idGruppo);
        model.addAttribute("persone",persone);

        return "gruppo/nomina_responsabile";
    }

    /**
     * Nomina ed assegna un nuovo responsabile @{@link Persona}  ad una commissione @{@link Commissione}
     * @param model per salvare informazioni da recuperare nell'html
     * @param idCommissione la commissione che riceverà un nuovo responsabile
     * @param idPersona id della persona che diventerà il nuovo responsabile
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/commissioni/{idCommissione}/nominaResponsabile/{idPersona}")
    public String nominaResponsabile(Model model, @PathVariable("idCommissione") int idCommissione, @PathVariable("idPersona") int idPersona){

        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        gruppoService.nominaResponsabile(idPersona, idCommissione);
        Commissione commissione = (Commissione) gruppoService.findSupergruppo(idCommissione);
        List<Persona> persone = gruppoService.findAllMembriInSupergruppo(commissione.getId());
        model.addAttribute("idCommissione",idCommissione);
        model.addAttribute("isCapogruppo", gruppoService.isResponsabile(personaLoggata.getId(), gruppoService.findGruppoByCommissione(idCommissione).getId()));
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idCommissione));
        model.addAttribute("persone",persone);
        model.addAttribute("personaLoggata", gruppoService.getAuthenticatedUser().getId());
        model.addAttribute("supergruppo", commissione);
        model.addAttribute("flagNomina", 1);
        model.addAttribute("responsabile", gruppoService.findPersona(idPersona));
        return "gruppo/commissione_detail";
    }

}
