package it.unisa.Amigo.documento.services;

import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.exceptions.StorageFileNotFoundException;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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

@SpringBootTest
class DocumentoServiceImplIT {

    @Autowired
    private DocumentoServiceImpl documentoService;

    @Autowired
    private DocumentoDAO documentoDAO;

    @Autowired
    private SupergruppoDAO supergruppoDAO;

    @Autowired
    private TaskDAO taskDAO;

    @AfterEach
    void afterEach() {
        documentoDAO.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideAddDocumento")
    void addDocumento(final Documento doc, final String fileName, final byte[] bytes, final String mimeType) throws MalformedURLException {
        documentoDAO.save(doc);
        documentoService.addDocumento(fileName, bytes, mimeType);
        assertThat((documentoService.loadAsResource(doc)).exists());
    }

    private static Stream<Arguments> provideAddDocumento() {

        Documento doc1 = new Documento("src/test/resources/documents/1", LocalDate.now(), "test.txt", false, "text/plain");
        Documento doc2 = new Documento("src/test/resources/documents/2", LocalDate.now(), "test.txt", false, "text/plain");
        Documento doc3 = new Documento("src/test/resources/documents/3", LocalDate.now(), "test.txt", false, "text/plain");

        return Stream.of(
                Arguments.of(doc1, "1", "1".getBytes(), "text/plain"),
                Arguments.of(doc2, "2", "2".getBytes(), "text/plain"),
                Arguments.of(doc3, "3", "3".getBytes(), "text/plain")
        );
    }

    @ParameterizedTest
    @MethodSource("provideUpdateDocumento")
    void updateDocumento(final Documento expectedDocumento) {
        documentoDAO.save(expectedDocumento);
        Documento actualDocumento = documentoService.updateDocumento(expectedDocumento);
        assertEquals(expectedDocumento, actualDocumento);
    }

    private static Stream<Arguments> provideUpdateDocumento() {

        Documento doc1 = new Documento("src/test/resources/documents/1", LocalDate.now(), "test.txt", false, "text/plain");
        Documento doc2 = new Documento("src/test/resources/documents/2", LocalDate.now(), "test.txt", false, "text/plain");
        Documento doc3 = new Documento("src/test/resources/documents/3", LocalDate.now(), "test.txt", false, "text/plain");

        return Stream.of(
                Arguments.of(doc1),
                Arguments.of(doc2),
                Arguments.of(doc3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideLoadAsResource")
    void loadAsResource(final Documento expectedDocumento, final Resource expectedResouce) {
        documentoDAO.save(expectedDocumento);
        Resource actualResource = null;
        try {
            actualResource = documentoService.loadAsResource(expectedDocumento);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertEquals(expectedResouce, actualResource);
    }

    private static Stream<Arguments> provideLoadAsResource() throws MalformedURLException {

        Documento doc1 = new Documento("src/test/resources/documents/1", LocalDate.now(), "test.txt", false, "text/plain");
        Documento doc2 = new Documento("src/test/resources/documents/2", LocalDate.now(), "test.txt", false, "text/plain");
        Documento doc3 = new Documento("src/test/resources/documents/3", LocalDate.now(), "test.txt", false, "text/plain");

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
        documentoDAO.save(expectedDocumento);
        StorageFileNotFoundException execeptionThrows = assertThrows(StorageFileNotFoundException.class, () -> {
            Resource actualResource = documentoService.loadAsResource(expectedDocumento);
        });
        assertEquals("Could not read file: " + expectedDocumento.getNome(), execeptionThrows.getMessage());
    }

    private static Stream<Arguments> provideLoadAsResourceError1() throws MalformedURLException {

        Documento doc1 = new Documento("src/test/resources/documents/4", LocalDate.now(), "test.txt", false, "text/plain");
        Documento doc2 = new Documento("src/test/resources/documents/5", LocalDate.now(), "test.txt", false, "text/plain");
        Documento doc3 = new Documento("src/test/resources/documents/6", LocalDate.now(), "test.txt", false, "text/plain");

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
    void approvedDocuments(ArrayList<Documento> expectedDocumenti, Supergruppo expectedSupergruppo, Task task) {
        supergruppoDAO.save(expectedSupergruppo);
        taskDAO.save(task);

        List<Documento> actualDocumenti = documentoService.approvedDocuments(expectedSupergruppo.getId());
        assertEquals(expectedDocumenti, actualDocumenti);
    }

    private static Stream<Arguments> provideApprovedDocuments() throws MalformedURLException {
        LocalDate tmpDate = LocalDate.now();
        Supergruppo supergruppo1 = new Supergruppo("GAQD Informatica", "gruppo", true);
        Supergruppo supergruppo2 = new Supergruppo("Robe", "commisione", false);
        Supergruppo supergruppo3 = new Supergruppo("Boh", "gruppo", true);

        Documento doc1 = new Documento("src/test/resources/documents/1", LocalDate.now(), "test.txt", false, "text/plain");
        Documento doc2 = new Documento("src/test/resources/documents/2", LocalDate.now(), "test.txt", false, "text/plain");
        Documento doc3 = new Documento("src/test/resources/documents/3", LocalDate.now(), "test.txt", false, "text/plain");

        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "approvato");
        task1.setDocumento(doc1);
        doc1.setTask(task1);
        task1.setSupergruppo(supergruppo1);
        supergruppo1.addTask(task1);
        Task task2 = new Task("descrizione 2", tmpDate, "Task 2", "approvato");
        task2.setDocumento(doc2);
        doc2.setTask(task2);
        task2.setSupergruppo(supergruppo2);
        supergruppo2.addTask(task2);
        Task task3 = new Task("descrizione 3", tmpDate, "Task 3", "approvato");
        task3.setDocumento(doc3);
        doc3.setTask(task3);
        task3.setSupergruppo(supergruppo3);
        supergruppo3.addTask(task3);

        ArrayList<Documento> documenti1 = new ArrayList<>();
        documenti1.add(doc1);
        ArrayList<Documento> documenti2 = new ArrayList<>();
        documenti2.add(doc2);
        ArrayList<Documento> documenti3 = new ArrayList<>();
        documenti3.add(doc3);

        return Stream.of(
                Arguments.of(documenti1, supergruppo1, task1),
                Arguments.of(documenti2, supergruppo2, task2),
                Arguments.of(documenti3, supergruppo3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSearchDocumenti")
    void searchDocumenti(final List<Documento> expectedDocumenti, String nome) {
        Documento example = new Documento();
        example.setNome(nome);
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        documentoDAO.saveAll(expectedDocumenti);
        List<Documento> actualDocumenti = documentoService.searchDocumenti(example);
        assertEquals(expectedDocumenti, actualDocumenti);
    }

    private static Stream<Arguments> provideSearchDocumenti() throws MalformedURLException {
        Documento doc1 = new Documento("src/test/resources/documents/4", LocalDate.now(), "test1.txt", false, "text/plain");
        Documento doc2 = new Documento("src/test/resources/documents/5", LocalDate.now(), "test2.txt", false, "text/plain");
        Documento doc3 = new Documento("src/test/resources/documents/6", LocalDate.now(), "test3.txt", false, "text/plain");

        ArrayList<Documento> documenti1 = new ArrayList<>();
        documenti1.add(doc1);
        ArrayList<Documento> documenti2 = new ArrayList<>();
        documenti2.add(doc2);
        ArrayList<Documento> documenti3 = new ArrayList<>();
        documenti3.add(doc3);

        return Stream.of(
                Arguments.of(documenti1, doc1.getNome()),
                Arguments.of(documenti2, doc2.getNome()),
                Arguments.of(documenti3, doc3.getNome())
        );
    }
}