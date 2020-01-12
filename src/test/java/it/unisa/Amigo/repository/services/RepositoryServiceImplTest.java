package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class RepositoryServiceImplTest {

    @InjectMocks
    private RepositoryServiceImpl repositoryService;

    @Mock
    private DocumentoService documentoService;

    private static Stream<Arguments> provideAddDocumentoInRepository() {
        MultipartFile file1 = new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes());
        MultipartFile file2 = new MockMultipartFile("testtestetst", "file.txt", MediaType.TEXT_PLAIN_VALUE, "ciao mondo ciao mondo".getBytes());
        return Stream.of(
                Arguments.of(file1),
                Arguments.of(file2)
        );
    }

    private static Stream<Arguments> provideDownloadDocumento() throws MalformedURLException {
        Documento doc1 = new Documento("src/main/resources/documents/file.txt", LocalDate.now(),
                "file.txt", false, "application/txt");
        Documento doc2 = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "application/txt");
        Resource res1 = new UrlResource(Paths.get(doc1.getPath()).toUri());
        Resource res2 = new UrlResource(Paths.get(doc2.getPath()).toUri());
        return Stream.of(
                Arguments.of(doc1, res1),
                Arguments.of(doc2, res2)
        );
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
                Arguments.of(documento2, nome2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideAddDocumentoInRepository")
    void addDocumentoInRepository(MultipartFile file) {
        Documento expectedDocumento = new Documento();
        when(documentoService.addDocumento(file)).thenReturn(expectedDocumento);
        expectedDocumento.setInRepository(true);
        when(documentoService.updateDocumento(expectedDocumento)).thenReturn(expectedDocumento);
        boolean expectedValue = repositoryService.addDocumentoInRepository(file);
        assertTrue(expectedValue);
    }

    @ParameterizedTest
    @MethodSource("provideDownloadDocumento")
    void downloadDocumento(Documento expectedDocumento, Resource expectedResource) {
        when(documentoService.loadAsResource(expectedDocumento)).thenReturn(expectedResource);
        Resource actualResource = repositoryService.downloadDocumento(expectedDocumento);
        assertEquals(actualResource, expectedResource);
    }

    @Test
    void findDocumento() {
        Documento expectedDocumento = new Documento();
        when(documentoService.findDocumentoById(0)).thenReturn(expectedDocumento);
        Documento actualDocumento = repositoryService.findDocumentoById(0);
        assertEquals(actualDocumento, expectedDocumento);
    }

    /*
        @SneakyThrows
        @Test
        void downloadDocumento() {
            Documento documento = new Documento();
            when(documentoService.findDocumento(0)).thenReturn(documento);
            Resource resource = new ClassPathResource("");
            when(documentoService.loadAsResource(documento)).thenReturn(resource);
            ResponseEntity<Resource> expectedValue = new ResponseEntity<Resource>(HttpStatus.MULTI_STATUS);
            when(responseEntity.ok().
                    contentType(MediaType.parseMediaType(documento.getFormat())).
                    header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + documento.getNome() + "\"").
                    body(resource)).
                    thenReturn(expectedValue);

            ResponseEntity<Resource> actualValue = repositoryService.downloadDocumento(0);
            assertEquals(actualValue,expectedValue);

        }
    */
    @ParameterizedTest
    @MethodSource("provideSerarchDcoumentInRepository")
    void serarchDcoumentInRepository(Documento documentoExample) {
        List<Documento> expectedDocumenti = new ArrayList<>();
        when(documentoService.searchDocumenti(documentoExample)).thenReturn(expectedDocumenti);
        List<Documento> actualDocumenti = repositoryService.searchDocumentInRepository("test");
        assertEquals(actualDocumenti, expectedDocumenti);
    }
}
