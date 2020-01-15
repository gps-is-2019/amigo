
package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RepositoryServiceImplIT {

    @Autowired
    private RepositoryServiceImpl repositoryService;

    @Autowired
    private DocumentoServiceImpl documentoService;

    @Autowired
    private DocumentoDAO documentoDAO;

    @AfterEach
    void afterEach() {
        documentoDAO.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideAddDocumentoInRepository")
    void addDocumentoInRepository(String fileName, byte[] bytes, String mimeType) {
        Boolean expectedValue = repositoryService.addDocumentoInRepository(fileName, bytes, mimeType);
        assertEquals(true, expectedValue);

    }

    private static Stream<Arguments> provideAddDocumentoInRepository() {
        return Stream.of(
                Arguments.of("test.txt", "ciao mondo".getBytes(), "text/plain")
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetDocumentoAsResource")
    void getDocumentoAsResource(final Documento expectedDocumento, final Resource expectedResouce) {
        documentoDAO.save(expectedDocumento);
        Resource actualResource = repositoryService.getDocumentoAsResource(expectedDocumento);
        assertEquals(expectedResouce, actualResource);
    }

    private static Stream<Arguments> provideGetDocumentoAsResource() throws MalformedURLException {
        Documento doc1 = new Documento("src/test/resources/documents/1", LocalDate.now(), "test.txt", true, "text/plain");
        Documento doc2 = new Documento("src/test/resources/documents/2", LocalDate.now(), "test.txt", true, "text/plain");
        Documento doc3 = new Documento("src/test/resources/documents/3", LocalDate.now(), "test.txt", true, "text/plain");

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
    @MethodSource("provideSerarchDcoumentInRepository")
    void serarchDcoumentInRepository(final Documento documentoExample, final String name) {
        documentoDAO.save(documentoExample);
        List<Documento> expectedDocumenti = documentoService.searchDocumenti(documentoExample);
        List<Documento> actualDocumenti = repositoryService.searchDocumentInRepository(name);
        assertEquals(actualDocumenti, expectedDocumenti);
    }

    private static Stream<Arguments> provideSerarchDcoumentInRepository() {
        String nome1 = "test1";
        Documento documento1 = new Documento();
        documento1.setInRepository(true);
        documento1.setNome(nome1);

        String nome2 = "test2";
        Documento documento2 = new Documento();
        documento2.setInRepository(true);
        documento2.setNome(nome2);

        return Stream.of(
                Arguments.of(documento1, nome1),
                Arguments.of(documento2, null)
        );
    }
}

