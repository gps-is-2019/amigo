package it.unisa.Amigo.consegna.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domain.Role;
import it.unisa.Amigo.autenticazione.domain.User;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.consegna.services.ConsegnaService;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.services.DocumentoService;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConsegnaControllerTest {

    @MockBean
    private ConsegnaService consegnaService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GruppoService gruppoService;

    @MockBean
    private DocumentoService documentoService;


    @ParameterizedTest
    @MethodSource("provideDestinatari")
    void viewConsegna(final Set<String> possibiliDestinatari, final List<Persona> destinatari, final String ruoloDest, final boolean flagRuolo) throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);

        when(consegnaService.possibiliDestinatari()).thenReturn(possibiliDestinatari);
        when(consegnaService.getDestinatariByRoleString(ruoloDest)).thenReturn(destinatari);

        this.mockMvc.perform(get("/consegna/{ruolo}", ruoloDest)
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("possibiliDestinatari", possibiliDestinatari))
                .andExpect(model().attribute("flagRuolo", flagRuolo))
                .andExpect(view().name("consegna/destinatari"));
    }

    private static Stream<Arguments> provideDestinatari() {
        //test 1
        Role pqaRole = new Role(Role.PQA_ROLE);

        Set<String> possibiliDest1 = new HashSet<String>();
        possibiliDest1.add(Role.PQA_ROLE);

        List<Persona> destinatari1 = new ArrayList<>();
        destinatari1.add(new Persona("", pqaRole.getName(), ""));

        //test 2
        Role capogruppoRole = new Role(Role.CAPOGRUPPO_ROLE);

        Set<String> possibiliDest2 = new HashSet<String>();
        possibiliDest2.add(Role.CAPOGRUPPO_ROLE);
        possibiliDest2.add(Role.NDV_ROLE);

        List<Persona> destinatari2 = new ArrayList<>();
        destinatari2.add(new Persona("Nome", "Cognome", Role.CAPOGRUPPO_ROLE));
        destinatari2.add(new Persona("Nome", "Cognome", Role.CAPOGRUPPO_ROLE));

        return Stream.of(
                Arguments.of(possibiliDest1, destinatari1, pqaRole.getName(), true),
                Arguments.of(possibiliDest2, destinatari2, capogruppoRole.getName(), false),
                Arguments.of(possibiliDest2, destinatari2, null, false),
                Arguments.of(possibiliDest1, destinatari1, "", true)
        );
    }

    @Test
    void sendDocumento() {
    }

    @ParameterizedTest
    @MethodSource("provideConsegneInviate")
    void findConsegneInviate(final List<Consegna> consegne) throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        when(consegnaService.consegneInviate()).thenReturn(consegne);
        this.mockMvc.perform(get("/consegna/inviati")
                .param("name", "")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("consegne", consegne))
                .andExpect(view().name("consegna/documenti-inviati"));
    }

    private static Stream<Arguments> provideConsegneInviate() {
        Persona persona1 = new Persona("persona1", "persona1", "");

        Documento documento1 = new Documento();
        documento1.setNome("Ciao");
        Documento documento2 = new Documento();
        documento2.setNome("Ciao");
        Documento documento3 = new Documento();
        documento3.setNome("Ciao");
        Documento documento4 = new Documento();
        documento4.setNome("Ciao");

        Consegna consegna = new Consegna();
        consegna.setId(1);
        consegna.setMittente(persona1);
        consegna.setDocumento(documento1);
        Consegna consegna1 = new Consegna();
        consegna1.setId(2);
        consegna1.setDocumento(documento2);
        consegna1.setMittente(persona1);
        Consegna consegna2 = new Consegna();
        consegna2.setId(3);
        consegna2.setDocumento(documento3);
        consegna2.setMittente(persona1);
        Consegna consegna3 = new Consegna();
        consegna3.setId(4);
        consegna3.setDocumento(documento4);
        consegna3.setMittente(persona1);

        List<Consegna> test1 = new ArrayList<>();
        List<Consegna> test2 = new ArrayList<>();
        test1.add(consegna);
        test1.add(consegna2);
        test2.add(consegna3);
        test2.add(consegna1);

        return Stream.of(
                Arguments.of(test1),
                Arguments.of(test2)
        );

    }

    @ParameterizedTest
    @MethodSource("provideConsegneRicevute")
    void findConsegneRicevute(final List<Consegna> consegne) throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);


        when(consegnaService.consegneRicevute()).thenReturn(consegne);

        this.mockMvc.perform(get("/consegna/ricevuti")
                .param("name", "")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("consegne", consegne))
                .andExpect(view().name("consegna/documenti-ricevuti"));
    }

    private static Stream<Arguments> provideConsegneRicevute() {
        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona persona1 = new Persona("persona1", "persona1", "");
        persona1.setUser(user);


        Documento documento1 = new Documento();
        documento1.setNome("Ciao");
        Documento documento2 = new Documento();
        documento2.setNome("Ciao");
        Documento documento3 = new Documento();
        documento3.setNome("Ciao");
        Documento documento4 = new Documento();
        documento4.setNome("Ciao");

        Consegna consegna = new Consegna();
        consegna.setId(1);
        consegna.setMittente(persona1);
        consegna.setDocumento(documento1);
        Consegna consegna1 = new Consegna();
        consegna1.setId(2);
        consegna1.setDocumento(documento2);
        consegna1.setMittente(persona1);
        Consegna consegna2 = new Consegna();
        consegna2.setId(3);
        consegna2.setDocumento(documento3);
        consegna2.setMittente(persona1);
        Consegna consegna3 = new Consegna();
        consegna3.setId(4);
        consegna3.setDocumento(documento4);
        consegna3.setMittente(persona1);

        List<Consegna> test1 = new ArrayList<>();
        List<Consegna> test2 = new ArrayList<>();
        test1.add(consegna);
        test1.add(consegna2);
        test2.add(consegna3);
        test2.add(consegna1);

        return Stream.of(
                Arguments.of(test1),
                Arguments.of(test2)
        );

    }

    /*
    @ParameterizedTest
    @MethodSource("provideDownloadDocumento")
    void downloadDocumento(Consegna consegna, Persona personaLoggata) throws Exception {

        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona persona1 = new Persona("persona1", "persona1", "");
        persona1.setUser(user);

        when(consegnaService.findConsegnaByDocumento(consegna.getDocumento().getId())).thenReturn(consegna);
        when(gruppoService.getAuthenticatedUser()).thenReturn(personaLoggata);

        ResultActions response = this.mockMvc.perform(get("/consegna/miei-documenti/{idDocumento}", consegna.getDocumento().getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("consegna/documenti-ricevuti"));

        response.andReturn().


    }

    private static Stream<Arguments> provideDownloadDocumento(){

        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona persona1 = new Persona("persona1", "persona1", "");
        persona1.setUser(user);


        Documento documento1 = new Documento();
        documento1.setNome("Ciao");
        Documento documento2 = new Documento();
        documento2.setNome("Ciao");
        Documento documento3 = new Documento();
        documento3.setNome("Ciao");
        Documento documento4 = new Documento();
        documento4.setNome("Ciao");

        Consegna consegna = new Consegna();
        consegna.setId(1);
        consegna.setMittente(persona1);
        consegna.setDestinatario(persona1);
        consegna.setDocumento(documento1);
        Consegna consegna1 = new Consegna();
        consegna1.setId(2);
        consegna1.setDocumento(documento2);
        consegna1.setMittente(persona1);
        consegna1.setDestinatario(persona1);
        Consegna consegna2 = new Consegna();
        consegna2.setId(3);
        consegna2.setDocumento(documento3);
        consegna2.setDestinatario(persona1);
        consegna2.setMittente(persona1);
        Consegna consegna3 = new Consegna();
        consegna3.setId(4);
        consegna3.setDocumento(documento4);
        consegna3.setDestinatario(persona1);
        consegna3.setMittente(persona1);

        List<Consegna> test1 = new ArrayList<>();
        List<Consegna> test2 = new ArrayList<>();
        test1.add(consegna);
        test1.add(consegna2);
        test2.add(consegna3);
        test2.add(consegna1);

        return Stream.of(
                Arguments.of(consegna, persona1),
                Arguments.of(consegna2, persona1)
        );
    }
    */

    @ParameterizedTest
    @MethodSource("provideApprovaConsegna")
    void approvaConsegna(final List<Consegna> consegne) throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);


        when(consegnaService.consegneRicevute()).thenReturn(consegne);

        this.mockMvc.perform(get("/consegna/approva/{id}", consegne.get(0).getId())
                .param("name", "")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("consegne", consegne))
                .andExpect(view().name("consegna/documenti-ricevuti"));
    }

    private static Stream<Arguments> provideApprovaConsegna() {

        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona persona1 = new Persona("persona1", "persona1", "");
        persona1.setUser(user);


        Documento documento1 = new Documento();
        documento1.setNome("Ciao");
        Documento documento2 = new Documento();
        documento2.setNome("Ciao");
        Documento documento3 = new Documento();
        documento3.setNome("Ciao");
        Documento documento4 = new Documento();
        documento4.setNome("Ciao");

        Consegna consegna = new Consegna();
        consegna.setId(1);
        consegna.setMittente(persona1);
        consegna.setDestinatario(persona1);
        consegna.setDocumento(documento1);
        Consegna consegna1 = new Consegna();
        consegna1.setId(2);
        consegna1.setDocumento(documento2);
        consegna1.setMittente(persona1);
        consegna1.setDestinatario(persona1);
        Consegna consegna2 = new Consegna();
        consegna2.setId(3);
        consegna2.setDocumento(documento3);
        consegna2.setDestinatario(persona1);
        consegna2.setMittente(persona1);
        Consegna consegna3 = new Consegna();
        consegna3.setId(4);
        consegna3.setDocumento(documento4);
        consegna3.setDestinatario(persona1);
        consegna3.setMittente(persona1);

        List<Consegna> test1 = new ArrayList<>();
        List<Consegna> test2 = new ArrayList<>();
        test1.add(consegna);
        test1.add(consegna2);
        test2.add(consegna3);
        test2.add(consegna1);

        return Stream.of(
                Arguments.of(test1),
                Arguments.of(test2)
        );

    }

    @ParameterizedTest
    @MethodSource("provideRifiutaConsegna")
    void rifiutaConsegna(final List<Consegna> consegne) throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);


        when(consegnaService.consegneRicevute()).thenReturn(consegne);

        this.mockMvc.perform(get("/consegna/rifiuta/{id}", consegne.get(0).getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("consegne", consegne))
                .andExpect(view().name("consegna/documenti-ricevuti"));
    }

    private static Stream<Arguments> provideRifiutaConsegna() {

        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona persona1 = new Persona("persona1", "persona1", "");
        persona1.setUser(user);


        Documento documento1 = new Documento();
        documento1.setNome("Ciao");
        Documento documento2 = new Documento();
        documento2.setNome("Ciao");
        Documento documento3 = new Documento();
        documento3.setNome("Ciao");
        Documento documento4 = new Documento();
        documento4.setNome("Ciao");

        Consegna consegna = new Consegna();
        consegna.setId(1);
        consegna.setMittente(persona1);
        consegna.setDestinatario(persona1);
        consegna.setDocumento(documento1);
        Consegna consegna1 = new Consegna();
        consegna1.setId(2);
        consegna1.setDocumento(documento2);
        consegna1.setMittente(persona1);
        consegna1.setDestinatario(persona1);
        Consegna consegna2 = new Consegna();
        consegna2.setId(3);
        consegna2.setDocumento(documento3);
        consegna2.setDestinatario(persona1);
        consegna2.setMittente(persona1);
        Consegna consegna3 = new Consegna();
        consegna3.setId(4);
        consegna3.setDocumento(documento4);
        consegna3.setDestinatario(persona1);
        consegna3.setMittente(persona1);

        List<Consegna> test1 = new ArrayList<>();
        List<Consegna> test2 = new ArrayList<>();
        test1.add(consegna);
        test1.add(consegna2);
        test2.add(consegna3);
        test2.add(consegna1);

        return Stream.of(
                Arguments.of(test1),
                Arguments.of(test2)
        );

    }

    @ParameterizedTest
    @MethodSource("provideDownloadDocumentoFalse")
    void downloadDocumentoNull(User user, Documento documento, Consegna consegna, boolean consentito) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "https://i.makeagif.com/media/6-18-2016/i4va3h.gif");
        ResponseEntity<Resource> expectedResponse = new ResponseEntity<>(headers, HttpStatus.FOUND);
        consegna.setDocumento(documento);
        when(consegnaService.findConsegnaByDocumento(documento.getId())).thenReturn(consegna);
        when(consegnaService.currentPersonaCanOpen(consegna)).thenReturn(consentito);

        this.mockMvc.perform(get("/consegna/miei-documenti/{idDocumento}",documento.getId())
                .with(csrf())
                .with(user(userDetails)))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("https://i.makeagif.com/media/6-18-2016/i4va3h.gif"));

    }

    private static Stream<Arguments> provideDownloadDocumentoFalse(){
        User user1 = new User("ferrucci@unista.it", "ferrucci");
        Documento documento = new Documento();
        documento.setPath("/src/test/resources/documents/file.txt");
        documento.setId(100);
        documento.setNome("test.txt");
        documento.setFormat("text/plain");

        Documento documento2 = new Documento();
        documento2.setPath("//src//test//resources//documents/file.txt");
        documento2.setId(101);
        documento2.setNome("test.txt");
        documento2.setFormat("text/plain");

        Consegna consegna = new Consegna();

        return Stream.of(
                Arguments.of(user1, documento, consegna, false)
                );
    }

    @ParameterizedTest
    @MethodSource("provideDownloadDocumentoTrue")
    void downloadDocumento(User user, Documento documento, Consegna consegna, boolean consentito) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "https://i.makeagif.com/media/6-18-2016/i4va3h.gif");
        ResponseEntity<Resource> expectedResponse = new ResponseEntity<>(headers, HttpStatus.FOUND);
        consegna.setDocumento(documento);
        when(consegnaService.findConsegnaByDocumento(documento.getId())).thenReturn(consegna);
        when(consegnaService.currentPersonaCanOpen(consegna)).thenReturn(consentito);

        this.mockMvc.perform(get("/consegna/miei-documenti/{idDocumento}",documento.getId())
                .with(csrf())
                .with(user(userDetails)))
                .andExpect(status().is(200))
                .andExpect(header().exists("Content-Disposition"));
    }

    private static Stream<Arguments> provideDownloadDocumentoTrue(){
        User user1 = new User("ferrucci@unista.it", "ferrucci");
        Documento documento = new Documento();
        documento.setPath("/src/test/resources/documents/file.txt");
        documento.setId(100);
        documento.setNome("test.txt");
        documento.setFormat("text/plain");

        Documento documento2 = new Documento();
        documento2.setPath("//src//test//resources//documents/file.txt");
        documento2.setId(101);
        documento2.setNome("test.txt");
        documento2.setFormat("text/plain");

        Consegna consegna = new Consegna();

        return Stream.of(
                Arguments.of(user1, documento, consegna, true),
                Arguments.of(user1, documento2, consegna, true)

        );
    }

    @ParameterizedTest
    @MethodSource("provideSendDocumentoUnauthorized")
    public void sendDocumentoUnauthorized(User user, MockMultipartFile multipartFile) throws Exception {

        UserDetailImpl userDetail = new UserDetailImpl(user);
        this.mockMvc.perform(multipart("/consegna").file(multipartFile).param("destinatariPost", "h2")
                .with(csrf())
                .with(user(userDetail)))
                .andExpect(status().isOk())
                .andExpect(view().name("/unauthorized"));
    }

    private static Stream<Arguments> provideSendDocumentoUnauthorized(){
        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        MockMultipartFile file = new MockMultipartFile("file", "test.side", "application/pdf", new byte[1]);
        return Stream.of(
                Arguments.of(user, file)
        );

    }

    @ParameterizedTest
    @MethodSource("provideSendDocumento")
    public void sendDocumento(User user, MockMultipartFile multipartFile) throws Exception {

        UserDetailImpl userDetail = new UserDetailImpl(user);
        this.mockMvc.perform(multipart("/consegna").file(multipartFile).param("destinatariPost", "h2")
                .with(csrf())
                .with(user(userDetail)))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/consegna/inviati?name="));
    }

    private static Stream<Arguments> provideSendDocumento() {
        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "application/pdf", new byte[1]);
        return Stream.of(
                Arguments.of(user, file)
        );


    }
}
