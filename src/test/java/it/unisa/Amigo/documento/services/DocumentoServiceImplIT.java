package it.unisa.Amigo.documento.services;

import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DocumentoServiceImplIT {

    @Autowired
    private DocumentoServiceImpl documentoService;

    @Autowired
    private DocumentoDAO documentoDAO;

    @AfterEach
    void afterEach() {
        documentoDAO.deleteAll();
    }

    @Test
    void addDocumentoStoreAndLoad() {
        documentoService.addDocumento(new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()));
        Documento documento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(), "test.txt", false, "text/plain");
        assertThat((documentoService.loadAsResource(documento)).exists());
    }

    @Test
    void updateDocumento() {
        Documento documento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "text/plain");
        Documento expectedDocumento = documentoDAO.save(documento);
        expectedDocumento.setNome("testUpdate.txt");
        Documento actualDocumento = documentoService.updateDocumento(expectedDocumento);
        assertEquals(expectedDocumento, actualDocumento);
    }

    @Test
    void findDocumentoById() {
        Documento expectedDocumento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "text/plain");
        documentoDAO.save(expectedDocumento);
        Documento actualDocumento = documentoService.findDocumentoById(expectedDocumento.getId());
        assertEquals(expectedDocumento, actualDocumento);
    }
/*
    //TODO
    @Test
    void searchDocumenti(){
        Documento documento1 = new Documento("src/main/resources/documents/test.txt", LocalDate.now(), "test.txt", true, "text/plain");
        Documento documento2 = new Documento("src/main/resources/documents/test1.txt", LocalDate.now(), "test1.txt", true, "text/plain");
        documentoDAO.save(documento1);
        documentoDAO.save(documento2);
        List<Documento> expectedDocumenti = new ArrayList<>();
        expectedDocumenti.add(documento1);
        expectedDocumenti.add(documento2);
        Documento example = new Documento();
        example.setNome("test.txt");
        List<Documento> actualDocumenti = documentoService.searchDocumenti(example);
        assertEquals(expectedDocumenti,actualDocumenti);

    }

 */
}