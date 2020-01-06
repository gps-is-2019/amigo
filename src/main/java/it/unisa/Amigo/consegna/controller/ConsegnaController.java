package it.unisa.Amigo.consegna.controller;

import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.consegna.services.ConsegnaService;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Questa classe si occupa della logica di controllo del sottosistema "Consegna"
 */
@Controller
public class ConsegnaController {
    @Autowired
    private ConsegnaService consegnaService;

    @Autowired
    private GruppoService gruppoService;

    /**
     * Mostra una pagina contenente tutti i possibili destinatari della consegna
     *
     * @param model     per salvare informazioni da recuperare nell'html
     * @param ruoloDest il ruolo a cui effettuare la consegna
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping({"/consegna/{ruolo}", "/consegna"})
    public String viewConsegna(Model model, @PathVariable(name = "ruolo", required = false) String ruoloDest) {
        Set<String> possibiliDestinatari = consegnaService.possibiliDestinatari();

        if (ruoloDest == null)
            ruoloDest = "";

        if (possibiliDestinatari.size() == 1 && ruoloDest.equals(""))
            ruoloDest = possibiliDestinatari.iterator().next();

        List<Persona> destinatari = new ArrayList<>();
        boolean flagRuolo = false;
        if (ruoloDest.equalsIgnoreCase(Role.PQA_ROLE) || ruoloDest.equalsIgnoreCase(Role.NDV_ROLE)) {
            destinatari.add(new Persona("", ruoloDest, ""));
            flagRuolo = true;
        } else if (possibiliDestinatari.contains(ruoloDest)) {
            destinatari = gruppoService.findAllByRuolo(ruoloDest);
        }

        model.addAttribute("possibiliDestinatari", possibiliDestinatari);
        model.addAttribute("destinatari", destinatari);
        model.addAttribute("ruoloDest", ruoloDest);
        model.addAttribute("flagRuolo", flagRuolo);

        return "consegna/destinatari";
    }

    /**
     * Effettua delle consegne ai destinatari presi in input
     *
     * @param model           per salvare informazioni da recuperare nell'html
     * @param file            il documento da allegare alla consegna
     * @param destinatariPost i destinatari
     * @return il path della pagina su cui eseguire il redirect
     */
    @PostMapping("/consegna")
    public String sendDocumento(Model model, @RequestParam MultipartFile file, @RequestParam String destinatariPost) {
        if (!Character.isDigit(destinatariPost.charAt(0))) {
            consegnaService.sendDocumento(null, destinatariPost, file);
        } else {
            String[] destinatariString = destinatariPost.split(",");
            int[] destinatariInt = new int[destinatariString.length];
            int i = 0;

            for (String p : destinatariString)
                destinatariInt[i++] = Integer.parseInt(p);

            consegnaService.sendDocumento(destinatariInt, "", file);
        }
        return "redirect:/consegna/inviati?name=";
    }

    /**
     * Recupera le consegne inviate dall'utente autenticato
     *
     * @param model per salvare informazioni da recuperare nell'html
     * @param name  il nome su cui filtrare la ricerca
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/consegna/inviati")
    public String findConsegneInviate(Model model, @RequestParam("name") String name) {
        List<Consegna> consegne = consegnaService.consegneInviate();
        model.addAttribute("consegne", consegneFilter(consegne, name));
        return "consegna/documenti-inviati";
    }

    /**
     * Recupera le consegne ricevute dall'utente autenticato
     *
     * @param model per salvare informazioni da recuperare nell'html
     * @param name  il nome su cui filtrare la ricerca
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/consegna/ricevuti")
    public String findConsegneRicevute(Model model, @RequestParam("name") String name) {
        List<Consegna> consegne = consegnaService.consegneRicevute();
        model.addAttribute("consegne", consegneFilter(consegne, name));
        return "consegna/documenti-ricevuti";
    }

    /**
     * Filtra la ricerca in base al nome della consegna
     *
     * @param consegne le consegne da filtrare
     * @param name     utilizzato dal filtro
     * @return la lista delle consegne filtrate
     */
    private List<Consegna> consegneFilter(List<Consegna> consegne, String name) {
        List<Consegna> consegneReturn = new ArrayList<>();

        for (Consegna c : consegne)
            if (c.getDocumento().getNome().toLowerCase().contains(name.toLowerCase()) || name.equals(""))
                consegneReturn.add(c);

        return consegneReturn;
    }

    /**
     * Esegue il downlaod del file allegato ad un documento
     *
     * @param model       per salvare informazioni da recuperare nell'html
     * @param idDocumento l'id del documento
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/consegna/miei-documenti/{idDocumento}")
    public ResponseEntity<Resource> downloadDocumento(Model model, @PathVariable("idDocumento") int idDocumento) {
        Consegna consegna = consegnaService.findConsegnaByDocumento(idDocumento);
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        Set<Role> role = personaLoggata.getUser().getRoles();
        List<String> ruoliString = new ArrayList<>();
        for(Role r : role){
            ruoliString.add(r.getName());
        }

        if(consegna.getLocazione().equalsIgnoreCase(Consegna.PQA_LOCAZIONE) && (ruoliString.contains(Role.PQA_ROLE))){
            return consegnaService.downloadDocumento(idDocumento);
        }
        if(consegna.getLocazione().equalsIgnoreCase(Consegna.NDV_LOCAZIONE) && (ruoliString.contains(Role.NDV_ROLE))){
            return consegnaService.downloadDocumento(idDocumento);
        }
        if (consegna.getMittente().getId() != personaLoggata.getId() && consegna.getDestinatario().getId() != personaLoggata.getId()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "https://i.makeagif.com/media/6-18-2016/i4va3h.gif");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
        return consegnaService.downloadDocumento(idDocumento);
    }

    /**
     * Modifica lo stato di una consegna in APPROVATA tramite il suo id
     * @param model      per salvare informazioni da recuperare nell'html
     * @param idConsegna l'id della consegna da approvare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/consegna/approva/{id}")
    public String approvaConsegna(Model model, @PathVariable("id") int idConsegna) {
        consegnaService.approvaConsegna(idConsegna);
        List<Consegna> consegne = consegnaService.consegneRicevute();
        model.addAttribute("consegne", consegneFilter(consegne, ""));
        return "consegna/documenti-ricevuti";
    }

    /**
     * Modifica lo stato di una consegna in RIFIUTATA tramite il suo id
     * @param model      per salvare informazioni da recuperare nell'html
     * @param idConsegna l'id della consegna da rifiutare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/consegna/rifiuta/{id}")
    public String rifiutaConsegna(Model model, @PathVariable("id") int idConsegna) {
        consegnaService.rifiutaConsegna(idConsegna);
        List<Consegna> consegne = consegnaService.consegneRicevute();
        model.addAttribute("consegne", consegneFilter(consegne, ""));
        return "consegna/documenti-ricevuti";
    }
}