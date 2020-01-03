package it.unisa.Amigo.consegna.services;

import it.unisa.Amigo.consegna.dao.ConsegnaDAO;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import it.unisa.Amigo.gruppo.services.GruppoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ConsegnaServiceImpl implements ConsegnaService {

    private final ConsegnaDAO consegnaDAO;

    private final DocumentoServiceImpl documentoService;

    private final GruppoServiceImpl gruppoService;

    @Override
    public void sendDocumento(int[] idDestinatari, MultipartFile file) {
        Documento doc = documentoService.addDocumento(file);

        for (int id : idDestinatari) {
            Consegna consegna = new Consegna();
            consegna.setDataConsegna(LocalDate.now());
            consegna.setStato("da valutare");
            consegna.setDocumento(doc);
            consegna.setMittente(gruppoService.getAuthenticatedUser());
            consegna.setDestinatario(gruppoService.findPersona(id));
            consegnaDAO.save(consegna);
        }
    }
}
