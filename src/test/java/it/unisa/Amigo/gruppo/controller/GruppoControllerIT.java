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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = WebMvcAutoConfiguration.class)
//@WebMvcTest(GruppoController.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GruppoControllerIT {


    @Autowired
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

        /*when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.findSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedSupergruppo);
        when( gruppoService.findAllMembriInSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedPersone);
        when( gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(true);
        when( gruppoService.findConsiglioBySupergruppo(expectedSupergruppo.getId())).thenReturn(expectedConsiglioDidattico);*/



        this.mockMvc.perform(get("/gruppi/{id}", expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata",expectedPersona.getId()))
                .andExpect(model().attribute("persone" ,expectedPersone))
                .andExpect(model().attribute("supergruppo", expectedSupergruppo))
                .andExpect(model().attribute("isCapogruppo", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(view().name("gruppo/gruppo_detail"));
    }
}
