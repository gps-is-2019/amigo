package it.unisa.Amigo.consegna.controller;

import it.unisa.Amigo.autenticazione.domain.Role;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.consegna.services.ConsegnaService;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoService;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequiredArgsConstructor
public class ConsegnaController {
    private final ConsegnaService consegnaService;

    private final GruppoService gruppoService;

    private final DocumentoService documentoService;

    /**
     * Mostra una pagina contenente tutti i possibili destinatari della consegna
     *
     * @param model     per salvare informazioni da recuperare nell'html
     * @param ruoloDest il ruolo a cui effettuare la consegna
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping({"/consegna/{ruolo}", "/consegna"})
    public String viewConsegna(final Model model, @PathVariable(name = "ruolo", required = false) String ruoloDest) {
        Set<String> possibiliDestinatari = consegnaService.possibiliDestinatari();

        if (ruoloDest == null) {
            ruoloDest = "";
        }

        if (possibiliDestinatari.size() == 1 && ruoloDest.equals("")) {
            ruoloDest = possibiliDestinatari.iterator().next();
        }

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
    public String sendDocumento(final Model model, @RequestParam final MultipartFile file, @RequestParam final String destinatariPost) {
        if (!Character.isDigit(destinatariPost.charAt(0))) {
            consegnaService.sendDocumento(null, destinatariPost, file);
        } else {
            String[] destinatariString = destinatariPost.split(",");
            int[] destinatariInt = new int[destinatariString.length];
            int i = 0;

            for (String p : destinatariString) {
                destinatariInt[i++] = Integer.parseInt(p);
            }
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
    public String findConsegneInviate(final Model model, @RequestParam("name") final String name) {
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
    public String findConsegneRicevute(final Model model, @RequestParam("name") final String name) {
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
    private List<Consegna> consegneFilter(final List<Consegna> consegne, final String name) {
        List<Consegna> consegneReturn = new ArrayList<>();

        for (Consegna c : consegne) {
            if (c.getDocumento().getNome().toLowerCase().contains(name.toLowerCase()) || name.equals("")) {
                consegneReturn.add(c);
            }
        }
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
    public ResponseEntity<Resource> downloadDocumento(final Model model, @PathVariable("idDocumento") final int idDocumento) {
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        Consegna consegna = consegnaService.findConsegnaByDocumentoAndDestinatario(idDocumento, personaLoggata.getId());

        if (consegna == null) {
            consegna = consegnaService.findConsegnaByDocumento(idDocumento);
        }
        Set<Role> role = personaLoggata.getUser().getRoles();
        List<String> ruoliString = new ArrayList<>();

        for (Role r : role) {
            ruoliString.add(r.getName());
        }
        boolean downloadConsentito = false;

        if (consegna.getMittente().getId() == personaLoggata.getId()) {
            downloadConsentito = true;
        } else if (consegna.getLocazione().equals(Consegna.USER_LOCAZIONE)) {
            if (consegna.getDestinatario().getId() == personaLoggata.getId()) {
                downloadConsentito = true;
            }
        } else {
            if ((consegna.getLocazione().equalsIgnoreCase(Consegna.PQA_LOCAZIONE) && (ruoliString.contains(Role.PQA_ROLE))) || (consegna.getLocazione().equalsIgnoreCase(Consegna.NDV_LOCAZIONE) && (ruoliString.contains(Role.NDV_ROLE)))) {
                downloadConsentito = true;
            }
        }

        if (downloadConsentito) {
            Documento documento = documentoService.findDocumentoById(idDocumento);
            Resource resource = documentoService.loadAsResource(documento);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(documento.getFormat()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + documento.getNome() + "\"")
                    .body(resource);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "https://i.makeagif.com/media/6-18-2016/i4va3h.gif");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    /**
     * Modifica lo stato di una consegna in APPROVATA tramite il suo id
     *
     * @param model      per salvare informazioni da recuperare nell'html
     * @param idConsegna l'id della consegna da approvare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/consegna/approva/{id}")
    public String approvaConsegna(final Model model, @PathVariable("id") final int idConsegna) {
        consegnaService.approvaConsegna(idConsegna);
        List<Consegna> consegne = consegnaService.consegneRicevute();
        model.addAttribute("consegne", consegneFilter(consegne, ""));
        return "consegna/documenti-ricevuti";
    }

    /**
     * Modifica lo stato di una consegna in RIFIUTATA tramite il suo id
     *
     * @param model      per salvare informazioni da recuperare nell'html
     * @param idConsegna l'id della consegna da rifiutare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/consegna/rifiuta/{id}")
    public String rifiutaConsegna(final Model model, @PathVariable("id") final int idConsegna) {
        consegnaService.rifiutaConsegna(idConsegna);
        List<Consegna> consegne = consegnaService.consegneRicevute();
        model.addAttribute("consegne", consegneFilter(consegne, ""));
        return "consegna/documenti-ricevuti";
    }
}
