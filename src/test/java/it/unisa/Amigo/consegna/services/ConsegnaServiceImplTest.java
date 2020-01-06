package it.unisa.Amigo.consegna.services;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.consegna.dao.ConsegnaDAO;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoService;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.Document;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest

class ConsegnaServiceImplTest {

    @Mock
    private  DocumentoServiceImpl documentoService;

    @Mock
    private  GruppoServiceImpl gruppoService;

    @Mock
    private ConsegnaDAO consegnaDAO;

    @InjectMocks
    private  ConsegnaServiceImpl consegnaService;


    @ParameterizedTest
    @MethodSource("provideDocumento")
    void sendDocumento(int[] idDestinatari, String locazione, MultipartFile file) {

        Persona expectedPersona1 = new Persona("Admin", "123", "Administrator");
        Persona expectedPersona2 = new Persona("123", "null", "Administrator");

        Documento doc = new Documento();
        doc.setDataInvio(LocalDate.now());
        doc.setNome(file.getOriginalFilename());
        doc.setInRepository(false);
        doc.setFormat(file.getContentType());

        Consegna consegna1 = new Consegna();
        consegna1.setDataConsegna(LocalDate.now());
        consegna1.setStato("da valutare");
        consegna1.setDocumento(doc);

        when(documentoService.addDocumento(file)).thenReturn(doc);
        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona1);
        when(gruppoService.findPersona(expectedPersona1.getId())).thenReturn(expectedPersona1);
        when(gruppoService.findPersona(expectedPersona2.getId())).thenReturn(expectedPersona2);


        List<Consegna> expectedConsegne = new ArrayList<>();

