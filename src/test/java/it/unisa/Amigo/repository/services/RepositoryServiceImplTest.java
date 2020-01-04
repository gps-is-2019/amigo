package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoService;
import it.unisa.Amigo.gruppo.domain.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.Document;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class RepositoryServiceImplTest {

    @InjectMocks
    private RepositoryServiceImpl repositoryService;

    @Mock
    private DocumentoService documentoService;

    @Mock
    private ResponseEntity  responseEntity;


    @Test
    void addDocumentoInRepository() {

        MultipartFile file = new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes());
        Documento expectedDocumento = new Documento();
        when(documentoService.addDocumento(file)).thenReturn(expectedDocumento);
        expectedDocumento.setInRepository(true);
        when(documentoService.updateDocumento(expectedDocumento)).thenReturn(expectedDocumento);
        boolean expectedValue = repositoryService.addDocumentoInRepository(file);
        assertEquals(true, expectedValue);

    }

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

    @Test
    void serarchDcoumentInRepository() {
        Documento documentoExample = new Documento();
        documentoExample.setInRepository(true);
        documentoExample.setNome("test");
        List<Documento> expectedDocumenti = new ArrayList<>();
        when(documentoService.searchDocumenti(documentoExample)).thenReturn(expectedDocumenti);
        List<Documento> actualDocumenti = repositoryService.serarchDcoumentInRepository("test");
        assertEquals(actualDocumenti,expectedDocumenti);
    }


}