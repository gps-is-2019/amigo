package it.unisa.Amigo.consegna.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.consegna.services.ConsegnaService;
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

    @Test
    void testSendDocumento() {
    }

    @Test
    void findConsegneInviate() {
    }

    @Test
    void findConsegneRicevute() {
    }

    @Test
    void downloadDocumento() {
    }

    @Test
    void testViewConsegna() {
    }

    @Test
    void testSendDocumento1() {
    }

    @Test
    void testFindConsegneInviate() {
    }

    @Test
    void testFindConsegneRicevute() {
    }

    @Test
    void testDownloadDocumento() {
    }

    @Test
    void testViewConsegna1() {
    }

    @Test
    void testSendDocumento2() {
    }

    @Test
    void testFindConsegneInviate1() {
    }

    @Test
    void testFindConsegneRicevute1() {
    }

    @Test
    void testDownloadDocumento1() {
    }

    @Test
    void approvaConsegna() {
    }

    @Test
    void rifiutaConsegna() {
    }
}