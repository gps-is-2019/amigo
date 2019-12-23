package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.AmigoApplication;
import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.configuration.UserDetailsServiceImpl;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.thymeleaf.context.IContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

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
    void visualizzaMembriSupergruppo() throws Exception {


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

        when(gruppoService.visualizzaPersonaLoggata()).thenReturn(expectedPersona);
        when(gruppoService.findSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedSupergruppo);
        when( gruppoService.visualizzaListaMembriSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedPersone);
        when( gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(true);
        when( gruppoService.findConsiglioBySupergruppo(expectedSupergruppo.getId())).thenReturn(expectedConsiglioDidattico);


        this.mockMvc.perform(get("/gruppo/visualizzaMembriSupergruppo={id}", expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata",expectedPersona.getId()))
                .andExpect(model().attribute("persone" ,expectedPersone))
                .andExpect(model().attribute("supergruppo", expectedSupergruppo))
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(model().attribute("idConsiglio", gruppoService.findConsiglioBySupergruppo(expectedSupergruppo.getId()).getId()))
                .andDo(print())
                .andExpect(view().name("gruppo/paginaVisualizzaMembri"));
    }


    @Test
    void visualizzaGruppi() throws Exception {

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

        when(gruppoService.visualizzaSupergruppi(expectedPersona.getId())).thenReturn(expectedSupergruppi);
        when(gruppoService.visualizzaPersonaLoggata()).thenReturn(expectedPersona);

        this.mockMvc.perform(get("/gruppo/visualizzaGruppi={idPersona}", expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata",expectedPersona.getId()))
                .andExpect(model().attribute("supergruppi", expectedSupergruppi))
                .andDo(print())
                .andExpect(view().name("gruppo/paginaIMieiGruppi"));
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

        when(gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(expectedPersona1.getId(),expectedPersona2.getId())).thenReturn(expectedPersone);
        when(gruppoService.findSupergruppo(expectedPersona2.getId())).thenReturn(expectedSupergruppo);
        when(gruppoService.visualizzaPersonaLoggata()).thenReturn(expectedPersona1);

        this.mockMvc.perform(get("/gruppo/id={id}&supergruppo={id2}", expectedConsiglioDidattico.getId(), expectedSupergruppo.getId())
                .with(user(userDetails1)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata",expectedPersona1.getId()))
                .andExpect(model().attribute("supergruppo", expectedSupergruppo))
                .andExpect(model().attribute("persone", expectedPersone))
                .andDo(print())
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

        this.mockMvc.perform(get("/gruppo/aggiungi/id={id}&supergruppo={id2}", expectedPersona.getId(), expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(view().name("gruppo/aggiungi"));

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

        this.mockMvc.perform(get("/gruppo/rimuovi/id={id}&supergruppo={id2}", expectedPersona.getId(), expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(view().name("gruppo/rimuovi"));
    }
}