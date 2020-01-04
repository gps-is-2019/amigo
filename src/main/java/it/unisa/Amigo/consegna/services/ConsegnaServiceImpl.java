package it.unisa.Amigo.consegna.services;

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
import java.util.List;

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

    @Override
    public ResponseEntity<Resource> downloadDocumento(int idDocument) {
        Documento documento = documentoService.findDocumento(idDocument);
        Resource resource = documentoService.loadAsResource(documento);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documento.getFormat()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + documento.getNome() + "\"")
                .body(resource);
    }

    @Override
    public List<Consegna> consegneInviate(Persona mittente) {
        return consegnaDAO.findAllByMittente(mittente);
    }

    @Override
    public List<Consegna> consegneRicevute(Persona destinatario) {
        return consegnaDAO.findAllByDestinatario(destinatario);
    }

    @Override
    public Consegna findConsegnaByDocumento(int idDocumento){
        return consegnaDAO.findByDocumento_Id(idDocumento);
    }
}
