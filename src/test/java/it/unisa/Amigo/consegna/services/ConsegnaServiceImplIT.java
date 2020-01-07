package it.unisa.Amigo.consegna.services;

import aj.org.objectweb.asm.ConstantDynamic;
import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.consegna.dao.ConsegnaDAO;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ConsegnaServiceImplIT {

    @Autowired
    private DocumentoServiceImpl documentoService;

    @Autowired
    private GruppoServiceImpl gruppoService;

    @Autowired
    private ConsegnaDAO consegnaDAO;

    @Autowired
    private  ConsegnaServiceImpl consegnaService;

    @Autowired
    private PersonaDAO personaDAO;

    @Autowired
    private DocumentoDAO documentoDAO;

    @Autowired
    private UserDAO userDAO;


    @WithMockUser("ferrucci")
    @ParameterizedTest
    @MethodSource("provideDocumento")
    void sendDocumento(int[] idDestinatari, String locazione, MultipartFile file) {

        User user = new User("admin", "admin");
        Set<Role> ruoli = new HashSet<Role>();
        ruoli.add(new Role(Role.NDV_ROLE));
        user.setRoles(ruoli);
        UserDetailImpl userDetails = new UserDetailImpl(user);

        User user1 = new User("admin", "admin");
        user.setRoles(ruoli);
        UserDetailImpl userDetails1 = new UserDetailImpl(user1);

        Persona expectedPersona1 = new Persona("Admin", "123", "Administrator");
        expectedPersona1.setUser(user);
        Persona expectedPersona2 = new Persona("123", "null", "Administrator");
        expectedPersona2.setUser(user1);

        Documento doc = new Documento();
        doc.setDataInvio(LocalDate.now());
        doc.setNome(file.getOriginalFilename());
        doc.setInRepository(false);
        doc.setFormat(file.getContentType());

        Consegna consegna1 = new Consegna();
        consegna1.setDataConsegna(LocalDate.now());
        consegna1.setStato("da valutare");
        consegna1.setDocumento(doc);

        personaDAO.save(expectedPersona1);
        personaDAO.save(expectedPersona2);
        documentoDAO.save(doc);




       /* when(documentoService.addDocumento(file)).thenReturn(doc);
        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona1);
        when(gruppoService.findPersona(expectedPersona1.getId())).thenReturn(expectedPersona1);
        when(gruppoService.findPersona(expectedPersona2.getId())).thenReturn(expectedPersona2);*/


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

        documentoDAO.save(expectedDocumento);
        /*when(documentoService.loadAsResource(expectedDocumento)).thenReturn(expectedResource);
        when(documentoService.findDocumento(expectedDocumento.getId())).thenReturn(expectedDocumento);*/
        Resource actualResource = consegnaService.downloadDocumento(expectedDocumento.getId()).getBody();
        assertEquals(actualResource, expectedResource);
    }

    private static Stream<Arguments> provideDownloadDocumento() throws MalformedURLException {
        Documento doc1 = new Documento("src/main/resources/documents/test2.txt", LocalDate.now(),
                "test2.txt", false, "application/txt");
        Documento doc2 = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "application/txt");

        Resource res1 = new UrlResource(Paths.get(doc1.getPath()).toUri());
        Resource res2 = new UrlResource(Paths.get(doc2.getPath()).toUri());

        return Stream.of(
                Arguments.of(doc1, res1),
                Arguments.of(doc2, res2)
        );
    }

    @WithMockUser("ferrucci")
    @Test
    void consegneInviate() {
        Consegna consegna = new Consegna();
        Consegna consegna1 = new Consegna();
        consegna.setMittente(gruppoService.getAuthenticatedUser());
        consegna1.setMittente(gruppoService.getAuthenticatedUser());
        List<Consegna> expectedConsegne = new ArrayList<>();
        expectedConsegne.add(consegna);
        expectedConsegne.add(consegna1);
        consegnaDAO.save(consegna);
        consegnaDAO.save(consegna1);
        assertEquals(expectedConsegne, consegnaDAO.findAllByMittente(gruppoService.getAuthenticatedUser()));
    }

    @WithMockUser("ferrucci")
    @Test
    void consegneRicevute() {
        User user = new User("ferrucci", "admin");
        Set<Role> ruoli = new HashSet<Role>();
        ruoli.add(new Role(Role.CAPOGRUPPO_ROLE));
        user.setRoles(ruoli);
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona persona = new Persona("persona", "persona", "PQA");
        persona.setUser(user);
        personaDAO.save(persona);
        userDAO.save(user);
        Consegna consegna = new Consegna();
        Consegna consegna1 = new Consegna();
        consegna.setDestinatario(persona);
        consegna1.setDestinatario(persona);
        List<Consegna> expectedConsegne = new ArrayList<>();
        expectedConsegne.add(consegna);
        expectedConsegne.add(consegna1);
        consegnaDAO.save(consegna);
        consegnaDAO.save(consegna1);
        assertEquals(expectedConsegne, consegnaService.consegneRicevute());
    }

    @ParameterizedTest
    @MethodSource("provideDocumenti")
    void findConsegnaByDocumento(Documento documento) {
        Documento doc = documento;
        doc.setDataInvio(LocalDate.now());

        documentoDAO.save(doc);

        Consegna expectedConsegna = new Consegna();
        expectedConsegna.setDataConsegna(LocalDate.now());
        expectedConsegna.setStato("da valutare");
        expectedConsegna.setDocumento(doc);

        consegnaDAO.save(expectedConsegna);


       // when(consegnaDAO.findByDocumento_Id(doc.getId())). thenReturn(expectedConsegna);
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

    @WithMockUser("ferrucci")
    @ParameterizedTest
    @MethodSource("providePossibiliDestinatari")
    void possibiliDestinatari(Role role) {
        User user = new User("ferrucci", "admin");
        Set<Role> ruoli = new HashSet<Role>();
        ruoli.add(role);
        user.setRoles(ruoli);
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona persona = new Persona("persona", "persona", "PQA");
        persona.setUser(user);
        personaDAO.save(persona);
        userDAO.save(user);

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
        consegnaDAO.save(consegna);
        consegnaService.approvaConsegna(consegna.getId());
        assertEquals(consegnaDAO.findById(consegna.getId()).get().getStato(), "APPROVATA");
    }

    @Test
    void rifiutaConsegna() {
        Consegna consegna = new Consegna();
        consegna.setStato("APPROVATA");
        consegnaDAO.save(consegna);
        consegnaService.rifiutaConsegna(consegna.getId());
        assertEquals(consegnaDAO.findById(consegna.getId()).get().getStato(), "RIFIUTATA");
    }
}
