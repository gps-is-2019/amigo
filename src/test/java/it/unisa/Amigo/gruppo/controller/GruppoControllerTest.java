package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.gruppo.domain.*;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.junit.Test;
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
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GruppoControllerTest {
    @MockBean
    private GruppoService gruppoService;

    @Autowired
    private MockMvc mockMvc;

    private static Stream<Arguments> provideFindAllMembriInSupergruppi() {
        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        ConsiglioDidattico consiglio1 = new ConsiglioDidattico("Informatica");
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);
        ConsiglioDidattico consiglio2 = new ConsiglioDidattico("Ingegneria");
        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");
        gruppo1.setId(1);
        gruppo2.setId(2);
        consiglio1.setId(3);
        consiglio2.setId(4);
        user.setId(5);
        user1.setId(6);
        persona1.setId(7);
        persona2.setId(8);
        return Stream.of(
                Arguments.of(user, persona1, gruppo1, consiglio1),
                Arguments.of(user1, persona2, gruppo2, consiglio2)
        );
    }

    private static Stream<Arguments> provideAllSupergruppi() {
        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);
        Gruppo gruppo3 = new Gruppo("GAQR- Ingegneria", "Gruppo", true);
        Gruppo gruppo4 = new Gruppo("GAQD- Ingegneria", "Gruppo", true);
        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");
        gruppo1.setId(1);
        gruppo2.setId(2);
        gruppo3.setId(3);
        gruppo4.setId(4);
        user.setId(5);
        user.setId(6);
        persona1.setId(7);
        persona2.setId(8);
        return Stream.of(
                Arguments.of(user, persona1, gruppo1, gruppo2),
                Arguments.of(user1, persona2, gruppo4, gruppo3)
        );
    }

    private static Stream<Arguments> provideAddMembro() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");
        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");
        Supergruppo supergruppo = new Commissione("Commissione", "Commissione", true, "");
        Supergruppo supergruppo1 = new Gruppo("gruppo", "gruppo", true);
        persona1.setId(1);
        persona2.setId(2);
        user.setId(3);
        user1.setId(4);
        supergruppo.setId(5);
        supergruppo1.setId(6);
        return Stream.of(
                Arguments.of(user, persona1, supergruppo),
                Arguments.of(user1, persona2, supergruppo1)
        );
    }

    private static Stream<Arguments> provideRemoveMembro() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");
        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");
        Supergruppo supergruppo = new Commissione("Commissione", "Commissione", true, "");
        Supergruppo supergruppo1 = new Gruppo("gruppo", "gruppo", true);
        persona1.setId(1);
        persona2.setId(2);
        user.setId(3);
        user1.setId(4);
        supergruppo.setId(5);
        supergruppo1.setId(6);
        return Stream.of(
                Arguments.of(user, persona1, supergruppo),
                Arguments.of(user1, persona2, supergruppo1)
        );
    }
