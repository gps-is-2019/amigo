package it.unisa.Amigo.documento.service;

import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.exceptions.StorageFileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Questa classe implementa i metodi  per la logica di business del sottosistema "Documento"
 */
@Service
@RequiredArgsConstructor
public class DocumentoServiceImpl implements DocumentoService {

    private static String BASE_PATH = "src/main/resources/documents/";

    private final DocumentoDAO documentoDAO;

    private String storeFile(final byte[] bytes, final int idDoc) {
        Path path = Paths.get(BASE_PATH + idDoc);
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BASE_PATH + idDoc;
    }

    /**
     * Esegue il salvataggio di un file su file system, crea un documento @{@link Documento} e lo salva all'interno del database.
     *
     * @param fileName nome del file da salvare
     * @param bytes    file da salvare
     * @param mimeType formato del file da salvare
     * @return il documento salvato nel database contenente la path del file salvato.
     */
    @Override
    @Transactional
    public Documento addDocumento(final String fileName, final byte[] bytes, final String mimeType) {
        Documento doc = new Documento();
        doc.setDataInvio(LocalDate.now());
        doc.setNome(fileName);
        doc.setInRepository(false);
        doc.setFormat(mimeType);
        doc = documentoDAO.save(doc);
        String path = storeFile(bytes, doc.getId());
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
    public Documento updateDocumento(final Documento documento) {
        return documentoDAO.save(documento);
    }

    /**
     * Esegue il prelievo del file in base alla path presente nel documento @{@link Documento} passato come parametro.
     *
     * @param documento in cui è presente la path del file da scaricare.
     * @return resource contenente il file prelevato dal file system.
     */
    public Resource loadAsResource(final Documento documento) {
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
     * Recupera la lista dei documenti approvati all'interno del supergruppo passato come parametro
     *
     * @param idSupergruppo id del supergruppo dal quale prelevare i documenti
     * @return listq dei documenti approvati
     */
    @Override
    public List<Documento> approvedDocuments(final int idSupergruppo) {
        return documentoDAO.findAllByTask_Supergruppo_IdAndTask_Stato(idSupergruppo, "approvato");
    }

    /**
     * Ritorna il documento @{@link Documento} con id passato come parametro ricercandolo all'interno del database.
     *
     * @param idDocumento documento che si vuole ottenere.
     * @return documento con id uguale a idDocumento.
     */
    @Override
    public Documento findDocumentoById(final Integer idDocumento) {
        return documentoDAO.findById(idDocumento).get();

    }

    /**
     * Ritorna una lista di documenti dato un documento di confronto
     * @param example documento da utilizzare come criterio di ricerca
     *
     * @return lista di documenti che matchano la ricerca
     */
    @Override
    public List<Documento> searchDocumenti(final Documento example) {
        List<Documento> result = new ArrayList<>();
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        Iterable<Documento> iterable = documentoDAO.findAll(Example.of(example, matcher));
        for (Documento documento : iterable) {
            result.add(documento);
        }
        return result;
    }
}
