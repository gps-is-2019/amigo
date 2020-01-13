package it.unisa.Amigo.consegna.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domain.Role;
import it.unisa.Amigo.autenticazione.domain.User;
import it.unisa.Amigo.consegna.dao.ConsegnaDAO;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConsegnaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonaDAO personaDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ConsegnaDAO consegnaDAO;

    @Autowired
    private DocumentoDAO documentoDAO;

    @AfterEach
    void afterEach() {
        consegnaDAO.deleteAll();
        documentoDAO.deleteAll();
        personaDAO.deleteAll();
        userDAO.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideDestinatari")
    void viewConsegna(final Set<String> possibiliDestinatari, final List<Persona> destinatari, final String ruoloDest, final boolean flagRuolo, final User userLoggato) throws Exception {

        for (int i = 0; i < destinatari.size(); i++) {
            if (!destinatari.get(i).getCognome().equals(Role.PQA_ROLE)) {
                personaDAO.save(destinatari.get(i));
            }
        }

        UserDetailImpl userDetails = new UserDetailImpl(userLoggato);
        personaDAO.save(userLoggato.getPersona());
        userDAO.save(userLoggato);

        this.mockMvc.perform(get("/consegna/{ruolo}", ruoloDest)
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("possibiliDestinatari", possibiliDestinatari))
                .andExpect(model().attribute("destinatari", destinatari))
                .andExpect(model().attribute("ruoloDest", ruoloDest))
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

        Persona persona1 = new Persona("Nome", "Cognome", "");
        User user1 = new User("admin", "admin");
        user1.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        user1.setPersona(persona1);
        persona1.setUser(user1);

        /*//test 2
        Role capogruppoRole = new Role(Role.CAPOGRUPPO_ROLE);

        Set<String> possibiliDest2 = new HashSet<String>();
        possibiliDest2.add(Role.CAPOGRUPPO_ROLE);
        possibiliDest2.add(Role.NDV_ROLE);

        List<Persona> destinatari2 = new ArrayList<>();
        Persona persona2 = new Persona("Nome", "Cognome", "");
        Persona persona3 = new Persona("Nome", "Cognome", "");
        User user2 = new User("nome", "cognome");
        User user3 = new User("nome", "cognome");
        user2.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        user3.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        persona2.setUser(user2);
        persona3.setUser(user3);
        destinatari2.add(persona2);
        destinatari2.add(persona3);

        Persona persona4 = new Persona("Nome", "Cognome", "");
        User user4 = new User("admin", "admin");
        user4.addRole(new Role(Role.PQA_ROLE));
        user4.setPersona(persona4);
        persona4.setUser(user4);
        */
        return Stream.of(
                Arguments.of(possibiliDest1, destinatari1, pqaRole.getName(), true, user1)
                //Arguments.of(possibiliDest2, destinatari2, capogruppoRole.getName(), false, user4)
        );
    }

/*
    @Test
    void sendDocumento() {
    }*/

    @ParameterizedTest
    @MethodSource("provideConsegneInviate")
    void findConsegneInviate(final List<Consegna> consegne, final User userLoggato) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(userLoggato);
        personaDAO.save(userLoggato.getPersona());
        userDAO.save(userLoggato);

        for (int i = 0; i < consegne.size(); i++) {
            documentoDAO.save(consegne.get(i).getDocumento());
            consegne.set(i, consegnaDAO.save(consegne.get(i)));
        }

        this.mockMvc.perform(get("/consegna/inviati")
                .param("name", "")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("consegne", consegne))
                .andExpect(view().name("consegna/documenti-inviati"));
    }

    private static Stream<Arguments> provideConsegneInviate() {
        //test1
        Persona persona1 = new Persona("Nome", "Cognome", "");
        User user1 = new User("admin", "admin");
        user1.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        user1.setPersona(persona1);
        persona1.setUser(user1);

        Documento documento1 = new Documento();
        documento1.setNome("Ciao");

        Consegna consegna = new Consegna();
        consegna.setId(1);
        consegna.setMittente(persona1);
        consegna.setDocumento(documento1);

        List<Consegna> test1 = new ArrayList<>();
        test1.add(consegna);

        //test2
        /*Persona persona2 = new Persona("Nome", "Cognome", "");
        User user2 = new User("admin", "admin");
        user2.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        user2.setPersona(persona2);
        persona2.setUser(user2);

        Documento documento2 = new Documento();
        documento2.setNome("Documento");
        Documento documento3 = new Documento();
        documento3.setNome("Documento3");

        Consegna consegna2 = new Consegna();
        consegna2.setId(2);
        consegna2.setMittente(persona2);
        consegna2.setDocumento(documento2);

        Consegna consegna3 = new Consegna();
        consegna3.setId(3);
        consegna3.setMittente(persona2);
        consegna3.setDocumento(documento3);

        List<Consegna> test2 = new ArrayList<>();
        test2.add(consegna2);
        test2.add(consegna3);*/

        return Stream.of(
                Arguments.of(test1, user1)
                //Arguments.of(test2, user2)
        );

    }

    @ParameterizedTest
    @MethodSource("provideConsegneRicevute")
    void findConsegneRicevute(final List<Consegna> consegne, final User userLoggato) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(userLoggato);
        personaDAO.save(userLoggato.getPersona());
        userDAO.save(userLoggato);

        for (int i = 0; i < consegne.size(); i++) {
            documentoDAO.save(consegne.get(i).getDocumento());
            consegne.set(i, consegnaDAO.save(consegne.get(i)));
        }

        this.mockMvc.perform(get("/consegna/ricevuti")
                .param("name", "")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("consegne", consegne))
                .andExpect(view().name("consegna/documenti-ricevuti"));
    }

    private static Stream<Arguments> provideConsegneRicevute() {
        Persona persona1 = new Persona("Nome", "Cognome", "");
        User user1 = new User("admin", "admin");
        user1.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        user1.setPersona(persona1);
        persona1.setUser(user1);

        Documento documento1 = new Documento();
        documento1.setNome("Ciao");

        Consegna consegna = new Consegna();
        consegna.setId(1);
        consegna.setMittente(persona1);
        consegna.setDestinatario(persona1);
        consegna.setDocumento(documento1);

        List<Consegna> test1 = new ArrayList<>();
        test1.add(consegna);

        return Stream.of(
                Arguments.of(test1, user1)
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
    }*/

    @ParameterizedTest
    @MethodSource("provideApprovaConsegna")
    void approvaConsegna(final List<Consegna> consegne, final User userLoggato) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(userLoggato);
        personaDAO.save(userLoggato.getPersona());
        userDAO.save(userLoggato);
        List<Consegna> consegneExpected = new ArrayList<>();

        for (int i = 0; i < consegne.size(); i++) {
            documentoDAO.save(consegne.get(i).getDocumento());
            consegne.set(i, consegnaDAO.save(consegne.get(i)));
        }

        for (int i = 0; i < consegne.size(); i++) {
            consegneExpected.add(consegne.get(i));
            Consegna c = consegneExpected.get(i);
            c.setStato("APPROVATA");
            consegneExpected.set(i, c);
        }

        this.mockMvc.perform(get("/consegna/approva/{id}", consegne.get(0).getId())
                .param("name", "")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("consegne", consegneExpected))
                .andExpect(view().name("consegna/documenti-ricevuti"));
    }

    private static Stream<Arguments> provideApprovaConsegna() {

        Persona persona1 = new Persona("Nome", "Cognome", "");
        User user1 = new User("admin", "admin");
        user1.addRole(new Role(Role.PQA_ROLE));
        user1.setPersona(persona1);
        persona1.setUser(user1);


        Documento documento1 = new Documento();
        documento1.setNome("Ciao");

        Consegna consegna = new Consegna();
        consegna.setId(1);
        consegna.setStato("DA VALUTARE");
        consegna.setMittente(persona1);
        consegna.setDestinatario(persona1);
        consegna.setDocumento(documento1);

        List<Consegna> test1 = new ArrayList<>();
        test1.add(consegna);

        return Stream.of(
                Arguments.of(test1, user1)
        );

    }

    @ParameterizedTest
    @MethodSource("provideRifiutaConsegna")
    void rifiutaConsegna(final List<Consegna> consegne, final User userLoggato) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(userLoggato);
        personaDAO.save(userLoggato.getPersona());
        userDAO.save(userLoggato);
        List<Consegna> consegneExpected = new ArrayList<>();

        for (int i = 0; i < consegne.size(); i++) {
            documentoDAO.save(consegne.get(i).getDocumento());
            consegne.set(i, consegnaDAO.save(consegne.get(i)));
        }

        for (int i = 0; i < consegne.size(); i++) {
            consegneExpected.add(consegne.get(i));
            Consegna c = consegneExpected.get(i);
            c.setStato("RIFIUTATA");
            consegneExpected.set(i, c);
        }

        this.mockMvc.perform(get("/consegna/rifiuta/{id}", consegne.get(0).getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("consegne", consegneExpected))
                .andExpect(view().name("consegna/documenti-ricevuti"));
    }

    private static Stream<Arguments> provideRifiutaConsegna() {


        Persona persona1 = new Persona("Nome", "Cognome", "");
        User user1 = new User("admin", "admin");
        user1.addRole(new Role(Role.PQA_ROLE));
        user1.setPersona(persona1);
        persona1.setUser(user1);


        Documento documento1 = new Documento();
        documento1.setNome("Ciao");

        Consegna consegna = new Consegna();
        consegna.setId(1);
        consegna.setStato("DA VALUTARE");
        consegna.setMittente(persona1);
        consegna.setDestinatario(persona1);
        consegna.setDocumento(documento1);

        List<Consegna> test1 = new ArrayList<>();
        test1.add(consegna);

        return Stream.of(
                Arguments.of(test1, user1)
        );

    }

}