/*
    @ParameterizedTest
    @MethodSource("provideGroupCandidatesList")
    public void groupCandidatesList(Persona persona1, Persona persona2, User user1, User user2) throws Exception{

        UserDetailImpl userDetails1 = new UserDetailImpl(user1);
        UserDetailImpl userDetails2 = new UserDetailImpl(user2);


        Persona expectedPersona1 = persona1;
        Persona expectedPersona2 = persona2;
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

    private static Stream<Arguments> provideGroupCandidatesList(){
        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");
        User user3 = new User("admin3", "admin3");
        User user4 = new User("admin4", "admin4");

        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");
        Persona persona3 = new Persona("persona3", "persona3", "persona");
        Persona persona4 = new Persona("persona4", "persona4", "persona");

        user.setId(1);
        user1.setId(2);
        user3.setId(3);
        user4.setId(4);
        persona1.setId(5);
        persona2.setId(6);
        persona3.setId(7);
        persona4.setId(8);
        return Stream.of(
                Arguments.of(persona1,persona2, user1, user),
                Arguments.of(persona3, persona4, user3,user4)
        );
    }*/

    private static Stream<Arguments> provideCloseCommissione() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");
        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");
        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);
        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione2", true, "");
        persona1.setId(1);
        persona2.setId(2);
        user.setId(3);
        user1.setId(4);
        gruppo1.setId(5);
        gruppo2.setId(6);
        commissione1.setId(7);
        commissione2.setId(8);
        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );
    }

    private static Stream<Arguments> provideCreateCommissioneForm() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");
        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");
        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione2", true, "");
        persona1.setId(1);
        persona2.setId(2);
        user.setId(3);
        user1.setId(4);
        commissione1.setId(5);
        commissione2.setId(6);
        return Stream.of(
                Arguments.of(user, persona1, commissione1),
                Arguments.of(user1, persona2, commissione2)
        );
    }

    private static Stream<Arguments> provideAddMembroCommissione() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");
        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");
        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);
        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione2", true, "");
        persona1.setId(1);
        persona2.setId(2);
        user.setId(3);
        user1.setId(4);
        gruppo1.setId(5);
        gruppo2.setId(6);
        commissione1.setId(7);
        commissione2.setId(8);
        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );

    }

    @ParameterizedTest
    @MethodSource("provideFindAllMembriInSupergruppi")
    public void findAllMembriInSupergruppo(User userArg, Persona personaArg, Gruppo gruppo, ConsiglioDidattico consiglioDidattico) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(userArg);
        personaArg.setUser(userArg);
        gruppo.addPersona(personaArg);
        gruppo.setResponsabile(personaArg);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(personaArg);
        when(gruppoService.getAuthenticatedUser()).thenReturn(personaArg);
        when(gruppoService.findSupergruppo(gruppo.getId())).thenReturn(gruppo);
        when(gruppoService.findAllMembriInSupergruppo(gruppo.getId())).thenReturn(expectedPersone);
        when(gruppoService.isResponsabile(personaArg.getId(), gruppo.getId())).thenReturn(true);
        when(gruppoService.findConsiglioBySupergruppo(gruppo.getId())).thenReturn(consiglioDidattico);
        this.mockMvc.perform(get("/gruppi/{id}", gruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata", personaArg.getId()))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(model().attribute("supergruppo", gruppo))
                .andExpect(model().attribute("isCapogruppo", gruppoService.isResponsabile(personaArg.getId(), gruppo.getId())))
                .andExpect(view().name("gruppo/gruppo_detail"));
    }

    @ParameterizedTest
    @MethodSource("provideAllSupergruppi")
    public void findAllSupergruppi(User userArg, Persona personaArg, Gruppo gruppo1, Gruppo gruppo2) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(userArg);
        personaArg.setUser(userArg);
        List<Supergruppo> expectedSupergruppi = new ArrayList<>();
        expectedSupergruppi.add(gruppo2);
        expectedSupergruppi.add(gruppo1);
        personaArg.addSupergruppo(gruppo2);
        personaArg.addSupergruppo(gruppo1);
        when(gruppoService.findAllSupergruppiOfPersona(personaArg.getId())).thenReturn(expectedSupergruppi);
        when(gruppoService.getAuthenticatedUser()).thenReturn(personaArg);
        this.mockMvc.perform(get("/gruppi")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata", personaArg.getId()))
                .andExpect(model().attribute("supergruppi", expectedSupergruppi))
                .andExpect(view().name("gruppo/miei_gruppi"));
    }

    @ParameterizedTest
    @MethodSource("provideAddMembro")
    public void addMembro(User user, Persona persona, Supergruppo supergruppo) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        persona.setUser(user);
        persona.addSupergruppo(supergruppo);
        List<Persona> persone = new ArrayList<>();
        persone.add(persona);
        when(gruppoService.findPersona(persona.getId())).thenReturn(persona);
        when(gruppoService.findSupergruppo(supergruppo.getId())).thenReturn(supergruppo);
        when(gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(supergruppo.getId())).thenReturn(persone);
        when(gruppoService.findPersona(persona.getId())).thenReturn(persona);
        when(gruppoService.getAuthenticatedUser()).thenReturn(persona);
        when(gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), supergruppo.getId())).thenReturn(true);
        if (supergruppo.getType().equalsIgnoreCase("commissione")) {
            this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}", supergruppo.getId(), persona.getId())
                    .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("gruppo/aggiunta_membro_commissione"));
        }
        if (supergruppo.getType().equalsIgnoreCase("gruppo")) {
            this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}", supergruppo.getId(), persona.getId())
                    .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("gruppo/aggiunta_membro"));
        }

    }

    @ParameterizedTest
    @MethodSource("provideRemoveMembro")
    public void removeMembro(User user, Persona persona, Supergruppo supergruppo) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        persona.setUser(user);
        supergruppo.setResponsabile(persona);
        persona.addSupergruppo(supergruppo);
        Gruppo gruppo = new Gruppo("Gruppo", "gruppo", true);
        when(gruppoService.findPersona(persona.getId())).thenReturn(persona);
        when(gruppoService.findSupergruppo(supergruppo.getId())).thenReturn(supergruppo);
        when(gruppoService.getAuthenticatedUser()).thenReturn(persona);
        when(gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), supergruppo.getId())).thenReturn(true);
        when(gruppoService.findGruppoByCommissione(supergruppo.getId())).thenReturn(gruppo);
        when(gruppoService.isResponsabile(gruppoService.getAuthenticatedUser().getId(), gruppoService.findGruppoByCommissione(supergruppo.getId()).getId())).thenReturn(true);
        if (supergruppo.getType().equalsIgnoreCase("commissione")) {
            Commissione commissione = (Commissione) supergruppo;
            commissione.setGruppo(gruppo);
            this.mockMvc.perform(get("/gruppi/{idSupergruppo}/remove/{idPersona}", supergruppo.getId(), persona.getId())
                    .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(view().name("gruppo/commissione_detail"));
        }
        if (supergruppo.getType().equalsIgnoreCase("gruppo")) {
            this.mockMvc.perform(get("/gruppi/{idSupergruppo}/remove/{idPersona}", supergruppo.getId(), persona.getId())
                    .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(view().name("gruppo/gruppo_detail"));
        }
    }

    @Test
    public void findAllMembriInCommissione() throws Exception {
        User user = new User("admin", "admin");
        user.setId(1);
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setId(2);
        expectedPersona.setUser(user);
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        expectedCommissione.setId(3);
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedGruppo.setId(4);
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
                .andExpect(model().attribute("isCapogruppo", false))
                .andExpect(model().attribute("isResponsabile", true))
                .andExpect(model().attribute("persone", persone))
                .andExpect(model().attribute("supergruppo", expectedCommissione))
                .andExpect(model().attribute("personaLoggata", expectedPersona.getId()))
                .andExpect(view().name("gruppo/commissione_detail"));
    }

    @ParameterizedTest
    @MethodSource("provideCloseCommissione")
    public void closeCommissione(User user, Persona persona, Commissione commissione, Gruppo gruppo) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        persona.setUser(user);
        commissione.addPersona(persona);
        commissione.setResponsabile(persona);
        List<Persona> persone = new ArrayList<>();
        persone.add(persona);
        commissione.setGruppo(gruppo);
        when(gruppoService.getAuthenticatedUser()).thenReturn(persona);
        when(gruppoService.isResponsabile(persona.getId(), commissione.getId())).thenReturn(true);
        when(gruppoService.findGruppoByCommissione(commissione.getId())).thenReturn(gruppo);
        when(gruppoService.isResponsabile(persona.getId(), gruppo.getId())).thenReturn(true);
        when(gruppoService.findSupergruppo(commissione.getId())).thenReturn(commissione);
        when(gruppoService.findAllMembriInSupergruppo(commissione.getId())).thenReturn(persone);
        when(gruppoService.findGruppoByCommissione(commissione.getId())).thenReturn(gruppo);
        this.mockMvc.perform(get("/gruppi/commissioni/{id2}/chiusura", commissione.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isCapogruppo", true))
                .andExpect(model().attribute("isResponsabile", true))
                .andExpect(model().attribute("persone", persone))
                .andExpect(model().attribute("supergruppo", commissione))
                .andExpect(model().attribute("personaLoggata", persona.getId()))
                .andExpect(model().attribute("flagChiusura", 1))
                .andExpect(view().name("gruppo/commissione_detail"));
    }

    /*@ParameterizedTest
    @MethodSource("provideNominaResponsabile")
    public void nominaResponsabile(User user, Persona persona, Commissione commissione, Gruppo gruppo) throws Exception {


        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = persona;
        expectedPersona.setUser(user);

        Commissione expectedCommissione = commissione;
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);

        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);

        Gruppo expectedGruppo = gruppo;
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

    private static Stream<Arguments> provideNominaResponsabile(){
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");

        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");

        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);

        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione2", true, "");

        persona1.setId(1);
        persona2.setId(2);
        user.setId(3);
        user1.setId(4);
        gruppo1.setId(5);
        gruppo2.setId(6);
        commissione1.setId(7);
        commissione2.setId(8);
        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );
    }*/

    @ParameterizedTest
    @MethodSource("provideCreateCommissioneForm")
    public void createCommissioneForm(User user, Persona persona, Commissione commissione) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        persona.setUser(user);
        commissione.addPersona(persona);
        commissione.setResponsabile(persona);
        GruppoFormCommand gruppoFormCommand = new GruppoFormCommand();
        this.mockMvc.perform(get("/gruppi/{id}/commissioni/create", commissione.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("idGruppo", commissione.getId()))
                .andExpect(model().attribute("command", gruppoFormCommand))
                .andExpect(view().name("gruppo/crea_commissione"));
    }

    @ParameterizedTest
    @MethodSource("provideAddMembroCommissione")
    public void addMembroCommissione(User user, Persona persona, Commissione commissione, Gruppo gruppo) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        persona.setUser(user);
        List<Persona> persone = new ArrayList<>();
        persone.add(persona);
        commissione.setGruppo(gruppo);
        gruppo.addPersona(persona);
        when(gruppoService.getAuthenticatedUser()).thenReturn(persona);
        when(gruppoService.isResponsabile(persona.getId(), commissione.getId())).thenReturn(true);
        when(gruppoService.findSupergruppo(commissione.getId())).thenReturn(commissione);
        when(gruppoService.findAllMembriInGruppoNoCommissione(commissione.getId())).thenReturn(persone);
        when(gruppoService.findGruppoByCommissione(commissione.getId())).thenReturn(gruppo);
        when(gruppoService.findPersona(persona.getId())).thenReturn(persona);
        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}", commissione.getId(), persona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("flagAggiunta", 1))
                .andExpect(model().attribute("personaAggiunta", persona))
                .andExpect(model().attribute("persone", persone))
                .andExpect(model().attribute("supergruppo", commissione))
                .andExpect(model().attribute("personaLoggata", persona.getId()))
                .andExpect(view().name("gruppo/aggiunta_membro_commissione"));
    }

    /*@ParameterizedTest
    @MethodSource("provideRemoveMembroCommissione")
    public void removeMembroCommissione(User user, Persona persona, Commissione commissione, Gruppo gruppo) throws Exception {


        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = persona;
        expectedPersona.setUser(user);


        Commissione expectedCommissione = commissione;
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);


        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);

        Gruppo expectedGruppo = gruppo;
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

    private static Stream<Arguments> provideRemoveMembroCommissione(){

        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");

        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");

        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);

        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione2", true, "");

        persona1.setId(1);
        persona2.setId(2);
        user.setId(3);
        user1.setId(4);
        gruppo1.setId(5);
        gruppo2.setId(6);
        commissione1.setId(7);
        commissione2.setId(8);
        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );
    }*/
}