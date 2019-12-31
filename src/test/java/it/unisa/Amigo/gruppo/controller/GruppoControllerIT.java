package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.DipartimentoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.*;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
public class GruppoControllerIT {


    @Autowired
    private GruppoService gruppoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonaDAO personaDAO;

    @Autowired
    private SupergruppoDAO supergruppoDAO;

    @Autowired
    private ConsiglioDidatticoDAO consiglioDidatticoDAO;

    @Autowired
    private DipartimentoDAO dipartimentoDAO;

    @Autowired
    private UserDAO userDAO;


    @Test
    public void findAllMembriInSupergruppo() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Gruppo expectedGruppo = new Gruppo("GAQD- Informatica", "Gruppo", true);
        ConsiglioDidattico expectedConsiglioDidattico = new ConsiglioDidattico("Informatica");
        expectedGruppo.addPersona(expectedPersona);
        expectedGruppo.setResponsabile(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);

        personaDAO.save(expectedPersona);
        userDAO.save(user);
        supergruppoDAO.save(expectedGruppo);
        consiglioDidatticoDAO.save(expectedConsiglioDidattico);

        this.mockMvc.perform(get("/gruppi/{id}", expectedGruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata",expectedPersona.getId()))
                .andExpect(model().attribute("persone" ,expectedPersone))
                .andExpect(model().attribute("supergruppo", expectedGruppo))
                .andExpect(model().attribute("isCapogruppo", gruppoService.isResponsabile(expectedPersona.getId(),expectedGruppo.getId())))
                .andExpect(view().name("gruppo/gruppo_detail"));
    }


    @Test
    public void findAllSupergruppi() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Gruppo expectedSupergruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo expectedSupergruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);
        List<Gruppo> expectedSupergruppi = new ArrayList<>();
        expectedSupergruppi.add(expectedSupergruppo2);
        expectedSupergruppi.add(expectedSupergruppo1);
        expectedPersona.addSupergruppo(expectedSupergruppo2);
        expectedPersona.addSupergruppo(expectedSupergruppo1);

        personaDAO.save(expectedPersona);
        userDAO.save(user);
        supergruppoDAO.save(expectedSupergruppo1);
        supergruppoDAO.save(expectedSupergruppo2);

        this.mockMvc.perform(get("/gruppi")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata",expectedPersona.getId()))
                .andExpect(model().attribute("supergruppi", expectedSupergruppi))
                .andExpect(view().name("gruppo/miei_gruppi"));
    }


    @Test
    public void groupCandidatesList() throws Exception {

        User user1 = new User("admin", "admin");
        UserDetailImpl userDetails1 = new UserDetailImpl(user1);
        User user2 = new User("admin2", "admin2");


        Persona expectedPersona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona expectedPersona2 = new Persona("Persona2", "Persona2", "Persona");
        expectedPersona1.setUser(user1);
        expectedPersona2.setUser(user2);

        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona1);



        ConsiglioDidattico expectedConsiglioDidattico = new ConsiglioDidattico("Informatica");
        expectedConsiglioDidattico.addPersona(expectedPersona1);
        expectedConsiglioDidattico.addPersona(expectedPersona2);



        Supergruppo expectedSupergruppo = new Supergruppo("GAQR - Informatica", "Supergruppo", true);
        expectedSupergruppo.addPersona(expectedPersona2);
        expectedSupergruppo.setConsiglio(expectedConsiglioDidattico);

        personaDAO.save(expectedPersona1);
        personaDAO.save(expectedPersona2);
        userDAO.save(user1);
        userDAO.save(user2);
        consiglioDidatticoDAO.save(expectedConsiglioDidattico);
        supergruppoDAO.save(expectedSupergruppo);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/candidati", expectedSupergruppo.getId())
                .with(user(userDetails1)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata",expectedPersona1.getId()))
                .andExpect(model().attribute("supergruppo", expectedSupergruppo))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(view().name("gruppo/aggiunta_membro"));
    }

