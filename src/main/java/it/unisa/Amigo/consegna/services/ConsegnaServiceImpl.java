package it.unisa.Amigo.consegna.services;

import it.unisa.Amigo.autenticazione.domain.Role;
import it.unisa.Amigo.autenticazione.services.AuthService;
import it.unisa.Amigo.consegna.dao.ConsegnaDAO;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.services.DocumentoService;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Questa classe implementa i metodi  per la logica di Business del sottositema "Consegna"
 */
@Service
@RequiredArgsConstructor
public class ConsegnaServiceImpl implements ConsegnaService {

    private final ConsegnaDAO consegnaDAO;

    private final DocumentoService documentoService;

    private final GruppoService gruppoService;

    private final AuthService authService;

    /**
     * Permette il download di un documento.
     *
     * @param idDocumento @{@link Documento} da scaricare.
     * @return Resource del documento associato.
     */
    @Override
    public Resource getResourceFromDocumentoWithId(final Integer idDocumento) throws MalformedURLException {
        return documentoService.loadAsResource(documentoService.findDocumentoById(idDocumento));
    }

    /**
     * Effettua la consegna di un documento ad uno o più destinatari
     *
     * @param idDestinatari gli id dei destinatari che riceveranno il documento
     * @param fileName      il nome del file
     * @param bytes         il file da allegare alla consegna
     * @param mimeType      il formato del file
     */
    @Override
    public List<Consegna> sendDocumento(final int[] idDestinatari, final String locazione, final String fileName, final byte[] bytes, final String mimeType) {
        Documento doc = documentoService.addDocumento(fileName, bytes, mimeType);
        List<Consegna> result = new ArrayList<>();
        if (idDestinatari != null) {
            for (int id : idDestinatari) {
                Consegna consegna = new Consegna();
                consegna.setDataConsegna(LocalDate.now());
                consegna.setStato("NO_VALUTARE");
                consegna.setDocumento(doc);
                consegna.setMittente(gruppoService.getCurrentPersona());
                consegna.setLocazione(Consegna.USER_LOCAZIONE);
                consegna.setDestinatario(gruppoService.findPersona(id));
                result.add(consegna);
                consegnaDAO.save(consegna);
            }
        } else {
            Consegna consegna = new Consegna();
            consegna.setDataConsegna(LocalDate.now());
            consegna.setStato("DA_VALUTARE");
            consegna.setDocumento(doc);
            consegna.setMittente(gruppoService.getCurrentPersona());
            if (locazione.equalsIgnoreCase(Consegna.PQA_LOCAZIONE)) {
                consegna.setLocazione(Consegna.PQA_LOCAZIONE);
            }
            if (locazione.equalsIgnoreCase(Consegna.NDV_LOCAZIONE)) {
                consegna.setLocazione(Consegna.NDV_LOCAZIONE);
            }
            result.add(consegna);
            consegnaDAO.save(consegna);
        }
        return result;
    }

    /**
     * Recupera la lista delle consegne inviate dall'utente loggato
     *
     * @return la lista delle consegne inviate
     */
    @Override
    public List<Consegna> consegneInviate() {
        return consegnaDAO.findAllByMittente(gruppoService.getCurrentPersona());
    }

    /**
     * Recupera la lista delle consegna ricevute dall'utente loggato
     *
     * @return la lista delle consegne ricevute
     */
    @Override
    public List<Consegna> consegneRicevute() {
        Persona personaLoggata = gruppoService.getCurrentPersona();
        Set<Role> ruoli = personaLoggata.getUser().getRoles();
        List<String> ruoliString = new ArrayList<>();

        List<Consegna> consegneReturn = consegnaDAO.findAllByDestinatario(personaLoggata);

        for (Role r : ruoli) {
            ruoliString.add(r.getName());
        }
        if (ruoliString.contains(Role.PQA_ROLE)) {
            consegneReturn.addAll(consegnaDAO.findAllByLocazione(Consegna.PQA_LOCAZIONE));
        }

        if (ruoliString.contains(Role.NDV_ROLE)) {
            consegneReturn.addAll(consegnaDAO.findAllByLocazione(Consegna.NDV_LOCAZIONE));
        }
        return consegneReturn;
    }

