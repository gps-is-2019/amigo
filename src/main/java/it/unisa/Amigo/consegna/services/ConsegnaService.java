package it.unisa.Amigo.consegna.services;

import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConsegnaService {
    void sendDocumento(int[] idDestinatari, MultipartFile file);
    ResponseEntity<Resource> downloadDocumento(int idDocument);
    List<Consegna> consegneInviate(Persona mittente);
    List<Consegna> consegneRicevute(Persona destinatario);
}