    @Test
    public void addMembroCommissione() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);


        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true,  "Commissione");
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedGruppo.addPersona(expectedPersona);
        expectedCommissione.addPersona(expectedPersona);
        expectedGruppo.addCommissione(expectedCommissione);

        personaDAO.save(expectedPersona);
        userDAO.save(user);
        supergruppoDAO.save(expectedGruppo);
        supergruppoDAO.save(expectedCommissione);



        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}",expectedCommissione.getId(), expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("gruppo/aggiunta_membro_commissione"));

    }

    @Test
    public void addMembroGruppo() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        ConsiglioDidattico expectedConsiglioDidattico = new ConsiglioDidattico("Informatica");
        expectedConsiglioDidattico.addPersona(expectedPersona);

        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true,  "Commissione");
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedGruppo.setConsiglio(expectedConsiglioDidattico);
        expectedGruppo.addPersona(expectedPersona);
        expectedGruppo.setResponsabile(expectedPersona);
        expectedCommissione.addPersona(expectedPersona);
        expectedGruppo.addCommissione(expectedCommissione);

        personaDAO.save(expectedPersona);
        userDAO.save(user);
        supergruppoDAO.save(expectedGruppo);
        supergruppoDAO.save(expectedCommissione);



        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}",expectedGruppo.getId(), expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("gruppo/aggiunta_membro"));

    }


    @Test
    public void removeMembroCommissione() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Commissione expectedCommissione = new Commissione("GAQD- Informatica", "gruppo", true, "");
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppo(expectedCommissione);
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedGruppo.addCommissione(expectedCommissione);
        expectedGruppo.addPersona(expectedPersona);
        expectedGruppo.setResponsabile(expectedPersona);


        personaDAO.save(expectedPersona);
        userDAO.save(user);
        supergruppoDAO.save(expectedGruppo);
        supergruppoDAO.save(expectedCommissione);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/remove/{idPersona}", expectedCommissione.getId(), expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(view().name("gruppo/commissione_detail"));
    }

    @Test
    public void removeMembroGruppo() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Commissione expectedCommissione = new Commissione("GAQD- Informatica", "gruppo", true, "");
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppo(expectedCommissione);
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedGruppo.addCommissione(expectedCommissione);
        expectedGruppo.addPersona(expectedPersona);
        expectedGruppo.setResponsabile(expectedPersona);


        personaDAO.save(expectedPersona);
        userDAO.save(user);
        supergruppoDAO.save(expectedGruppo);
        supergruppoDAO.save(expectedCommissione);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/remove/{idPersona}", expectedGruppo.getId(), expectedPersona.getId())
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
        Commissione expectedCommissione = new Commissione("GAQD- Informatica", "gruppo", true, "");
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppo(expectedCommissione);
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedGruppo.addCommissione(expectedCommissione);
        expectedGruppo.addPersona(expectedPersona);
        expectedGruppo.setResponsabile(expectedPersona);

        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);
        expectedGruppo.addCommissione(expectedCommissione);

        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);


        personaDAO.save(expectedPersona);
        userDAO.save(user);
        supergruppoDAO.save(expectedGruppo);
        supergruppoDAO.save(expectedCommissione);

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
    public  void closeCommissione() throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Commissione expectedCommissione = new Commissione("GAQD- Informatica", "gruppo", true, "");
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppo(expectedCommissione);
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedGruppo.addCommissione(expectedCommissione);
        expectedGruppo.addPersona(expectedPersona);
        expectedGruppo.setResponsabile(expectedPersona);

        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);
        expectedGruppo.addCommissione(expectedCommissione);

        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);


        personaDAO.save(expectedPersona);
        userDAO.save(user);
        supergruppoDAO.save(expectedGruppo);
        supergruppoDAO.save(expectedCommissione);
        expectedCommissione.setState(false);

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

        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true,  "Commissione");
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
    public  void nominaResponsabile() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Commissione expectedCommissione = new Commissione("GAQD- Informatica", "gruppo", true, "");
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppo(expectedCommissione);
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedGruppo.addCommissione(expectedCommissione);
        expectedGruppo.addPersona(expectedPersona);
        expectedGruppo.setResponsabile(expectedPersona);

        expectedCommissione.addPersona(expectedPersona);
        expectedGruppo.addCommissione(expectedCommissione);

        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);


        personaDAO.save(expectedPersona);
        userDAO.save(user);
        supergruppoDAO.save(expectedGruppo);
        supergruppoDAO.save(expectedCommissione);

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


}
