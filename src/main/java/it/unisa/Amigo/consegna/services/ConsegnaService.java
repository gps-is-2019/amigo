package it.unisa.Amigo.consegna.services;

import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.web.multipart.MultipartFile;

public interface ConsegnaService {
    void sendDocumento(int idDestinatario, MultipartFile file);

}
