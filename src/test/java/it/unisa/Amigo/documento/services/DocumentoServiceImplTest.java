package it.unisa.Amigo.documento.services;

import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
class DocumentoServiceImplTest {

    @InjectMocks
    private DocumentoServiceImpl documentoService;

    @Mock
    private DocumentoDAO documentoDAO;
    private GruppoService gruppoService; //dubbio

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

