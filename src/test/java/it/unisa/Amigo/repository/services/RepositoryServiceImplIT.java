package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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
    void addDocumentoInRepository(MultipartFile file) {

        Boolean expectedValue = repositoryService.addDocumentoInRepository(file);
        assertEquals(true, expectedValue);

    }
    private static Stream<Arguments> provideAddDocumentoInRepository() {
        MultipartFile file1 = new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes());
        MultipartFile file2 = new MockMultipartFile("testtestetst", "file.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes());

        return Stream.of(
                Arguments.of(file1),
                Arguments.of(file2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDownloadDocumento")
    void downloadDocumento(Documento expectedDocumento) {
        documentoDAO.save(expectedDocumento);
        Documento actualDocumento = documentoService.findDocumento(expectedDocumento.getId());
        assertEquals(expectedDocumento, actualDocumento);
    }
    private static Stream<Arguments> provideDownloadDocumento() {
        Documento documento1 = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "text/plain");
        Documento documento2 = new Documento("src/main/resources/documents/file.txt", LocalDate.now(),
                "test.txt", false, "text/plain");

        return Stream.of(
                Arguments.of(documento1),
                Arguments.of(documento2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSerarchDcoumentInRepository")
    void serarchDcoumentInRepository( Documento documentoExample, String name) {

        List<Documento> expectedDocumenti = documentoService.searchDocumenti(documentoExample);
        List<Documento> actualDocumenti = repositoryService.searchDocumentInRepository(name);
        assertEquals(actualDocumenti,expectedDocumenti);
    }
    private static Stream<Arguments> provideSerarchDcoumentInRepository() {
        String nome1 ="test1";
        Documento documento1 = new Documento();
        documento1.setInRepository(true);
        documento1.setNome(nome1);

        String nome2 ="test2";
        Documento documento2 = new Documento();
        documento2.setInRepository(true);
        documento2.setNome(nome2);

        return Stream.of(
                Arguments.of(documento1, nome1),
                Arguments.of(documento2, nome2)
        );
    }
}
