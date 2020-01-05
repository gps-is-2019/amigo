package it.unisa.Amigo.consegna.services;

import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * Questa interfaccia definisce i metodi  per la logica di Business del sottositema "Consegna"
 */
public interface ConsegnaService {
    void sendDocumento(int[] idDestinatari, String locazione, MultipartFile file);

    ResponseEntity<Resource> downloadDocumento(int idDocument);

    List<Consegna> consegneInviate();

    List<Consegna> consegneRicevute();

    Consegna findConsegnaByDocumento(int idDocumento);

    Set<String> possibiliDestinatari();
}