        if (idDestinatari != null) {
            for (int id : idDestinatari) {
                Consegna consegna = new Consegna();
                consegna.setDataConsegna(LocalDate.now());
                consegna.setStato("da valutare");
                consegna.setDocumento(doc);
                consegna.setMittente(gruppoService.getAuthenticatedUser());
                consegna.setLocazione(Consegna.USER_LOCAZIONE);
                consegna.setDestinatario(gruppoService.findPersona(id));
                expectedConsegne.add(consegna);
            }
            assertEquals(expectedConsegne.size(), consegnaService.sendDocumento(idDestinatari, locazione, file).size());
        }
        else {
            Consegna consegna = new Consegna();
            consegna.setDataConsegna(LocalDate.now());
            consegna.setStato("da valutare");
            consegna.setDocumento(doc);
            consegna.setMittente(gruppoService.getAuthenticatedUser());
            if (locazione.equalsIgnoreCase(Consegna.PQA_LOCAZIONE))
                consegna.setLocazione(Consegna.PQA_LOCAZIONE);
            if (locazione.equalsIgnoreCase(Consegna.NDV_LOCAZIONE))
                consegna.setLocazione(Consegna.NDV_LOCAZIONE);
            expectedConsegne.add(consegna);
            assertEquals(expectedConsegne.size(), consegnaService.sendDocumento(idDestinatari, locazione, file).size());
        }
    }

    private static Stream<Arguments> provideDocumento() {

        Persona expectedPersona1 = new Persona("Admin", "123", "Administrator");
        Persona expectedPersona2 = new Persona("123", "112", "Administrator");
        Persona expectedPersona3 = new Persona("123", "Boh", "Administrator");

        String NDV_LOCAZIONE = "NDV";
        String USER_LOCAZIONE = "USER";

        int[] ids = {expectedPersona2.getId(), expectedPersona3.getId()};
        int[] ids2 = {expectedPersona3.getId()};

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test contract.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

        return Stream.of(
                Arguments.of(ids, NDV_LOCAZIONE, file),
                Arguments.of(ids2, USER_LOCAZIONE, file)
        );
    }



    @ParameterizedTest
    @MethodSource("provideDownloadDocumento")
    void downloadDocumento(Documento expectedDocumento, Resource expectedResource)  {

        when(documentoService.loadAsResource(expectedDocumento)).thenReturn(expectedResource);
        when(documentoService.findDocumento(expectedDocumento.getId())).thenReturn(expectedDocumento);
        Resource actualResource = consegnaService.downloadDocumento(expectedDocumento.getId()).getBody();
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

    @Test
    void consegneInviate() {
        Consegna consegna = new Consegna();
        Consegna consegna1 = new Consegna();
        User user = new User("admin", "admin");
        Set<Role> ruoli = new HashSet<Role>();
        ruoli.add(new Role(Role.NDV_ROLE));
        user.setRoles(ruoli);
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona persona = new Persona("persona", "persona", "PQA");
        consegna.setMittente(persona);
        consegna1.setMittente(persona);
        List<Consegna> expectedConsegne = new ArrayList<>();
        expectedConsegne.add(consegna);
        expectedConsegne.add(consegna1);

        when(consegnaDAO.findAllByMittente(gruppoService.getAuthenticatedUser())).thenReturn(expectedConsegne);

        assertEquals(expectedConsegne, consegnaDAO.findAllByMittente(gruppoService.getAuthenticatedUser()));
    }


    @Test
    void consegneRicevute() {
        Consegna consegna = new Consegna();
        Consegna consegna1 = new Consegna();
        User user = new User("admin", "admin");
        Set<Role> ruoli = new HashSet<Role>();
        ruoli.add(new Role(Role.PQA_ROLE));
        user.setRoles(ruoli);
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona persona = new Persona("persona", "persona", "PQA");
        persona.setUser(user);
        consegna.setDestinatario(persona);
        consegna1.setDestinatario(persona);
        List<Consegna> expectedConsegne = new ArrayList<>();
        expectedConsegne.add(consegna);
        expectedConsegne.add(consegna1);

        when(gruppoService.getAuthenticatedUser()).thenReturn(persona);
        when(consegnaDAO.findAllByMittente(gruppoService.getAuthenticatedUser())).thenReturn(expectedConsegne);
        when(consegnaDAO.findAllByLocazione(Consegna.PQA_LOCAZIONE)).thenReturn(expectedConsegne);
        when(consegnaDAO.findAllByLocazione(Consegna.NDV_LOCAZIONE)).thenReturn(expectedConsegne);

        assertEquals(expectedConsegne, consegnaService.consegneRicevute());
    }

    @ParameterizedTest
    @MethodSource("provideDocumenti")
    void findConsegnaByDocumento(Documento documento) {
        Documento doc = documento;
        doc.setDataInvio(LocalDate.now());


        Consegna expectedConsegna = new Consegna();
        expectedConsegna.setDataConsegna(LocalDate.now());
        expectedConsegna.setStato("da valutare");
        expectedConsegna.setDocumento(doc);

        when(consegnaDAO.findByDocumento_Id(doc.getId())). thenReturn(expectedConsegna);
        assertEquals(expectedConsegna, consegnaService.findConsegnaByDocumento(doc.getId()));
    }

    private static Stream<Arguments> provideDocumenti() {
        Documento documento1 = new Documento();
        documento1.setNome("Documento1");
        Documento documento2 = new Documento();
        documento1.setNome("Documento2");
        Documento documento3 = new Documento();
        documento1.setNome("Documento3");

        return Stream.of(
                Arguments.of(documento1),
                Arguments.of(documento2),
                Arguments.of(documento3)
        );
    }

    @ParameterizedTest
    @MethodSource("providePossibiliDestinatari")
    void possibiliDestinatari(Role role) {

        User user = new User("admin", "admin");
        Set<Role> ruoli = new HashSet<Role>();
        ruoli.add(role);
        user.setRoles(ruoli);
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona persona = new Persona("persona", "persona", "PQA");
        persona.setUser(user);

        Set<String> expectedRuoli = new HashSet<>();

        if (role.getName().equalsIgnoreCase(Role.PQA_ROLE)) {
            expectedRuoli.add(Role.CAPOGRUPPO_ROLE);
            expectedRuoli.add(Role.NDV_ROLE);
        }
        if (role.getName().equalsIgnoreCase(Role.CPDS_ROLE)) {
            expectedRuoli.add(Role.NDV_ROLE);
            expectedRuoli.add(Role.PQA_ROLE);
        }
        if (role.getName().equalsIgnoreCase(Role.CAPOGRUPPO_ROLE)) {
            expectedRuoli.add(Role.PQA_ROLE);
        }


        when(gruppoService.getAuthenticatedUser()).thenReturn(persona);

        assertEquals(expectedRuoli, consegnaService.possibiliDestinatari());

    }

    private static Stream<Arguments> providePossibiliDestinatari() {
        Role role = new Role(Role.NDV_ROLE);
        Role role1 = new Role(Role.CAPOGRUPPO_ROLE);
        Role role2 = new Role(Role.CPDS_ROLE);
        return Stream.of(
                Arguments.of(role),
                Arguments.of(role1),
                Arguments.of(role2)
        );
    }

    @Test
    void approvaConsegna() {
        Consegna consegna = new Consegna();
        when(consegnaDAO.findById(consegna.getId())).thenReturn(java.util.Optional.of(consegna));
        consegnaService.approvaConsegna(consegna.getId());
        assertEquals(consegna.getStato(), "APPROVATA");
    }

    @Test
    void rifiutaConsegna() {
        Consegna consegna = new Consegna();
        when(consegnaDAO.findById(consegna.getId())).thenReturn(java.util.Optional.of(consegna));
        consegnaService.rifiutaConsegna(consegna.getId());
        assertEquals(consegna.getStato(), "RIFIUTATA");
    }
}