package it.unisa.Amigo.consegna.services;

import it.unisa.Amigo.consegna.domain.Consegna;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * Questa interfaccia definisce i metodi  per la logica di Business del sottositema "Consegna"
 */
public interface ConsegnaService {
    List<Consegna> sendDocumento(int[] idDestinatari, String locazione, MultipartFile file);

    List<Consegna> consegneInviate();

    List<Consegna> consegneRicevute();

    Consegna findConsegnaByDocumento(int idDocumento);

    Set<String> possibiliDestinatari();

    void approvaConsegna(int idConsegna);

    void rifiutaConsegna(int idConsegna);

    Consegna findConsegnaByDocumentoAndDestinatario(int idDocumento, int idDestinatario);
}
