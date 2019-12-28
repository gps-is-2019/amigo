package it.unisa.Amigo.documento.service;

import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.task.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DocumentoServiceImpl implements DocumentoService{

    @Autowired
    private final DocumentoDAO documentoDAO;
    private final GruppoService gruppoService;

    @Override
    public boolean addDocToTask(MultipartFile file, Task task) {

        if(gruppoService.visualizzaPersonaLoggata().getId() == task.getPersona().getId()){
            Documento documento = new Documento();
            try {
                documento.setFile(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            documento.setDataInvio(LocalDate.now());
            documento.setNome(file.getOriginalFilename());
            documento.setInRepository(true);
            documento.setFormat(file.getContentType());

            documento.setTask(task);
            task.setDocumento(documento);
            documentoDAO.save(documento);
            //salvare il cambiamento di task
            return true;
        }
        return false;
    }

    @Override
    public boolean addDocToConsegna(Documento documento, Consegna consegna) {
        //documento.setConsegna(consegna);
        //consegna.setDocumento(documento);
        documentoDAO.save(documento);
        //salvare il cambiamento di consegna
        return true;
        //da vedere
    }

    @Override
    public void addDocToRepository(MultipartFile file) {
        int flag = 0;
        Set<Role> roles = gruppoService.visualizzaPersonaLoggata().getUser().getRoles();
        for(Role role: roles )
            if(role.getName().equals(Role.PQA_ROLE))
                flag = 1;

        if(flag==1){

            Documento documento = new Documento();
            try {
                documento.setFile(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            documento.setDataInvio(LocalDate.now());
            documento.setNome(file.getOriginalFilename());
            documento.setInRepository(true);
            documento.setFormat(file.getContentType());
            documentoDAO.save(documento);
        }

        //eccezzione
    }

    @Override
    public Documento downloadDocumentoFromRepository(int idDocumento) {
        return documentoDAO.findByIdAndInRepository(idDocumento,true);
    }

    @Override
    public Documento downloadDocumentoFromConsegna(int idDocumento) {
        Optional<Documento> documento = documentoDAO.findById(idDocumento);
        // consegna = documento.get().getConsegna();
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        //serve la classe consegna
        /*if((consegna.getMittente().getId() == personaLoggata.getId()) ||
             (consegna.getDestinatario().getId() == personaLoggata.getId()))
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
        //vedere come fare con l'eccezione
    }

    @Override
    public List<Documento> searchDocumentoFromRepository(String nameDocumento) {
       return documentoDAO.findAllByInRepositoryAndNomeContains(true, nameDocumento);
    }
}
