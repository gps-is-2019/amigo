package it.unisa.Amigo.documento.service;

import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.task.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentoServiceImpl implements DocumentoService{

    @Autowired
    private final DocumentoDAO documentoDAO;
    private final GruppoService gruppoService;


    @Override
    public boolean addDocToTask(Documento documento, Task task) {
        documento.setTask(task);
        task.setDocumento(documento);
        documentoDAO.save(documento);
        //salvare il cambiamento di task
        return true;
        //da vedere
    }
    @Override
    public boolean addDocToConsegna(Documento documento, Consegna consegna) {
        documento.setConsegna(consegna);
        //consegna.setDocumento(documento);
        documentoDAO.save(documento);
        //salvare il cambiamento di consegna
        return true;
        //da vedere
    }

    @Override
    public void addDocToRepository(Documento documento) {
        documento.setInRepository(true);
        documentoDAO.save(documento);

    }

    @Override
    public Documento downloadDocumentoFromRepository(int idDocumento) {
        return documentoDAO.findByIdAndInRepository(idDocumento,true);
    }

    @Override
    public Documento downloadDocumentoFromConsegna(int idDocumento) {
        Optional<Documento> documento = documentoDAO.findById(idDocumento);
        Consegna consegna = documento.get().getConsegna();
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        //serve la classe consegna
        /*if(consegna.getMittente().getId()== personaLoggata.getId() ||consegna.getDestinatario().getId()== personaLoggata.getId() )
            //ritorna documento
        //altrimenti eccezione*/
        return null;
    }

    @Override
    public Documento downloadDocumentoFromTask(int idDocumento) {
        Optional<Documento> documento = documentoDAO.findById(idDocumento);
        Task task = documento.get().getTask();
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        Persona responsabile = task.getSupergruppo().getResponsabile();
        if(personaLoggata.getId()==responsabile.getId())
            return documento.get();
        return null;

        //vedere come fare con l'eccezzione
    }

    @Override
    public List<Documento> searchDocumentoFromRepository(String nameDocumento) {
       return  documentoDAO.findAllByInRepositoryAndNomeContains(true,nameDocumento);
    }
}
