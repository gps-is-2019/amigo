package it.unisa.Amigo.consegna.services;

import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.core.io.Resource;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

/**
 * Questa interfaccia definisce i metodi  per la logica di Business del sottositema "Consegna"
 */
public interface ConsegnaService {


    Resource getResourceFromDocumentoWithId(Integer idDocumento) throws MalformedURLException;

    List<Consegna> sendDocumento(int[] idDestinatari, String locazione, String fileName, byte[] bytes, String mimeType);

    List<Consegna> consegneInviate();

    List<Consegna> consegneRicevute();

    Consegna findConsegnaByDocumento(int idDocumento);

    Set<String> possibiliDestinatari();

    void approvaConsegna(int idConsegna);

    void rifiutaConsegna(int idConsegna);

    boolean currentPersonaCanOpen(Consegna consegna);

    Consegna inoltraPQAfromGruppo(Documento doc);

    List<Persona> getDestinatariByRoleString(String role);
}
