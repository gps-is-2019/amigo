package it.unisa.Amigo.documento.services;

import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    void addDocumentoStoreAndLoad() {
        documentoService.addDocumento(new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()));
        Documento documento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(), "test.txt", false, "text/plain");
        assertThat((documentoService.loadAsResource(documento)).exists());
    }

    @Test
    void updateDocumento() {
        Documento expectedDocumento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "text/plain");
        when(documentoDAO.save(expectedDocumento)).thenReturn(expectedDocumento);
        Documento actualDocumento = documentoService.updateDocumento(expectedDocumento);
        assertEquals(expectedDocumento,actualDocumento);
    }

    @Test
    void findDocumentoById() {
        Documento expectedDocumento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "text/plain");
        when(documentoDAO.findById(expectedDocumento.getId())).thenReturn(Optional.of(expectedDocumento));
        Documento actualDocumento = documentoService.findDocumentoById(expectedDocumento.getId());
        assertEquals(expectedDocumento,actualDocumento);
    }

    @Test
    void searchDocumenti() {
        Documento documento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "text/plain");
        Documento documento1 = new Documento("src/main/resources/documents/test1.txt", LocalDate.now(),
                "test1.txt", false, "text/plain");
        List<Documento> expectedDocumenti = new ArrayList<>();
        expectedDocumenti.add(documento);
        expectedDocumenti.add(documento1);

        Documento example = new Documento();
        example.setNome("test");
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        Iterable<Documento> iterable = new ArrayList<>();
        when(documentoDAO.findAll(Example.of(example,matcher))).thenReturn(expectedDocumenti);
        List<Documento> actualDocumenti = documentoService.searchDocumenti(example);
        assertEquals(expectedDocumenti, actualDocumenti);
    }
}