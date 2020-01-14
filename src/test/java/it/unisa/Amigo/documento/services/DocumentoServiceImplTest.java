package it.unisa.Amigo.documento.services;

import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.exceptions.StorageFileNotFoundException;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.domain.Task;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class DocumentoServiceImplTest {
    @InjectMocks
    private DocumentoServiceImpl documentoService;

    @Mock
    private DocumentoDAO documentoDAO;

    @ParameterizedTest
    @MethodSource("provideAddDocumento")
    void addDocumento(final Documento doc, final String fileName, final byte[] bytes, final String mimeType) throws MalformedURLException {
        when(documentoDAO.save(any(Documento.class))).thenReturn(doc);
        documentoService.addDocumento(fileName, bytes, mimeType);
        assertThat((documentoService.loadAsResource(doc)).exists());
    }

    private static Stream<Arguments> provideAddDocumento() {

        Documento doc1 = new Documento("src/main/resources/documents/1", LocalDate.now(), "test.txt", false, "text/plain");
        doc1.setId(1);
        Documento doc2 = new Documento("src/main/resources/documents/2", LocalDate.now(), "test.txt", false, "text/plain");
        doc2.setId(2);
        Documento doc3 = new Documento("src/main/resources/documents/3", LocalDate.now(), "test.txt", false, "text/plain");
        doc3.setId(3);

        return Stream.of(
                Arguments.of(doc1, "1", "1".getBytes(), "text/plain"),
                Arguments.of(doc2, "2", "2".getBytes(), "text/plain"),
                Arguments.of(doc3, "3", "3".getBytes(), "text/plain")
        );
    }

    @ParameterizedTest
    @MethodSource("provideUpdateDocumento")
    void updateDocumento(final Documento expectedDocumento) {
        when(documentoDAO.save(expectedDocumento)).thenReturn(expectedDocumento);
        Documento actualDocumento = documentoService.updateDocumento(expectedDocumento);
        assertEquals(expectedDocumento, actualDocumento);
    }

    private static Stream<Arguments> provideUpdateDocumento() {

        Documento doc1 = new Documento("src/main/resources/documents/1", LocalDate.now(), "test.txt", false, "text/plain");
        doc1.setId(1);
        Documento doc2 = new Documento("src/main/resources/documents/2", LocalDate.now(), "test.txt", false, "text/plain");
        doc2.setId(2);
        Documento doc3 = new Documento("src/main/resources/documents/3", LocalDate.now(), "test.txt", false, "text/plain");
        doc3.setId(3);

        return Stream.of(
                Arguments.of(doc1),
                Arguments.of(doc2),
                Arguments.of(doc3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideLoadAsResource")
    void loadAsResource(final Documento expectedDocumento, final Resource expectedResouce) {
        when(documentoDAO.save(expectedDocumento)).thenReturn(expectedDocumento);
        Resource actualResource = null;
        actualResource = documentoService.loadAsResource(expectedDocumento);
        assertEquals(expectedResouce, actualResource);
    }

    private static Stream<Arguments> provideLoadAsResource() throws MalformedURLException {

        Documento doc1 = new Documento("src/main/resources/documents/1", LocalDate.now(), "test.txt", false, "text/plain");
        doc1.setId(1);
        Documento doc2 = new Documento("src/main/resources/documents/2", LocalDate.now(), "test.txt", false, "text/plain");
        doc2.setId(2);
        Documento doc3 = new Documento("src/main/resources/documents/3", LocalDate.now(), "test.txt", false, "text/plain");
        doc3.setId(3);

        Resource resource1 = new UrlResource(Paths.get(doc1.getPath()).toUri());
        Resource resource2 = new UrlResource(Paths.get(doc2.getPath()).toUri());
        Resource resource3 = new UrlResource(Paths.get(doc3.getPath()).toUri());

        return Stream.of(
                Arguments.of(doc1, resource1),
                Arguments.of(doc2, resource2),
                Arguments.of(doc3, resource3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideLoadAsResourceError1")
    void loadAsResourceError1(final Documento expectedDocumento) {
        when(documentoDAO.save(expectedDocumento)).thenReturn(expectedDocumento);
        StorageFileNotFoundException execeptionThrows = assertThrows(StorageFileNotFoundException.class, () -> {
            Resource actualResource = documentoService.loadAsResource(expectedDocumento);
        });
        assertEquals("Could not read file: " + expectedDocumento.getNome(), execeptionThrows.getMessage());
    }

    private static Stream<Arguments> provideLoadAsResourceError1() throws MalformedURLException {

        Documento doc1 = new Documento("src/main/resources/documents/4", LocalDate.now(), "test.txt", false, "text/plain");
        doc1.setId(1);
        Documento doc2 = new Documento("src/main/resources/documents/5", LocalDate.now(), "test.txt", false, "text/plain");
        doc2.setId(2);
        Documento doc3 = new Documento("src/main/resources/documents/6", LocalDate.now(), "test.txt", false, "text/plain");
        doc3.setId(3);

        Resource resource1 = new UrlResource(Paths.get(doc1.getPath()).toUri());
        Resource resource2 = new UrlResource(Paths.get(doc2.getPath()).toUri());
        Resource resource3 = new UrlResource(Paths.get(doc3.getPath()).toUri());

        return Stream.of(
                Arguments.of(doc1, resource1),
                Arguments.of(doc2, resource2),
                Arguments.of(doc3, resource3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideApprovedDocuments")
    void approvedDocuments(ArrayList<Documento> expectedDocumenti, Supergruppo expectedSupergruppo) {
        when(documentoDAO.findAllByTask_Supergruppo_IdAndTask_Stato(expectedSupergruppo.getId(), "approvato")).thenReturn(expectedDocumenti);

        List<Documento> actualDocumenti = documentoService.approvedDocuments(expectedSupergruppo.getId());
        assertEquals(expectedDocumenti, actualDocumenti);
    }

    private static Stream<Arguments> provideApprovedDocuments() throws MalformedURLException {
        LocalDate tmpDate = LocalDate.now();
        Supergruppo supergruppo1 = new Supergruppo("GAQD Informatica", "gruppo", true);
        supergruppo1.setId(1);
        Supergruppo supergruppo2 = new Supergruppo("Robe", "commisione", false);
        supergruppo2.setId(2);
        Supergruppo supergruppo3 = new Supergruppo("Boh", "gruppo", true);
        supergruppo3.setId(3);

        Documento doc1 = new Documento("src/main/resources/documents/4", LocalDate.now(), "test.txt", false, "text/plain");
        doc1.setId(1);
        Documento doc2 = new Documento("src/main/resources/documents/5", LocalDate.now(), "test.txt", false, "text/plain");
        doc2.setId(2);
        Documento doc3 = new Documento("src/main/resources/documents/6", LocalDate.now(), "test.txt", false, "text/plain");
        doc3.setId(3);

        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "approvato");
        task1.setDocumento(doc1);
        task1.setSupergruppo(supergruppo1);
        Task task2 = new Task("descrizione 2", tmpDate, "Task 2", "approvato");
        task2.setDocumento(doc2);
        task2.setSupergruppo(supergruppo2);
        Task task3 = new Task("descrizione 3", tmpDate, "Task 3", "approvato");
        task3.setDocumento(doc3);

        ArrayList<Documento> documenti1 = new ArrayList<>();
        documenti1.add(doc1);
        ArrayList<Documento> documenti2 = new ArrayList<>();
        documenti2.add(doc2);
        ArrayList<Documento> documenti3 = new ArrayList<>();
        documenti2.add(doc3);

        return Stream.of(
                Arguments.of(documenti1, supergruppo1),
                Arguments.of(documenti2, supergruppo2),
                Arguments.of(documenti3, supergruppo3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSearchDocumenti")
    void searchDocumenti(final List<Documento> expectedDocumenti) {
        Documento example = new Documento();
        example.setNome("test");
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        when(documentoDAO.findAll(Example.of(example, matcher))).thenReturn(expectedDocumenti);
        List<Documento> actualDocumenti = documentoService.searchDocumenti(example);
        assertEquals(expectedDocumenti, actualDocumenti);
    }

    private static Stream<Arguments> provideSearchDocumenti() throws MalformedURLException {
        Documento doc1 = new Documento("src/main/resources/documents/4", LocalDate.now(), "test.txt", false, "text/plain");
        doc1.setId(1);
        Documento doc2 = new Documento("src/main/resources/documents/5", LocalDate.now(), "test.txt", false, "text/plain");
        doc2.setId(2);
        Documento doc3 = new Documento("src/main/resources/documents/6", LocalDate.now(), "test.txt", false, "text/plain");
        doc3.setId(3);

        ArrayList<Documento> documenti1 = new ArrayList<>();
        documenti1.add(doc1);
        ArrayList<Documento> documenti2 = new ArrayList<>();
        documenti2.add(doc2);
        ArrayList<Documento> documenti3 = new ArrayList<>();
        documenti2.add(doc3);

        return Stream.of(
                Arguments.of(documenti1),
                Arguments.of(documenti2),
                Arguments.of(documenti3)
        );
    }
}