    /**
     * Recupera una consegna tramite l'id del documento ad esso associata
     *
     * @param idDocumento l'id del documento
     * @return la consegna
     */
    @Override
    public Consegna findConsegnaByDocumento(final int idDocumento) {
        return consegnaDAO.findByDocumento_Id(idDocumento);
    }

    /**
     * Controlla se l'utente loggato ha il permesso di visualizzare il documento richiesto
     *
     * @param consegna la consegna alla quale si vuole accedere
     * @return true se l'utente loggato ha il permesso di accedere, false altrimenti
     */
    @Override
    public boolean currentPersonaCanOpen(final Consegna consegna) {
        Persona currentPersona = gruppoService.getCurrentPersona();

        Set<Role> role = authService.getCurrentUserRoles();

        if (consegna.getMittente().equals(currentPersona) || (consegna.getDestinatario() != null && currentPersona.equals(consegna.getDestinatario()))) {
            return true;
        } else {
            return (consegna.getLocazione().equalsIgnoreCase(Consegna.PQA_LOCAZIONE) && (role.contains(new Role(Role.PQA_ROLE)))) || (consegna.getLocazione().equalsIgnoreCase(Consegna.NDV_LOCAZIONE) && (role.contains(new Role(Role.NDV_ROLE))));
        }

    }

    /**
     * Recupera tutti i possibili ruoli destinatari a cui effettuare una consegna, in base ai permessi dell'utente loggato
     *
     * @return i destinatari
     */
    public Set<String> possibiliDestinatari() {
        Persona personaLoggata = gruppoService.getCurrentPersona();
        Set<Role> ruoli = personaLoggata.getUser().getRoles();
        Set<String> ruoliString = new HashSet<>();

        for (Role r : ruoli) {
            if (r.getName().equalsIgnoreCase(Role.PQA_ROLE)) {
                ruoliString.add(Role.CAPOGRUPPO_ROLE);
                ruoliString.add(Role.NDV_ROLE);
            }
            if (r.getName().equalsIgnoreCase(Role.CPDS_ROLE)) {
                ruoliString.add(Role.NDV_ROLE);
                ruoliString.add(Role.PQA_ROLE);
            }
            if (r.getName().equalsIgnoreCase(Role.CAPOGRUPPO_ROLE)) {
                ruoliString.add(Role.PQA_ROLE);
            }
        }

        return ruoliString;
    }

    /**
     * Modifica lo stato di una consegna in APPROVATA tramite il suo id
     *
     * @param idConsegna id della consegna
     */
    @Override
    public void approvaConsegna(final int idConsegna) {
        Consegna consegna = consegnaDAO.findById(idConsegna).get();
        consegna.setStato("APPROVATA");
        consegnaDAO.save(consegna);
    }

    /**
     * Modifica lo stato di una consegna in RIFIUTATA tramite il suo id
     *
     * @param idConsegna id della consegna
     */
    @Override
    public void rifiutaConsegna(final int idConsegna) {
        Consegna consegna = consegnaDAO.findById(idConsegna).get();
        consegna.setStato("RIFIUTATA");
        consegnaDAO.save(consegna);
    }

    /**
     * Inoltra un documento da un supergruppo al PQA
     *
     * @param doc il documento da inoltrare
     * @return la consegna relativa al documento inoltrato
     */
    @Override
    public Consegna inoltraPQAfromGruppo(final Documento doc) {
        Consegna consegna = new Consegna();
        consegna.setDataConsegna(LocalDate.now());
        consegna.setStato("DA_VALUTARE");
        consegna.setDocumento(doc);
        consegna.setMittente(gruppoService.getCurrentPersona());
        consegna.setLocazione(Consegna.PQA_LOCAZIONE);
        consegnaDAO.save(consegna);
        doc.setConsegna(consegna);
        return consegna;
    }

    /**
     * Recupera tutte le persone aventi uno specifico ruolo
     *
     * @param role il ruolo delle persone da recuperare
     * @return le persone con il ruolo richiesto
     */
    @Override
    public List<Persona> getDestinatariByRoleString(final String role) {
        return gruppoService.findAllByRuolo(role);
    }
}
