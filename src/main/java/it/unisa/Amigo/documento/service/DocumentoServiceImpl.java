package it.unisa.Amigo.documento.service;

import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.exceptions.StorageException;
import it.unisa.Amigo.documento.exceptions.StorageFileNotFoundException;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.task.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
/**
 * Questa classe implementa i metodi  per la logica di Business del sottositema "Documento"
 */
@Service
@RequiredArgsConstructor
public class DocumentoServiceImpl implements DocumentoService{

    @Autowired
    private final DocumentoDAO documentoDAO;
    private final GruppoService gruppoService;


    private Documento storeDocumento(MultipartFile file) {
        Documento documento = new Documento();

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, Paths.get("src/main/resources/documents").resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }

        documento.setPath("src/main/resources/documents/" + filename);

        documento.setDataInvio(LocalDate.now());
        documento.setNome(file.getOriginalFilename());
        documento.setInRepository(true);
        documento.setFormat(file.getContentType());
        return documento;
    }

    /**
     *Consente di creare un documento @{@link Documento}e aggiungerlo a un task @{@link Task}
     * @param file per creare il documento
     * @param task in cui aggiungere il documento
     * @return true se la persona è l'assegnatario del task, false altrimenti
     */
    @Override
    public boolean addDocToTask(MultipartFile file, Task task) {
        if(gruppoService.visualizzaPersonaLoggata().getId() == task.getPersona().getId()){
            Documento documento = storeDocumento(file);
            documento.setTask(task);
            task.setDocumento(documento);
            documentoDAO.save(documento);
            //salvare il cambiamento di task
            return true;
        }
        return false;
    }

    /**
     **Consente di creare un documento @{@link Documento}e aggiungerlo a una consegna @{@link Consegna}
     * @param file per creare il documento
     * @param consegna in cui aggiungere il documento
     * @return true se la persona è il mittente della consegna, false altrimenti
     */
    @Override
    public boolean addDocToConsegna(MultipartFile file, Consegna consegna) {
        if(gruppoService.visualizzaPersonaLoggata().getId() == consegna.getMittente().getId()){
            Documento documento = storeDocumento(file);
            documento.setConsegna(consegna);
            consegna.setDocumento(documento);
            documentoDAO.save(documento);
            //salvare il cambiamento di consegna
            return true;
        }
        return false;
    }

    /**
     * Crea un documento @{@link Documento} e lo aggiunge alla repository d'ateneo
     * @param file per creare il documento
     */
    @Override
    public void addDocToRepository(MultipartFile file) {
        int flag = 0;
        Set<Role> roles = gruppoService.visualizzaPersonaLoggata().getUser().getRoles();
        for(Role role: roles)
            if(role.getName().equals(Role.PQA_ROLE))
                flag = 1;

        if(flag==1){

            Documento documento = storeDocumento(file);
            documentoDAO.save(documento);
        }

        //eccezione
    }

    /**
     *
     * @param documento
     * @return
     */
    public Resource loadAsResource(Documento documento) {
        try {
            Resource resource = new UrlResource(Paths.get(documento.getPath()).toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + documento.getNome());
            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + documento.getNome(), e);
        }
    }

    /**
     * Ritorna un documento @{@link Documento} presente nella repository in base al suo id
     * @param idDocumento documento che si vuole scaricare
     * @return documento
     */
    @Override
    public Documento downloadDocumentoFromRepository(int idDocumento) {
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        if (personaLoggata != null)
            return documentoDAO.findByIdAndInRepository(idDocumento,true);
        else
            return null;
    }

    /**
     * Ritorna un documento @{@link Documento} di una consegna a patto che la persona loggata sia il mittente o il destinatario
     * @param idDocumento documento che si vuole scaricare
     * @return documento
     */
    @Override
    public Documento downloadDocumentoFromConsegna(int idDocumento) {
        Optional<Documento> documento = documentoDAO.findById(idDocumento);
        Consegna consegna = documento.get().getConsegna();
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        if(personaLoggata.getId()==consegna.getMittente().getId() || personaLoggata.getId() == consegna.getDestinatario().getId())
            return documento.get();
        return null;
        //eccezione
    }

    /**
     * Ritorna un documento @{@link Documento} di un task a patto che la persona loggata sia colui che abbia assegnato il task
     * @param idDocumento documento che si vuole scaricare
     * @return documento
     */
    @Override
    public Documento downloadDocumentoFromTask(int idDocumento) {
        Optional<Documento> documento = documentoDAO.findById(idDocumento);
        Task task = documento.get().getTask();
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        Persona responsabile = task.getSupergruppo().getResponsabile();
        if(personaLoggata.getId()==responsabile.getId())
            return documento.get();
        return null;
    }

    /**
     * Ritorna una lista di documenti @{@link Documento} presenti nella repository dove il nome di ciascun documento contine la stringa passata come parametro
     * @param nameDocumento stringa da ricercare nel nome dei documenti
     * @return lista di documenti
     */
    @Override
    public List<Documento> searchDocumentoFromRepository(String nameDocumento) {
       return documentoDAO.findAllByInRepositoryAndNomeContains(true, nameDocumento);
    }
}
