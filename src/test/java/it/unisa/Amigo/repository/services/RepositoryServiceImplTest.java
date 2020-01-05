package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

    @Mock
    private ResponseEntity  responseEntity;


    @ParameterizedTest
    @MethodSource("provideAddDocumentoInRepository")
    void addDocumentoInRepository( MultipartFile file) {


        Documento expectedDocumento = new Documento();
        when(documentoService.addDocumento(file)).thenReturn(expectedDocumento);
        expectedDocumento.setInRepository(true);
        when(documentoService.updateDocumento(expectedDocumento)).thenReturn(expectedDocumento);
        boolean expectedValue = repositoryService.addDocumentoInRepository(file);
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


    @ParameterizedTest
    @MethodSource("provideSerarchDcoumentInRepository")
    void serarchDcoumentInRepository( Documento documentoExample, String name) {
        List<Documento> expectedDocumenti = new ArrayList<>();
        when(documentoService.searchDocumenti(documentoExample)).thenReturn(expectedDocumenti);
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