package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.services.DocumentoService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class RepositoryServiceImplTest {

    @InjectMocks
    private RepositoryServiceImpl repositoryService;

    @Mock
    private DocumentoService documentoService;

    @ParameterizedTest
    @MethodSource("provideAddDocumentoInRepository")
    void addDocumentoInRepository(String fileName, final byte[] bytes, final String mimeTypee) {
        Documento expectedDocumento = new Documento();
        when(documentoService.addDocumento(fileName, bytes, mimeTypee)).thenReturn(expectedDocumento);
        expectedDocumento.setInRepository(true);
        when(documentoService.updateDocumento(expectedDocumento)).thenReturn(expectedDocumento);
        boolean actualValue = repositoryService.addDocumentoInRepository(fileName, bytes, mimeTypee);
        assertEquals(true, actualValue);
    }

    private static Stream<Arguments> provideAddDocumentoInRepository() {

        return Stream.of(
                Arguments.of("test.txt","ciao mondo".getBytes(),"text/plain")
        );
    }

    @ParameterizedTest
    @MethodSource("provideDownloadDocumento")
    void getDocumentoAsResource(final Documento expectedDocumento, final Resource expectedResource) {
        when(documentoService.loadAsResource(expectedDocumento)).thenReturn(expectedResource);
        Resource actualResource = repositoryService.getDocumentoAsResource(expectedDocumento);
        assertEquals(actualResource, expectedResource);
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

    @ParameterizedTest
    @MethodSource("provideFindDocumento")
    void findDocumento(Documento documento, Documento expectedDocumento) {
        List<Documento> documenti = new ArrayList<>();
        documenti.add(documento);
        when(documentoService.searchDocumenti(documento)).thenReturn(documenti);
        Documento actualDocumento = repositoryService.findDocumentoById(documento.getId());
        assertEquals(actualDocumento, expectedDocumento);
    }

    private static Stream<Arguments> provideFindDocumento() {
        Documento documento1 = new Documento();
        Documento documento2 = new Documento();
        documento1.setId(1);
        documento1.setInRepository(true);
        documento2.setId(2);
        documento2.setInRepository(false);
        return Stream.of(
                Arguments.of(documento1, documento1),
                Arguments.of(documento2, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSerarchDcoumentInRepository")
    void serarchDcoumentInRepository(final Documento documentoExample, final String nameDocumento) {
        List<Documento> expectedDocumenti = new ArrayList<>();
        when(documentoService.searchDocumenti(documentoExample)).thenReturn(expectedDocumenti);
        List<Documento> actualDocumenti = repositoryService.searchDocumentInRepository(nameDocumento);
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
                Arguments.of(documento1,nome1),
                Arguments.of(documento2,null)
        );
    }
}
