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
   void storeDocumento(){

   }

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

    @Test
    void searchDocumentoFromRepository(){

    }
}
