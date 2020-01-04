package it.unisa.Amigo.documento.service;

import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.exceptions.StorageException;
import it.unisa.Amigo.documento.exceptions.StorageFileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

/**
 * Questa classe implementa i metodi  per la logica di Business del sottosistema "Documento"
 */
@Service
@RequiredArgsConstructor
public class DocumentoServiceImpl implements DocumentoService {

    private static final String BASE_PATH = "src/main/resources/documents/";

    private final DocumentoDAO documentoDAO;

    private String storeFile(MultipartFile file, int idDoc) {
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
                Files.copy(inputStream, Paths.get(BASE_PATH).resolve(idDoc + ""), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
        return BASE_PATH + idDoc;
    }

    /**
     * Esegue il salvataggio di un file su file system, crea un documento @{@link Documento} e lo salva all'interno del database.
     *
     * @param file da salvare su file system.
     * @return il documento salvato nel database contenente la path del file salvato.
     */
    @Override
    @Transactional
    public Documento addDocumento(MultipartFile file) {
        Documento doc = new Documento();
        doc.setDataInvio(LocalDate.now());
        doc.setNome(file.getOriginalFilename());
        doc.setInRepository(false);
        doc.setFormat(file.getContentType());
        doc = documentoDAO.save(doc);
        String path = storeFile(file, doc.getId());
        doc.setPath(path);
        return updateDocumento(doc);
    }

    /**
     * Esegue il salvataggio di un documento @{@link Documento} all'interno del database.
     *
     * @param documento da salvare su database.
     * @return il documento salvato nel database.
     */
    @Override
    public Documento updateDocumento(Documento documento) {
        return documentoDAO.save(documento);
    }

    /**
     * Esegue il prelievo del file in base alla path presente nel documento @{@link Documento} passato come parametro.
     *
     * @param documento in cui è presente la path del file da scaricare.
     * @return resource contenente il file prelevato dal file system.
     */
    public Resource loadAsResource(Documento documento) {
        try {
            Resource resource = new UrlResource(Paths.get(documento.getPath()).toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + documento.getNome());
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + documento.getNome(), e);
        }
    }

    /**
     * Ritorna il documento @{@link Documento} con id passato come parametro ricercandolo all'interno del database.
     *
     * @param idDocumento documento che si vuole ottenere.
     * @return documento con id uguale a idDocumento.
     */
    @Override
    public Documento findDocumento(int idDocumento) {
        return documentoDAO.findById(idDocumento).get();
    }

    /**
     * Ritorna una lista di documenti il cui nome contiene la stringa passata come parametro.
     *
     * @param nameDocumento stringa da ricercare nel nome dei documenti.
     * @return lista di documenti contenenti la stringa ricercata.
     */
    @Override
    public List<Documento> searchDocumenti(String nameDocumento) {
        return documentoDAO.findAllByNomeContains(nameDocumento);
    }
}