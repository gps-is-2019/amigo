package it.unisa.Amigo.consegna.services;

import it.unisa.Amigo.consegna.domain.Consegna;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConsegnaService {
    void sendDocumento(int[] idDestinatari, MultipartFile file);
    ResponseEntity<Resource> downloadDocumento(int idDocument);
    List<Consegna> documentiInviati(int idMittente);
}
