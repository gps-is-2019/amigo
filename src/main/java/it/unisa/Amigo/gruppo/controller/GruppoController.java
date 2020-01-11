package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.gruppo.domain.Commissione;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Questa classe si occupa della logica di controllo del sottosistema gruppo
 */
@Controller
@RequiredArgsConstructor
public class GruppoController {
    private final GruppoService gruppoService;

    /***
     * Ritorna ad una pagina i membri @{@link Persona} di un supergruppo
     * @param model per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo di cui si vogliono visualizzare i membri
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{id}")
    public String findAllMembriInSupergruppo(final Model model, @PathVariable(name = "id") final int idSupergruppo) {
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        model.addAttribute("isCapogruppo", gruppoService.isResponsabile(personaLoggata.getId(), idSupergruppo));
        prepareCandidateList(idSupergruppo, model, gruppoService.findAllMembriInSupergruppo(idSupergruppo));
        model.addAttribute("commissioni", gruppoService.findAllCommissioniByGruppo(idSupergruppo));
        return "gruppo/gruppo_detail";
    }

    /***
     * Ritorna ad una pagina i gruppi @{@link Supergruppo} di una persona @{@link Persona}
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi")
    public String findAllSupergruppi(final Model model) {
        int idPersona = gruppoService.getAuthenticatedUser().getId();
        model.addAttribute("supergruppi", gruppoService.findAllSupergruppiOfPersona(idPersona));
        model.addAttribute("personaLoggata", idPersona);
        //TODO aggiungere al model il ruolo della persona loggata
        model.addAttribute("ruolo", gruppoService.getAuthenticatedUser().getRuolo());
        return "gruppo/miei_gruppi";
    }

    /***
     * Ritorna ad una pagina i membri @{@link Persona} di una consiglio didattico @{@link it.unisa.Amigo.gruppo.domain.ConsiglioDidattico} ma non nel @{@link Supergruppo}
     * @param idSupergruppo id del consiglio didattico
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/candidati")
    public String findAllMembriInConsiglioDidatticoNoSupergruppo(@PathVariable(name = "idSupergruppo") final int idSupergruppo, final Model model) {
        prepareCandidateList(idSupergruppo, model, gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(idSupergruppo));
        return "gruppo/aggiunta_membro";
    }

    /***
     * Ritorna ad una pagina i membri @{@link Persona} di una gruppo che non sono ancora stati assegnati ad una commissione@{@link Commissione}
     * @param idSupergruppo id della commissione
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/commissioni/{idSupergruppo}/candidati")
    public String groupCandidatesList(@PathVariable(name = "idSupergruppo") final int idSupergruppo, final Model model) {
        prepareCandidateList(idSupergruppo, model, gruppoService.findAllMembriInGruppoNoCommissione(idSupergruppo));
        return "gruppo/aggiunta_membro_commissione";
    }

    /**
     * Recupera le informazioni sui membri componenti un supergruppo, la persona loggata e li inserisce nel model
     *
     * @param idSupergruppo da cui recuperare la lista dei membri
     * @param model         per salvare informazioni da recuperare nell'html
     * @param allMembri     la lista di persone che verrà inserita nel model
     */
    private void prepareCandidateList(final int idSupergruppo, final Model model, final List<Persona> allMembri) {
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
    public String addMembro(@PathVariable(name = "idPersona") final int idPersona, @PathVariable(name = "idSupergruppo") final int idSupergruppo, final Model model) {
        if (!gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), idSupergruppo)) {
            return "unauthorized";
        }
        Persona persona = gruppoService.findPersona(idPersona);
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        gruppoService.addMembro(persona, supergruppo);
        model.addAttribute("flagAggiunta", 1);
        model.addAttribute("personaAggiunta", persona);
        if (supergruppo.getType().equalsIgnoreCase("Gruppo")) {
            prepareCandidateList(idSupergruppo, model, gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(idSupergruppo));
            model.addAttribute("isCapogruppo", gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), idSupergruppo));
            return "gruppo/aggiunta_membro";
        } else {
            prepareCandidateList(idSupergruppo, model, gruppoService.findAllMembriInGruppoNoCommissione(idSupergruppo));
            return "gruppo/aggiunta_membro_commissione";
        }
    }


    /***
     * Rimuove un membro @{@link Persona} dal supergruppo @{@link Supergruppo}
     * @param idPersona id della persona da rimuovere
     * @param idSupergruppo id del supergruppo da cui rimuovere la persona
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/remove/{idPersona}")
    public String removeMembro(@PathVariable(name = "idPersona") final int idPersona, @PathVariable(name = "idSupergruppo") final int idSupergruppo, final Model model) {
        if (!gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), idSupergruppo)) {
            return "unauthorized";
        }
        Persona persona = gruppoService.findPersona(idPersona);
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        gruppoService.removeMembro(persona, supergruppo);
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        prepareCandidateList(idSupergruppo, model, gruppoService.findAllMembriInSupergruppo(idSupergruppo));
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(), idSupergruppo));
        model.addAttribute("flagRimozione", 1);
        model.addAttribute("personaRimossa", persona);
        if (supergruppo.getType().equalsIgnoreCase("Gruppo")) {
            model.addAttribute("isCapogruppo", gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), idSupergruppo));
            model.addAttribute("commissioni", gruppoService.findAllCommissioniByGruppo(idSupergruppo));
            return "gruppo/gruppo_detail";
        } else {
            model.addAttribute("isCapogruppo", gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), gruppoService.findGruppoByCommissione(idSupergruppo).getId()));
            //model.addAttribute("commissioni", gruppoService.findAllCommissioniByGruppo(idSupergruppo));
            return "gruppo/commissione_detail";
        }
    }

    /***
     * Ritorna ad una pagina i membri @{@link Persona} di una commissione @{@link Commissione}
     * @param model per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id della commissione di cui si vogliono visualizzare i membri
     * TODO id_commissione
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{id}/commissione_detail/{id_commissione}")
    public String findAllMembriInCommissione(final Model model, @PathVariable(name = "id") final int idSupergruppo, @PathVariable(name = "id_commissione") final int idCommissione) {
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        prepareCandidateList(idCommissione, model, gruppoService.findAllMembriInSupergruppo(idCommissione));
        model.addAttribute("isCapogruppo", gruppoService.isResponsabile(personaLoggata.getId(), gruppoService.findGruppoByCommissione(idCommissione).getId()));
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(), idCommissione));
        return "gruppo/commissione_detail";
    }

    /**
     * Esegue la chiusura di una commissione @{@link Commissione}
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idCommissione l'id della commissione della quale si vuole effettuare la chiusura
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/commissioni/{id2}/chiusura")
    public String closeCommissione(final Model model, @PathVariable(name = "id2") final int idCommissione) {
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        if (!gruppoService.isResponsabile(personaLoggata.getId(), gruppoService.findGruppoByCommissione(idCommissione).getId())) {
            return "unauthorized";
        }
        prepareCandidateList(idCommissione, model, gruppoService.findAllMembriInSupergruppo(idCommissione));
        model.addAttribute("isCapogruppo", gruppoService.isResponsabile(personaLoggata.getId(), gruppoService.findGruppoByCommissione(idCommissione).getId()));
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(), idCommissione));
        model.addAttribute("flagChiusura", 1);
        gruppoService.closeCommissione(idCommissione);
        return "gruppo/commissione_detail";
    }

    /**
     * Gestisce il form necessario alla creazione di una nuova commissione @{@link Commissione}
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo l'id del gruppo al quale verrà assegnata la nuova commissione
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{id}/commissioni/create")
    public String createCommissioneForm(final Model model, @PathVariable(name = "id") final int idSupergruppo) {
        model.addAttribute("idGruppo", idSupergruppo);
        model.addAttribute("command", new GruppoFormCommand());
        List<Persona> persone = gruppoService.findAllMembriInSupergruppo(idSupergruppo);
        model.addAttribute("persone", persone);
        return "gruppo/crea_commissione";
    }

    /**
     * Gestisce la creazione di una commissione @{@link Commissione}
     *
     * @param gruppoFormCommand il gestore del form
     * @param model             per salvare informazioni da recuperare nell'html
     * @param idGruppo          l'id del gruppo al quale verrà assegnata la nuova commissione
     * @return il path della pagina su cui eseguire il redirect
     */
    @PostMapping("/gruppi/{idGruppo}/commissioni/create")
    public String createCommissione(@ModelAttribute("command") final GruppoFormCommand gruppoFormCommand, final Model model, @PathVariable(name = "idGruppo") final int idGruppo) {
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        if((gruppoFormCommand.getName().equals("")) || (gruppoFormCommand.getDescrizione().equals("")) || (gruppoFormCommand.getIdPersona() == 0)
            || (!gruppoFormCommand.getName().matches("[A-Z][a-zA-Z][^#&<>\\\"~;$^%{}?]{1,20}$")) || (!gruppoFormCommand.getDescrizione().matches("[A-Z][a-zA-Z][^#&<>\\\"~;$^%{}?]{1,100}$"))) {
            model.addAttribute("idGruppo", idGruppo);
            model.addAttribute("command", new GruppoFormCommand());
            List<Persona> persone = gruppoService.findAllMembriInSupergruppo(idGruppo);
            model.addAttribute("persone", persone);
            model.addAttribute("flagCreate", 1);
            return "gruppo/crea_commissione";
        }
        if (!gruppoService.isResponsabile(personaLoggata.getId(), idGruppo)) {
            return "unauthorized";
        }
        Commissione commissione = new Commissione(gruppoFormCommand.getName(), "Commissione", true, gruppoFormCommand.getDescrizione());
        gruppoService.createCommissione(commissione, idGruppo);
        gruppoService.nominaResponsabile(gruppoFormCommand.getIdPersona(), commissione.getId());


        model.addAttribute("isCapogruppo", gruppoService.isResponsabile(personaLoggata.getId(), idGruppo));
        prepareCandidateList(idGruppo, model, gruppoService.findAllMembriInSupergruppo(idGruppo));
        model.addAttribute("commissioni", gruppoService.findAllCommissioniByGruppo(idGruppo));

        return "gruppo/gruppo_detail";
    }

    /**
     * Nomina ed assegna un nuovo responsabile @{@link Persona}  ad una commissione @{@link Commissione}
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idCommissione la commissione che riceverà un nuovo responsabile
     * @param idPersona     id della persona che diventerà il nuovo responsabile
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/commissioni/{idCommissione}/nominaResponsabile/{idPersona}")
    public String nominaResponsabile(final Model model, @PathVariable("idCommissione") final int idCommissione, @PathVariable("idPersona") final int idPersona) {

        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        gruppoService.nominaResponsabile(idPersona, idCommissione);
        Commissione commissione = (Commissione) gruppoService.findSupergruppo(idCommissione);
        if (!gruppoService.isResponsabile(personaLoggata.getId(), gruppoService.findGruppoByCommissione(idCommissione).getId())) {
            return "unauthorized";
        }
        List<Persona> persone = gruppoService.findAllMembriInSupergruppo(commissione.getId());
        model.addAttribute("idCommissione", idCommissione);
        model.addAttribute("isCapogruppo", gruppoService.isResponsabile(personaLoggata.getId(), gruppoService.findGruppoByCommissione(idCommissione).getId()));
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(), idCommissione));
        model.addAttribute("persone", persone);
        model.addAttribute("personaLoggata", gruppoService.getAuthenticatedUser().getId());
        model.addAttribute("supergruppo", commissione);
        model.addAttribute("flagNomina", 1);
        model.addAttribute("responsabile", gruppoService.findPersona(idPersona));
        return "gruppo/commissione_detail";
    }

}
