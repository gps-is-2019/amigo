package it.unisa.Amigo.documento.services;

import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class DocumentoServiceImplIT {

    @Autowired
    private DocumentoServiceImpl documentoService;

    @Autowired
    private DocumentoDAO documentoDAO;

   //non so il grupposervice se metterlo o meno


    @Test
    void addDocToTask(){

    }

    @Test
    void addDocToConsegna(){

    }

    @Test
    void addDocToRepository(){

    }

    @Test
    void loadAsResource(){

    }

    @Test
    void downloadDocumentoFromRepository(){

    }

    @Test
    void downloadDocumentoFromTask(){

    }
/*
    @Test
    void searchDocumentoFromRepository(){
        Documento documento1 = new Documento("src/main/resources/documents/test.txt", LocalDate.now(), "test.txt", true, "text/plain");
        Documento documento2 = new Documento("src/main/resources/documents/test1.txt", LocalDate.now(), "test1.txt", true, "text/plain");
        documentoDAO.save(documento1);
        documentoDAO.save(documento2);
        List<Documento> documentiExpected = new ArrayList<>();
        documentiExpected.add(documento1);
        documentiExpected.add(documento2);
        List<Documento> actualDocumenti = documentoService.searchDocumentoFromRepository("test");
        assertEquals(documentiExpected,actualDocumenti);
    }*/
}
