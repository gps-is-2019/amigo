package it.unisa.Amigo.consegna.services;

import org.springframework.web.multipart.MultipartFile;

public interface ConsegnaService {
    void sendDocumento(int[] idDestinatari, MultipartFile file);
}
