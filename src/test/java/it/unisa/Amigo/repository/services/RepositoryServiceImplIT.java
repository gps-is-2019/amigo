package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


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

    @Test
    void addDocumentoInRepository() {

        MultipartFile file = new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes());
        Boolean expectedValue = repositoryService.addDocumentoInRepository(file);
        assertEquals(true, expectedValue);

    }

    @Test
    void downloadDocumento() {
        Documento expectedDocumento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "text/plain");
        documentoDAO.save(expectedDocumento);
        Documento actualDocumento = documentoService.findDocumento(expectedDocumento.getId());
        assertEquals(expectedDocumento, actualDocumento);
    }

    @Test
    void serarchDcoumentInRepository() {

        Documento documentoExample = new Documento();
        documentoExample.setInRepository(true);
        documentoExample.setNome("test");
        List<Documento> expectedDocumenti = documentoService.searchDocumenti(documentoExample);
        List<Documento> actualDocumenti = repositoryService.serarchDcoumentInRepository("test");
        assertEquals(actualDocumenti,expectedDocumenti);
    }
}
