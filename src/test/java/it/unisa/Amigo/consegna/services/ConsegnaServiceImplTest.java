package it.unisa.Amigo.consegna.services;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domain.Role;
import it.unisa.Amigo.autenticazione.domain.User;
import it.unisa.Amigo.autenticazione.services.AuthService;
import it.unisa.Amigo.consegna.dao.ConsegnaDAO;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import it.unisa.Amigo.gruppo.domain.Commissione;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static it.unisa.Amigo.consegna.domain.Consegna.PQA_LOCAZIONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class ConsegnaServiceImplTest {

    @Mock
    private DocumentoServiceImpl documentoService;

    @Mock
    private GruppoServiceImpl gruppoService;

    @Mock
    private ConsegnaDAO consegnaDAO;

    @InjectMocks
    private ConsegnaServiceImpl consegnaService;

    @Mock
    private AuthService authService;

    private static Stream<Arguments> provideDocumento() throws IOException {

        Persona expectedPersona1 = new Persona("Admin", "123", "Administrator");
        expectedPersona1.setId(1);
        Persona expectedPersona2 = new Persona("123", "112", "Administrator");
        expectedPersona2.setId(2);
        Persona expectedPersona3 = new Persona("123", "Boh", "Administrator");
        expectedPersona3.setId(3);

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
                Arguments.of(ids, NDV_LOCAZIONE, file.getOriginalFilename(), file.getBytes(), file.getContentType()),
                Arguments.of(null, PQA_LOCAZIONE, file.getOriginalFilename(), file.getBytes(), file.getContentType()),
                Arguments.of(null, NDV_LOCAZIONE, file.getOriginalFilename(), file.getBytes(), file.getContentType())
        );
    }

    private static Stream<Arguments> provideDocumenti() {
        Documento documento1 = new Documento();
        documento1.setNome("Documento1");
        documento1.setId(1);
        Documento documento2 = new Documento();
        documento2.setNome("Documento2");
        documento2.setId(2);
        Documento documento3 = new Documento();
        documento3.setNome("Documento3");
        documento3.setId(3);

        return Stream.of(
                Arguments.of(documento1),
                Arguments.of(documento2),
                Arguments.of(documento3)
        );
    }

    private static Stream<Arguments> providePossibiliDestinatari() {
        Role role = new Role(Role.NDV_ROLE);
        Role role1 = new Role(Role.CAPOGRUPPO_ROLE);
        Role role2 = new Role(Role.CPDS_ROLE);
        Role role3 = new Role(Role.PQA_ROLE);
        return Stream.of(
                Arguments.of(role),
                Arguments.of(role1),
                Arguments.of(role2),
                Arguments.of(role3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDocumento")
    void sendDocumento(final int[] idDestinatari, final String locazione, final String fileName, final byte[] bytes, final String mimeType) {
        Persona expectedPersona1 = new Persona("Admin", "123", "Administrator");
        Persona expectedPersona2 = new Persona("123", "null", "Administrator");

        Documento doc = new Documento();
        doc.setDataInvio(LocalDate.now());
        doc.setNome(fileName);
        doc.setInRepository(false);
        doc.setFormat(mimeType);

        Consegna consegna1 = new Consegna();
        consegna1.setDataConsegna(LocalDate.now());
        consegna1.setStato("da valutare");
        consegna1.setDocumento(doc);

        when(documentoService.addDocumento(fileName, bytes, mimeType)).thenReturn(doc);
        when(gruppoService.getCurrentPersona()).thenReturn(expectedPersona1);
        when(gruppoService.findPersona(expectedPersona1.getId())).thenReturn(expectedPersona1);
        when(gruppoService.findPersona(expectedPersona2.getId())).thenReturn(expectedPersona2);


        List<Consegna> expectedConsegne = new ArrayList<>();

        if (idDestinatari != null) {
            for (int id : idDestinatari) {
                Consegna consegna = new Consegna();
                consegna.setDataConsegna(LocalDate.now());
                consegna.setStato("da valutare");
                consegna.setDocumento(doc);
                consegna.setMittente(gruppoService.getCurrentPersona());
                consegna.setLocazione(Consegna.USER_LOCAZIONE);
                consegna.setDestinatario(gruppoService.findPersona(id));
                expectedConsegne.add(consegna);
            }
            assertEquals(expectedConsegne.size(), consegnaService.sendDocumento(idDestinatari, locazione, fileName, bytes, mimeType).size());
        } else {
            Consegna consegna = new Consegna();
            consegna.setDataConsegna(LocalDate.now());
            consegna.setStato("da valutare");
            consegna.setDocumento(doc);
            consegna.setMittente(gruppoService.getCurrentPersona());
            if (locazione.equalsIgnoreCase(PQA_LOCAZIONE)) {
                consegna.setLocazione(PQA_LOCAZIONE);
            }
            if (locazione.equalsIgnoreCase(Consegna.NDV_LOCAZIONE)) {
                consegna.setLocazione(Consegna.NDV_LOCAZIONE);
            }
            expectedConsegne.add(consegna);
            assertEquals(expectedConsegne.size(), consegnaService.sendDocumento(idDestinatari, locazione, fileName, bytes, mimeType).size());
        }
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

        when(consegnaDAO.findAllByMittente(gruppoService.getCurrentPersona())).thenReturn(expectedConsegne);

        assertEquals(expectedConsegne, consegnaDAO.findAllByMittente(gruppoService.getCurrentPersona()));
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

        when(gruppoService.getCurrentPersona()).thenReturn(persona);
        when(consegnaDAO.findAllByMittente(gruppoService.getCurrentPersona())).thenReturn(expectedConsegne);
        when(consegnaDAO.findAllByLocazione(PQA_LOCAZIONE)).thenReturn(expectedConsegne);
        when(consegnaDAO.findAllByLocazione(Consegna.NDV_LOCAZIONE)).thenReturn(expectedConsegne);

        assertEquals(expectedConsegne, consegnaService.consegneRicevute());
    }

    @ParameterizedTest
    @MethodSource("provideDocumenti")
    void findConsegnaByDocumento(final Documento documento) {
        Documento doc = documento;
        doc.setDataInvio(LocalDate.now());


        Consegna expectedConsegna = new Consegna();
        expectedConsegna.setDataConsegna(LocalDate.now());
        expectedConsegna.setStato("da valutare");
        expectedConsegna.setDocumento(doc);

        when(consegnaDAO.findByDocumento_Id(doc.getId())).thenReturn(expectedConsegna);
        assertEquals(expectedConsegna, consegnaService.findConsegnaByDocumento(doc.getId()));
    }

    @ParameterizedTest
    @MethodSource("providePossibiliDestinatari")
    void possibiliDestinatari(final Role role) {

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


        when(gruppoService.getCurrentPersona()).thenReturn(persona);

        assertEquals(expectedRuoli, consegnaService.possibiliDestinatari());

    }

    @Test
    void approvaConsegna() {
        Consegna consegna = new Consegna();
        consegna.setId(1);
        when(consegnaDAO.findById(consegna.getId())).thenReturn(java.util.Optional.of(consegna));
        consegnaService.approvaConsegna(consegna.getId());
        assertEquals(consegna.getStato(), "APPROVATA");
    }

    @Test
    void rifiutaConsegna() {
        Consegna consegna = new Consegna();
        consegna.setId(1);
        when(consegnaDAO.findById(consegna.getId())).thenReturn(java.util.Optional.of(consegna));
        consegnaService.rifiutaConsegna(consegna.getId());
        assertEquals(consegna.getStato(), "RIFIUTATA");
    }

    @ParameterizedTest
    @MethodSource("provideInoltraPQAfromGruppo")
    void inoltraPQAfromGruppo(final Documento doc, final Persona persona, final Consegna expectedConsegna) {

        when(gruppoService.getCurrentPersona()).thenReturn(persona);
        assertEquals(expectedConsegna, consegnaService.inoltraPQAfromGruppo(doc));
    }

    private static Stream<Arguments> provideInoltraPQAfromGruppo() {

        Persona expectedPersona1 = new Persona("Admin", "123", "Administrator");
        expectedPersona1.setId(1);
        Persona expectedPersona2 = new Persona("123", "112", "Administrator");
        expectedPersona2.setId(2);
        Persona expectedPersona3 = new Persona("123", "Boh", "Administrator");
        expectedPersona3.setId(3);

        Documento documento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "text/plain");
        Documento documento1 = new Documento("src/main/resources/documents/test1.txt", LocalDate.now(),
                "test1.txt", false, "text/plain");
        Documento documento2 = new Documento("src/main/resources/documents/test2.txt", LocalDate.now(),
                "test2.txt", false, "text/plain");

        Consegna consegna1 = new Consegna();
        consegna1.setDataConsegna(LocalDate.now());
        consegna1.setStato("DA_VALUTARE");
        consegna1.setDocumento(documento);
        consegna1.setMittente(expectedPersona1);
        consegna1.setLocazione(PQA_LOCAZIONE);

        Consegna consegna2 = new Consegna();
        consegna2.setDataConsegna(LocalDate.now());
        consegna2.setStato("DA_VALUTARE");
        consegna2.setDocumento(documento1);
        consegna2.setMittente(expectedPersona2);
        consegna2.setLocazione(PQA_LOCAZIONE);

        Consegna consegna3 = new Consegna();
        consegna3.setDataConsegna(LocalDate.now());
        consegna3.setStato("DA_VALUTARE");
        consegna3.setDocumento(documento2);
        consegna3.setMittente(expectedPersona3);
        consegna3.setLocazione(PQA_LOCAZIONE);

        return Stream.of(
                Arguments.of(documento, expectedPersona1, consegna1),
                Arguments.of(documento1, expectedPersona2, consegna2),
                Arguments.of(documento2, expectedPersona3, consegna3)
        );
    }

    @Test
    public void getResourceFromDocumentoWithId() throws MalformedURLException {
        Documento documento = new Documento();
        documento.setPath("file:src/test/resources/documents/test.txt");
        Resource resource = new UrlResource(documento.getPath());
        when(documentoService.loadAsResource(documentoService.findDocumentoById(documento.getId()))).thenReturn(resource);
        assertEquals(resource, consegnaService.getResourceFromDocumentoWithId(documento.getId()));
    }

    @Test
    public void getDestinatariByRoleString(){
        User user = new User("ferrucci@unisa.it", "ferrucci");
        user.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        Persona persona = new Persona("Filomena", "Ferrucci", "Professore Associato");
        persona.setUser(user);
        user.setPersona(persona);
        List<Persona> persone = new ArrayList<>();
        persone.add(persona);
        when(gruppoService.findAllByRuolo(Role.CAPOGRUPPO_ROLE)).thenReturn(persone);
        assertEquals(persone, consegnaService.getDestinatariByRoleString(Role.CAPOGRUPPO_ROLE));
    }

    @ParameterizedTest
    @MethodSource("provideCurrentPersona")
    public void currentPersonaCanOpen(Persona persona, Consegna consegna) {
        User user  = new User("user", "user");
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(Role.PQA_ROLE));
        user.setRoles(roles);
        user.setPersona(persona);
        persona.setUser(user);
        when( gruppoService.getCurrentPersona()).thenReturn(persona);
        when(authService.getCurrentUserRoles()).thenReturn(roles);
        assertTrue(consegnaService.currentPersonaCanOpen(consegna));

    }

    private static Stream<Arguments> provideCurrentPersona(){

        Persona persona = new Persona("Filomena", "Ferrucci", "Professore Ordinario");
        Persona persona2 = new Persona("Vittorio", "Scarano", "Professore Ordianrio");
        Persona persona3 = new Persona("Roberto", "De Prisco", "Professore Ordinario");
        Consegna consegna = new Consegna();
        consegna.setMittente(persona);
        consegna.setDestinatario(persona2);
        consegna.setLocazione(PQA_LOCAZIONE);

        Consegna consegna1 = new Consegna();
        consegna1.setMittente(persona2);
        consegna1.setDestinatario(persona);
        consegna1.setLocazione(PQA_LOCAZIONE);

        return Stream.of(
                Arguments.of(persona2,consegna),
                Arguments.of(persona, consegna1),
                Arguments.of(persona3, consegna)
        );
    }
}
