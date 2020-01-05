package it.unisa.Amigo.consegna.services;

import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.consegna.dao.ConsegnaDAO;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    private final DocumentoServiceImpl documentoService;

    private final GruppoServiceImpl gruppoService;

    /**
     * Effettua la consegna di un documento ad uno o più destinatari
     *
     * @param idDestinatari gli id dei destinatari che riceveranno il documento
     * @param file          il file allegato al documento
     */
    @Override
    public void sendDocumento(int[] idDestinatari, String locazione, MultipartFile file) {
        Documento doc = documentoService.addDocumento(file);

        if (idDestinatari != null) {
            for (int id : idDestinatari) {
                Consegna consegna = new Consegna();
                consegna.setDataConsegna(LocalDate.now());
                consegna.setStato("da valutare");
                consegna.setDocumento(doc);
                consegna.setMittente(gruppoService.getAuthenticatedUser());
                consegna.setLocazione(Consegna.USER_LOCAZIONE);
                consegna.setDestinatario(gruppoService.findPersona(id));
                consegnaDAO.save(consegna);
            }
        } else {
            Consegna consegna = new Consegna();
            consegna.setDataConsegna(LocalDate.now());
            consegna.setStato("da valutare");
            consegna.setDocumento(doc);
            consegna.setMittente(gruppoService.getAuthenticatedUser());
            if (locazione.equalsIgnoreCase(Consegna.PQA_LOCAZIONE))
                consegna.setLocazione(Consegna.PQA_LOCAZIONE);
            if (locazione.equalsIgnoreCase(Consegna.NDV_LOCAZIONE))
                consegna.setLocazione(Consegna.NDV_LOCAZIONE);
            consegnaDAO.save(consegna);
        }
    }

    /**
     * Effettua il download di un documento
     *
     * @param idDocument il documento da scaricare
     * @return la pagina di visualizzazione del documento scaricato
     */
    @Override
    public ResponseEntity<Resource> downloadDocumento(int idDocument) {
        Documento documento = documentoService.findDocumento(idDocument);
        Resource resource = documentoService.loadAsResource(documento);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documento.getFormat()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + documento.getNome() + "\"")
                .body(resource);
    }

    /**
     * Recupera la lista delle consegna inviate da una persona
     *
     * @return la lista delle consegne
     */
    @Override
    public List<Consegna> consegneInviate() {
        return consegnaDAO.findAllByMittente(gruppoService.getAuthenticatedUser());
    }

    /**
     * Recupera la lista delle consegna ricevute da una persona
     *
     * @return la lista delle consegne
     */
    @Override
    public List<Consegna> consegneRicevute() {
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        Set<Role> ruoli = personaLoggata.getUser().getRoles();
        List<String> ruoliString = new ArrayList<>();

        List<Consegna> consegneReturn = consegnaDAO.findAllByDestinatario(personaLoggata);

        for (Role r : ruoli)
            ruoliString.add(r.getName());

        if (ruoliString.contains(Role.PQA_ROLE))
            consegneReturn.addAll(consegnaDAO.findAllByLocazione(Consegna.PQA_LOCAZIONE));

        if (ruoliString.contains(Role.NDV_ROLE))
            consegneReturn.addAll(consegnaDAO.findAllByLocazione(Consegna.NDV_LOCAZIONE));

        return consegneReturn;
    }

    /**
     * Recupera una consegna tramite l'id del documento ad esso associata
     *
     * @param idDocumento l'id del documento
     * @return la consegna
     */
    @Override
    public Consegna findConsegnaByDocumento(int idDocumento) {
        return consegnaDAO.findByDocumento_Id(idDocumento);
    }

    /**
     * Recupera tutti i possibili ruoli destinatari a cui effettuare una consegna
     *
     * @return i destinatari
     */
    public Set<String> possibiliDestinatari() {
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
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
}