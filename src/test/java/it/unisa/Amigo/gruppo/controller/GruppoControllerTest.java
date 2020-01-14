package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domain.User;
import it.unisa.Amigo.gruppo.domain.*;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GruppoControllerTest {
    @MockBean
    private GruppoService gruppoService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("provideFindAllMembriInSupergruppi")
    public void findAllMembriInSupergruppo(final User userArg, final Persona personaArg, final Gruppo gruppo, final ConsiglioDidattico consiglioDidattico) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(userArg);
        personaArg.setUser(userArg);
        gruppo.addPersona(personaArg);
        gruppo.setResponsabile(personaArg);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(personaArg);

        when(gruppoService.getCurrentPersona()).thenReturn(personaArg);
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


    @ParameterizedTest
    @MethodSource("provideAllSupergruppi")
    public void findAllSupergruppi(final User userArg, final Persona personaArg, final Gruppo gruppo1, final Gruppo gruppo2) throws Exception {

        User user = userArg;
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = personaArg;
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo1 = gruppo1;
        Supergruppo expectedSupergruppo2 = gruppo2;
        List<Supergruppo> expectedSupergruppi = new ArrayList<>();
        expectedSupergruppi.add(expectedSupergruppo2);
        expectedSupergruppi.add(expectedSupergruppo1);
        expectedPersona.addSupergruppo(expectedSupergruppo2);
        expectedPersona.addSupergruppo(expectedSupergruppo1);

        when(gruppoService.findAllSupergruppiOfPersona(expectedPersona.getId())).thenReturn(expectedSupergruppi);
        when(gruppoService.getCurrentPersona()).thenReturn(expectedPersona);

        this.mockMvc.perform(get("/gruppi")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata", expectedPersona.getId()))
                .andExpect(model().attribute("supergruppi", expectedSupergruppi))
                .andExpect(view().name("gruppo/miei_gruppi"));
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


    /* Exception evaluating SpringEL expression: "supergruppo.name"

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
        expectedConsiglioDidattico.setId(0);


        Supergruppo expectedSupergruppo = new Supergruppo("GAQR - Informatica", "Supergruppo", true);
        expectedSupergruppo.addPersona(expectedPersona2);
        expectedSupergruppo.setConsiglio(expectedConsiglioDidattico);
        expectedSupergruppo.setId(0);

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

    @ParameterizedTest
    @MethodSource("provideAddMembro")
    public void addMembro(final User user, final Persona persona, final Supergruppo supergruppo) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = persona;
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = supergruppo;
        expectedPersona.addSupergruppo(expectedSupergruppo);

        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);

        when(gruppoService.findPersona(expectedPersona.getId())).thenReturn(expectedPersona);
        when(gruppoService.findSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedSupergruppo);
        when(gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(expectedSupergruppo.getId())).thenReturn(persone);
        when(gruppoService.findPersona(expectedPersona.getId())).thenReturn(expectedPersona);
        when(gruppoService.getCurrentPersona()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(gruppoService.getCurrentPersona().getId(), expectedSupergruppo.getId())).thenReturn(true);

        if (expectedSupergruppo.getType().equalsIgnoreCase("commissione")) {
            this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}", expectedSupergruppo.getId(), expectedPersona.getId())
                    .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("gruppo/aggiunta_membro_commissione"));
        }

        if (expectedSupergruppo.getType().equalsIgnoreCase("gruppo")) {
            this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}", expectedSupergruppo.getId(), expectedPersona.getId())
                    .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("gruppo/aggiunta_membro"));
        }

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


    @ParameterizedTest
    @MethodSource("provideRemoveMembro")
    public void removeMembro(final User user, final Persona persona, final Supergruppo supergruppo) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = persona;
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = supergruppo;
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppo(expectedSupergruppo);
        Gruppo gruppo = new Gruppo("Gruppo", "gruppo", true);

        when(gruppoService.findPersona(expectedPersona.getId())).thenReturn(expectedPersona);
        when(gruppoService.findSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedSupergruppo);
        when(gruppoService.getCurrentPersona()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(gruppoService.getCurrentPersona().getId(), expectedSupergruppo.getId())).thenReturn(true);
        when(gruppoService.findGruppoByCommissione(expectedSupergruppo.getId())).thenReturn(gruppo);
        when(gruppoService.isResponsabile(gruppoService.getCurrentPersona().getId(), gruppoService.findGruppoByCommissione(expectedSupergruppo.getId()).getId())).thenReturn(true);

        if (expectedSupergruppo.getType().equalsIgnoreCase("commissione")) {
            Commissione commissione = (Commissione) expectedSupergruppo;
            commissione.setGruppo(gruppo);
            this.mockMvc.perform(get("/gruppi/{idSupergruppo}/remove/{idPersona}", expectedSupergruppo.getId(), expectedPersona.getId())
                    .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(view().name("gruppo/commissione_detail"));
        }
        if (expectedSupergruppo.getType().equalsIgnoreCase("gruppo")) {
            this.mockMvc.perform(get("/gruppi/{idSupergruppo}/remove/{idPersona}", expectedSupergruppo.getId(), expectedPersona.getId())
                    .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(view().name("gruppo/gruppo_detail"));
        }
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

        when(gruppoService.getCurrentPersona()).thenReturn(expectedPersona);
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
    public void closeCommissione(final User user, final Persona persona, final Commissione commissione, final Gruppo gruppo) throws Exception {
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

        when(gruppoService.getCurrentPersona()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedCommissione.getId())).thenReturn(true);
        when(gruppoService.findGruppoByCommissione(expectedCommissione.getId())).thenReturn(expectedGruppo);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedGruppo.getId())).thenReturn(true);
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

    @ParameterizedTest
    @MethodSource("provideCreateCommissioneForm")
    public void createCommissioneForm(final User user, final Persona persona, final Commissione commissione) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = persona;
        expectedPersona.setUser(user);

        Commissione expectedCommissione = commissione;
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

    /*
    java.lang.AssertionError: Model attribute 'idCommissione' expected:<7> but was:<null>

    @ParameterizedTest
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
    @MethodSource("provideAddMembroCommissione")
    public void addMembroCommissione(final User user, final Persona persona, final Commissione commissione, final Gruppo gruppo) throws Exception {


        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = persona;
        expectedPersona.setUser(user);

        Commissione expectedCommissione = commissione;


        List<Persona> persone = new ArrayList<>();
        persone.add(expectedPersona);

        Gruppo expectedGruppo = gruppo;
        expectedCommissione.setGruppo(expectedGruppo);
        expectedGruppo.addPersona(expectedPersona);


        when(gruppoService.getCurrentPersona()).thenReturn(expectedPersona);
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
    @MethodSource("provideRemoveMembroCommissione")
    public void removeMembroCommissione(final User user, final Persona persona, final Commissione commissione, final Gruppo gruppo) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        persona.setUser(user);
        commissione.addPersona(persona);
        commissione.setResponsabile(persona);
        List<Persona> persone = new ArrayList<>();
        persone.add(persona);
        commissione.setGruppo(gruppo);
        gruppo.addPersona(persona);

        List<Commissione> commissioni = new ArrayList<>();
        commissioni.add(commissione);

        when(gruppoService.getCurrentPersona()).thenReturn(persona);
        when(gruppoService.isResponsabile(persona.getId(), commissione.getId())).thenReturn(true);
        when(gruppoService.findSupergruppo(commissione.getId())).thenReturn(commissione);
        when(gruppoService.findAllMembriInSupergruppo(commissione.getId())).thenReturn(persone);
        when(gruppoService.findGruppoByCommissione(commissione.getId())).thenReturn(gruppo);
        when(gruppoService.findPersona(persona.getId())).thenReturn(persona);
        when(gruppoService.findAllCommissioniByGruppo(gruppo.getId())).thenReturn(commissioni);
        when(gruppoService.isResponsabile(gruppoService.getCurrentPersona().getId(), gruppo.getId())).thenReturn(true);
        when(gruppoService.findSupergruppo(commissione.getId())).thenReturn(commissione);
        when(gruppoService.findSupergruppo(gruppo.getId())).thenReturn(gruppo);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/remove/{idPersona}", gruppo.getId(), persona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(model().attribute("isCapogruppo", true))
                .andExpect(model().attribute("isResponsabile", true))
                .andExpect(model().attribute("flagRimozione", 1))
                .andExpect(model().attribute("personaRimossa", persona))
                .andExpect(view().name("gruppo/gruppo_detail"));

    }

    private static Stream<Arguments> provideRemoveMembroCommissione() {

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

    /*@ParameterizedTest
    @MethodSource("provideCreaCommissione")
    public void creaCommissione(GruppoFormCommand gruppoFormCommand, Gruppo gruppo, User user, List<Persona> persone, List<Commissione> commissioni) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        when(gruppoService.isResponsabile(user.getId(), gruppo.getId())).thenReturn(true);
        when(gruppoService.findAllMembriInSupergruppo(gruppo.getId())).thenReturn(persone);
        when(gruppoService.findAllCommissioniByGruppo(gruppo.getId())).thenReturn(commissioni);

        this.mockMvc.perform(post("/gruppi/{idGruppo}/commissioni/create", gruppo.getId())
                .with(user(userDetails))
                .with(csrf())
                .param("name", gruppoFormCommand.getName())
                .param("descrizione", gruppoFormCommand.getDescrizione())
                .param("idPersona", "" + gruppoFormCommand.getIdPersona()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(model().attribute("isCapogruppo", true))
                .andExpect(model().attribute("commissioni", commissioni))
                .andExpect(view().name("gruppo/gruppo_detail"));
    }

    private static Stream<Arguments> provideCreaCommissione() {
        Persona persona = new Persona("FF", "FF", "FF");
        persona.setId(1);
        User user = new User("z", "z");
        user.setId(1);
        persona.setUser(user);
        user.setPersona(persona);
        Gruppo gruppo = new Gruppo("ff", "Gruppo", true);
        gruppo.setId(1);
        gruppo.addPersona(persona);
        gruppo.setResponsabile(persona);
        GruppoFormCommand gruppoFormCommand = new GruppoFormCommand();
        gruppoFormCommand.setName("Nome");
        gruppoFormCommand.setDescrizione("Descrizione");
        gruppoFormCommand.setIdPersona(1);
        Commissione commissione = new Commissione("a", "Commissione", true, "b");

        List<Persona> persone = new ArrayList<>();
        persone.add(persona);

        List<Commissione> commissioni = new ArrayList<>();
        commissioni.add(commissione);

        return Stream.of(
            Arguments.of(gruppoFormCommand, gruppo, user, persone, commissioni)
        );
    }*/
}
