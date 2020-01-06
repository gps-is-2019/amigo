package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.gruppo.domain.*;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.junit.Test;
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
public class GruppoControllerTest {

    @MockBean
    private GruppoService gruppoService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void findAllMembriInSupergruppo() throws Exception {


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
        when(gruppoService.findAllMembriInSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedPersone);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())).thenReturn(true);
        when(gruppoService.findConsiglioBySupergruppo(expectedSupergruppo.getId())).thenReturn(expectedConsiglioDidattico);


        this.mockMvc.perform(get("/gruppi/{id}", expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata", expectedPersona.getId()))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(model().attribute("supergruppo", expectedSupergruppo))
                .andExpect(model().attribute("isCapogruppo", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(view().name("gruppo/gruppo_detail"));
    }


    @Test
    public void findAllSupergruppi() throws Exception {

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

        when(gruppoService.findAllSupergruppiOfPersona(expectedPersona.getId())).thenReturn(expectedSupergruppi);
        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);

        this.mockMvc.perform(get("/gruppi")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata", expectedPersona.getId()))
                .andExpect(model().attribute("supergruppi", expectedSupergruppi))
                .andExpect(view().name("gruppo/miei_gruppi"));
    }

    @Test
    public void groupCandidatesList() throws Exception {

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
                .andExpect(model().attribute("personaLoggata", expectedPersona1.getId()))
                .andExpect(model().attribute("supergruppo", expectedSupergruppo))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(view().name("gruppo/aggiunta_membro"));
    }

    @Test
    public void addMembro() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedPersona.addSupergruppo(expectedSupergruppo);

        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);

        when(gruppoService.findPersona(expectedPersona.getId())).thenReturn(expectedPersona);
        when(gruppoService.findSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedSupergruppo);
        when(gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(expectedSupergruppo.getId())).thenReturn(persone);
        when(gruppoService.findPersona(expectedPersona.getId())).thenReturn(expectedPersona);
        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), expectedSupergruppo.getId())).thenReturn(true);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}", expectedSupergruppo.getId(), expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("gruppo/aggiunta_membro"));

    }


    @Test
    public void removeMembro() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedPersona.addSupergruppo(expectedSupergruppo);

        when(gruppoService.findPersona(expectedPersona.getId())).thenReturn(expectedPersona);
        when(gruppoService.findSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedSupergruppo);
        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), expectedSupergruppo.getId())).thenReturn(true);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/remove/{idPersona}", expectedSupergruppo.getId(), expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(view().name("gruppo/gruppo_detail"));
    }

    @Test
    public void findAllMembriInCommissione() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);

        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);
        expectedGruppo.addCommissione(expectedCommissione);

        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.findGruppoByCommissione(expectedCommissione.getId())).thenReturn(expectedGruppo);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedCommissione.getId())).thenReturn(true);
        when(gruppoService.findSupergruppo(expectedCommissione.getId())).thenReturn(expectedCommissione);
        when(gruppoService.findAllMembriInSupergruppo(expectedCommissione.getId())).thenReturn(persone);


        this.mockMvc.perform(get("/gruppi/{id}/commissione_detail/{id_commissione}", expectedGruppo.getId(), expectedCommissione.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isCapogruppo", true))
                .andExpect(model().attribute("isResponsabile", true))
                .andExpect(model().attribute("persone", persone))
                .andExpect(model().attribute("supergruppo", expectedCommissione))
                .andExpect(model().attribute("personaLoggata", expectedPersona.getId()))
                .andExpect(view().name("gruppo/commissione_detail"));
    }

    @Test
    public void closeCommissione() throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);

        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);

        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedCommissione.setGruppo(expectedGruppo);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedCommissione.getId())).thenReturn(true);
        when(gruppoService.findSupergruppo(expectedCommissione.getId())).thenReturn(expectedCommissione);
        when(gruppoService.findAllMembriInSupergruppo(expectedCommissione.getId())).thenReturn(persone);
        when(gruppoService.findGruppoByCommissione(expectedCommissione.getId())).thenReturn(expectedGruppo);

        this.mockMvc.perform(get("/gruppi/commissioni/{id2}/chiusura", expectedCommissione.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isCapogruppo", true))
                .andExpect(model().attribute("isResponsabile", true))
                .andExpect(model().attribute("persone", persone))
                .andExpect(model().attribute("supergruppo", expectedCommissione))
                .andExpect(model().attribute("personaLoggata", expectedPersona.getId()))
                .andExpect(model().attribute("flagChiusura", 1))
                .andExpect(view().name("gruppo/commissione_detail"));
    }

    @Test
    public void createCommissioneForm() throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);

        GruppoFormCommand gruppoFormCommand = new GruppoFormCommand();

        this.mockMvc.perform(get("/gruppi/{id}/commissioni/create", expectedCommissione.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("idGruppo", expectedCommissione.getId()))
                .andExpect(model().attribute("command", gruppoFormCommand))
                .andExpect(view().name("gruppo/crea_commissione"));
    }

    @Test
    public void createCommissione() {
    }

    @Test
    public void nominaResponsabile() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);

        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);

        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedCommissione.setGruppo(expectedGruppo);


        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedCommissione.getId())).thenReturn(true);
        when(gruppoService.findSupergruppo(expectedCommissione.getId())).thenReturn(expectedCommissione);
        when(gruppoService.findAllMembriInSupergruppo(expectedCommissione.getId())).thenReturn(persone);
        when(gruppoService.findGruppoByCommissione(expectedCommissione.getId())).thenReturn(expectedGruppo);
        when(gruppoService.findPersona(expectedPersona.getId())).thenReturn(expectedPersona);


        this.mockMvc.perform(get("/gruppi/commissioni/{idCommissione}/nominaResponsabile/{idPersona}", expectedCommissione.getId(), expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("idCommissione", expectedCommissione.getId()))
                .andExpect(model().attribute("isCapogruppo", true))
                .andExpect(model().attribute("isResponsabile", true))
                .andExpect(model().attribute("persone", persone))
                .andExpect(model().attribute("supergruppo", expectedCommissione))
                .andExpect(model().attribute("personaLoggata", expectedPersona.getId()))
                .andExpect(model().attribute("flagNomina", 1))
                .andExpect(model().attribute("responsabile", expectedPersona))
                .andExpect(view().name("gruppo/commissione_detail"));
    }

    @Test
    public void addMembroCommissione() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");


        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);

        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedCommissione.setGruppo(expectedGruppo);
        expectedGruppo.addPersona(expectedPersona);


        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedCommissione.getId())).thenReturn(true);
        when(gruppoService.findSupergruppo(expectedCommissione.getId())).thenReturn(expectedCommissione);
        when(gruppoService.findAllMembriInGruppoNoCommissione(expectedCommissione.getId())).thenReturn(persone);
        when(gruppoService.findGruppoByCommissione(expectedCommissione.getId())).thenReturn(expectedGruppo);
        when(gruppoService.findPersona(expectedPersona.getId())).thenReturn(expectedPersona);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}", expectedCommissione.getId(), expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("flagAggiunta", 1))
                .andExpect(model().attribute("personaAggiunta", expectedPersona))
                .andExpect(model().attribute("persone", persone))
                .andExpect(model().attribute("supergruppo", expectedCommissione))
                .andExpect(model().attribute("personaLoggata", expectedPersona.getId()))
                .andExpect(view().name("gruppo/aggiunta_membro_commissione"));

    }

    @Test
    public void removeMembroCommissione() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);


        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);


        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);

        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedCommissione.setGruppo(expectedGruppo);
        expectedGruppo.addPersona(expectedPersona);

        List<Commissione> commissioni = new ArrayList<>();
        commissioni.add(expectedCommissione);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedCommissione.getId())).thenReturn(true);
        when(gruppoService.findSupergruppo(expectedCommissione.getId())).thenReturn(expectedCommissione);
        when(gruppoService.findAllMembriInSupergruppo(expectedCommissione.getId())).thenReturn(persone);
        when(gruppoService.findGruppoByCommissione(expectedCommissione.getId())).thenReturn(expectedGruppo);
        when(gruppoService.findPersona(expectedPersona.getId())).thenReturn(expectedPersona);
        when(gruppoService.findAllCommissioniByGruppo(expectedGruppo.getId())).thenReturn(commissioni);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/remove/{idPersona}", expectedGruppo.getId(), expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(model().attribute("isCapogruppo", true))
                .andExpect(model().attribute("isResponsabile", true))
                .andExpect(model().attribute("flagRimozione", 1))
                .andExpect(model().attribute("personaRimossa", expectedPersona))
                .andExpect(view().name("gruppo/commissione_detail"));

    }
}