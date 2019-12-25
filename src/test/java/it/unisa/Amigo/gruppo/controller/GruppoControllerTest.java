package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = WebMvcAutoConfiguration.class)
//@WebMvcTest(GruppoController.class)
@SpringBootTest
@AutoConfigureMockMvc
class GruppoControllerTest {

    @MockBean
    private GruppoService gruppoService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void findAllMembriInSupergruppo() throws Exception {


        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        ConsiglioDidattico expectedConsiglioDidattico = new ConsiglioDidattico("Informatica");
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.findSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedSupergruppo);
        when( gruppoService.findAllMembriInSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedPersone);
        when( gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(true);
        when( gruppoService.findConsiglioBySupergruppo(expectedSupergruppo.getId())).thenReturn(expectedConsiglioDidattico);


        this.mockMvc.perform(get("/gruppi/{id}", expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata",expectedPersona.getId()))
                .andExpect(model().attribute("persone" ,expectedPersone))
                .andExpect(model().attribute("supergruppo", expectedSupergruppo))
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(view().name("gruppo/gruppo_detail"));
    }


    @Test
    void findAllSupergruppi() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo expectedSupergruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        List<Supergruppo> expectedSupergruppi = new ArrayList<>();
        expectedSupergruppi.add(expectedSupergruppo1);
        expectedSupergruppi.add(expectedSupergruppo2);
        expectedPersona.addSupergruppo(expectedSupergruppo1);
        expectedPersona.addSupergruppo(expectedSupergruppo2);

        when(gruppoService.findAllSupergruppi(expectedPersona.getId())).thenReturn(expectedSupergruppi);
        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);

        this.mockMvc.perform(get("/gruppi")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata",expectedPersona.getId()))
                .andExpect(model().attribute("supergruppi", expectedSupergruppi))
                .andExpect(view().name("gruppo/miei_gruppi"));
    }

    @Test
    void findAllMembriInConsiglioDidatticoNoSupergruppo() throws Exception {

        User user1 = new User("admin", "admin");
        UserDetailImpl userDetails1 = new UserDetailImpl(user1);
        User user2 = new User("admin2", "admin2");
        UserDetailImpl userDetails2 = new UserDetailImpl(user2);

        Persona expectedPersona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona expectedPersona2 = new Persona("Persona2", "Persona2", "Persona");
        expectedPersona1.setUser(user1);
        expectedPersona2.setUser(user2);

        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona1);
        expectedPersone.add(expectedPersona2);

        Supergruppo expectedSupergruppo = new Supergruppo("GAQR - Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona2);

        ConsiglioDidattico expectedConsiglioDidattico = new ConsiglioDidattico("Informatica");
        expectedConsiglioDidattico.addPersona(expectedPersona1);
        expectedConsiglioDidattico.addPersona(expectedPersona2);

        when(gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(expectedPersona1.getId())).thenReturn(expectedPersone);
        when(gruppoService.findSupergruppo(expectedPersona2.getId())).thenReturn(expectedSupergruppo);
        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona1);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/candidati", expectedSupergruppo.getId())
                .with(user(userDetails1)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata",expectedPersona1.getId()))
                .andExpect(model().attribute("supergruppo", expectedSupergruppo))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(view().name("gruppo/aggiunta-membro"));
    }

    @Test
    void addMembro() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedPersona.addSupergruppo(expectedSupergruppo);

        when(gruppoService.findPersona(expectedPersona.getId())).thenReturn(expectedPersona);
        when(gruppoService.findSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedSupergruppo);
        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}",expectedSupergruppo.getId(), expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("gruppo/aggiunta-membro"));

    }


    @Test
    void removeMembro() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedPersona.addSupergruppo(expectedSupergruppo);

        when(gruppoService.findPersona(expectedPersona.getId())).thenReturn(expectedPersona);
        when(gruppoService.findSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedSupergruppo);
        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/remove/{idPersona}", expectedSupergruppo.getId(), expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(view().name("gruppo/gruppo_detail"));
    }
}