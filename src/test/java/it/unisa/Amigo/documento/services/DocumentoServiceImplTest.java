package it.unisa.Amigo.documento.services;

import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.task.domain.Task;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class DocumentoServiceImplTest {

    @InjectMocks
    private DocumentoServiceImpl documentoService;

    @Mock
    private DocumentoDAO documentoDAO;

    @Mock
    private GruppoService gruppoService;


    @Test
    void addDocToTaskAndLoadAsResource(){
        Persona persona = new Persona("Roberto", "De Prisco", "Responsabile PQA");
        when(gruppoService.visualizzaPersonaLoggata()).thenReturn(persona);
        Task task = new Task();
        task.setPersona(persona);
        documentoService.addDocToTask(new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()), task);
        Documento documento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(), "test.txt", false, "text/plain");
        assertThat((documentoService.loadAsResource(documento)).exists());
    }

    @Test
    void addDocToConsegnaAndLoadAsResource(){
        Persona persona = new Persona("Roberto", "De Prisco", "Responsabile PQA");
        when(gruppoService.visualizzaPersonaLoggata()).thenReturn(persona);
        Consegna consegna = new Consegna();
        consegna.setMittente(persona);
        documentoService.addDocToConsegna(new MockMultipartFile("test1", "test1.txt",
                MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes()), consegna);
        Documento documento = new Documento("src/main/resources/documents/test1.txt", LocalDate.now(), "test1.txt", false, "text/plain");
        assertThat((documentoService.loadAsResource(documento)).exists());
    }

    @Test
    void addDocToRepositoryAndLoadAsResource(){
        Persona persona = new Persona("Roberto", "De Prisco", "Responsabile PQA");
        User user = new User();
        user.addRole(new Role(Role.PQA_ROLE));
        persona.setUser(user);
        when(gruppoService.visualizzaPersonaLoggata()).thenReturn(persona);
        documentoService.addDocToRepository(new MockMultipartFile("test2", "test2.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()));
        Documento documento = new Documento("src/main/resources/documents/test2.txt", LocalDate.now(), "test2.txt", true, "text/plain");
        assertThat((documentoService.loadAsResource(documento)).exists());
    }

    @Test
    void downloadDocumentoFromRepository(){
        Persona persona = new Persona("Roberto", "De Prisco", "Responsabile PQA");
        when(gruppoService.visualizzaPersonaLoggata()).thenReturn(persona);
        Documento documentoExpected = new Documento("src/main/resources/documents/test3.txt", LocalDate.now(),
                "test3.txt", true, "text/plain");
        when(documentoDAO.findByIdAndInRepository(documentoExpected.getId(), true)).thenReturn(documentoExpected);
        Documento documentoScaricato = documentoService.downloadDocumentoFromRepository(documentoExpected.getId());
        assertEquals(documentoExpected, documentoScaricato);
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
        Documento documentoScaricato = documentoService.downloadDocumentoFromConsegna(documentoExpected.getId());
        assertEquals(documentoExpected, documentoScaricato);
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
        Documento documentoScaricato = documentoService.downloadDocumentoFromTask(documentoExpected.getId());
        assertEquals(documentoExpected, documentoScaricato);
    }

    @Test
    void searchDocumentoFromRepository(){

    }
}

