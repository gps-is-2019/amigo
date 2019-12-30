package it.unisa.Amigo.documento.services;

import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DocumentoServiceImplTest {

    @InjectMocks
    private DocumentoServiceImpl documentoService;

    @Mock
    private DocumentoDAO documentoDAO;

    @Test
    void addDocumento(){
        documentoService.addDocumento(new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()));
        Documento documento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(), "test.txt", false, "text/plain");
        assertThat((documentoService.loadAsResource(documento)).exists());
    }

    @Test
    void updateDocumento(){
        documentoService.addDocumento(new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()));
        Documento documento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(), "test.txt", false, "text/plain");
        assertThat((documentoService.loadAsResource(documento)).exists());
    }

/*
    @Test
    void downloadDocumentoFromRepository(){
        Persona persona = new Persona("Roberto", "De Prisco", "Responsabile PQA");
        when(gruppoService.visualizzaPersonaLoggata()).thenReturn(persona);
        Documento documentoExpected = new Documento("src/main/resources/documents/test3.txt", LocalDate.now(),
                "test3.txt", true, "text/plain");
        when(documentoDAO.findByIdAndInRepository(documentoExpected.getId(), true)).thenReturn(documentoExpected);
        Documento actualDocumento = documentoService.downloadDocumentoFromRepository(documentoExpected.getId());
        assertEquals(documentoExpected, actualDocumento);
    }

    @Test
    void downloadDocumentoFromConsegna(){
        Persona persona = new Persona("Roberto", "De Prisco", "Responsabile PQA");
        when(gruppoService.visualizzaPersonaLoggata()).thenReturn(persona);

        Documento documentoExpected = new Documento("src/main/resources/documents/test4.txt", LocalDate.now(),
                "test4.txt", true, "text/plain");
        Consegna consegna = new Consegna(LocalDate.now(), "Da valutare");
        documentoExpected.setConsegna(consegna);
        consegna.setMittente(persona);

        when(documentoDAO.findById(documentoExpected.getId())).thenReturn(Optional.of(documentoExpected));
        Documento actualDocumento = documentoService.downloadDocumentoFromConsegna(documentoExpected.getId());
        assertEquals(documentoExpected, actualDocumento);
    }

    @Test
    void downloadDocumentoFromTask(){
        Persona persona = new Persona("Roberto", "De Prisco", "Responsabile PQA");
        when(gruppoService.visualizzaPersonaLoggata()).thenReturn(persona);

        Documento documentoExpected = new Documento("src/main/resources/documents/test5.txt", LocalDate.now(),
                "test5.txt", true, "text/plain");

        Supergruppo gruppo = new Supergruppo("Gruppo", "Test", true);
        gruppo.setResponsabile(persona);

        Task task = new Task("Descrizione", LocalDate.now(), "Task", "Da valutare");
        task.setSupergruppo(gruppo);
        documentoExpected.setTask(task);

        when(documentoDAO.findById(documentoExpected.getId())).thenReturn(Optional.of(documentoExpected));
        Documento actualDocumento = documentoService.downloadDocumentoFromTask(documentoExpected.getId());
        assertEquals(documentoExpected, actualDocumento);
    }

    @Test
    void searchDocumentoFromRepository(){
        Persona persona = new Persona("Roberto", "De Prisco", "Responsabile PQA");
        when(gruppoService.visualizzaPersonaLoggata()).thenReturn(persona);
        Documento documento1 = new Documento("src/main/resources/documents/test6.txt", LocalDate.now(), "test6.txt", true, "text/plain");
        List<Documento> documentiExpected = new ArrayList<>();
        documentiExpected.add(documento1);
        when(documentoDAO.findAllByInRepositoryAndNomeContains(true, documento1.getNome())).thenReturn(documentiExpected);
        List<Documento> actualDocumenti = documentoService.searchDocumentoFromRepository(documento1.getNome());
        assertEquals(documentiExpected,actualDocumenti);

    }
    */

}

