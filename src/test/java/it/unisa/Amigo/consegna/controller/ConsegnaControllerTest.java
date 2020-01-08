package it.unisa.Amigo.consegna.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.consegna.services.ConsegnaService;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = WebMvcAutoConfiguration.class)
//@WebMvcTest(GruppoController.class)
@SpringBootTest
@AutoConfigureMockMvc
class ConsegnaControllerTest {

    @MockBean
    private ConsegnaService consegnaService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GruppoService gruppoService;


    @ParameterizedTest
    @MethodSource("provideDestinatari")
    void viewConsegna(Set<String> possibiliDestinatari, List<Persona> destinatari, String ruoloDest) throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        when(consegnaService.possibiliDestinatari()).thenReturn(possibiliDestinatari);
        when(gruppoService.findAllByRuolo(ruoloDest)).thenReturn(destinatari);

        this.mockMvc.perform(get("/consegna/{ruolo}", ruoloDest)
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("possibiliDestinatari", possibiliDestinatari))
                .andExpect(model().attribute("destinatari", destinatari))
                .andExpect(model().attribute("ruoloDest", ruoloDest))
                .andExpect(model().attribute("flagRuolo", false))
                .andExpect(view().name("consegna/destinatari"));

    }
    private static Stream<Arguments> provideDestinatari() {
        Role role = new Role(Role.PQA_ROLE);
        Role role1 = new Role(Role.CAPOGRUPPO_ROLE);

        Set<String> test1 = new HashSet<>();
        test1.add(role.getName());
        Set<String> test2 = new HashSet<>();
        test2.add(role.getName());
        test2.add(role1.getName());

        Persona persona1 = new Persona("persona1", "persona1", "");
        Persona persona2 = new Persona("persona2", "persona2", "");

        ArrayList<Persona> dest1 = new ArrayList<>();
        dest1.add(persona1);
        dest1.add(persona2);

        ArrayList<Persona> dest2 = new ArrayList<>();
        dest2.add(persona1);

        String ruolo1 = Role.NDV_ROLE;
        String ruolo2 = Role.CAPOGRUPPO_ROLE;

        return Stream.of(
                Arguments.of(test1, dest2, role1.getName()),
                Arguments.of(test2, dest1, role.getName()),
                Arguments.of(test2, dest2, role.getName()),
                Arguments.of(test1, dest1, role1.getName())
                );
    }

    @Test
    void sendDocumento() {
    }

    @ParameterizedTest
    @MethodSource("provideConsegneInviate")
    void findConsegneInviate(List<Consegna> consegne) throws Exception {

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

    private static Stream<Arguments> provideConsegneInviate(){

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
    void findConsegneRicevute(List<Consegna> consegne) throws Exception {

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

    private static Stream<Arguments> provideConsegneRicevute(){
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

    /*@ParameterizedTest
    @MethodSource("provideDownloadDocumento")
    void downloadDocumento(Consegna consegna, Persona personaLoggata) throws Exception {

        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona persona1 = new Persona("persona1", "persona1", "");
        persona1.setUser(user);



        when(consegnaService.findConsegnaByDocumento(consegna.getDocumento().getId())).thenReturn(consegna);
        when(gruppoService.getAuthenticatedUser()).thenReturn(personaLoggata);

        /* Come ritorno alla pagina di download?

        this.mockMvc.perform(get("/consegna/miei-documenti/{idDocumento}", consegna.getDocumento().getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("consegna/documenti-ricevuti"));


    }*/

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

    @ParameterizedTest
    @MethodSource("provideApprovaConsegna")
    void approvaConsegna(List<Consegna> consegne) throws Exception {

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

    private static Stream<Arguments> provideApprovaConsegna(){

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
    void rifiutaConsegna(List<Consegna> consegne) throws Exception {
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

    private static Stream<Arguments> provideRifiutaConsegna(){

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
}