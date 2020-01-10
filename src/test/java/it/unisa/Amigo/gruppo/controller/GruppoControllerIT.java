package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domain.User;
import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.Commissione;
import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Gruppo;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
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
    private UserDAO userDAO;


    @ParameterizedTest
    @MethodSource("provideFindAllMembriInSupergruppi")
    public void findAllMembriInSupergruppo(final User userArg, final Persona personaArg, final Gruppo gruppo, final ConsiglioDidattico consiglioDidattico) throws Exception {

        User user = userArg;
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = personaArg;
        expectedPersona.setUser(user);
        Gruppo expectedGruppo = gruppo;
        ConsiglioDidattico expectedConsiglioDidattico = consiglioDidattico;
        expectedGruppo.addPersona(expectedPersona);
        expectedGruppo.setResponsabile(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);

        personaDAO.save(expectedPersona);
        userDAO.save(user);

        consiglioDidatticoDAO.save(expectedConsiglioDidattico);
        supergruppoDAO.save(expectedGruppo);

        this.mockMvc.perform(get("/gruppi/{id}", expectedGruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata", expectedPersona.getId()))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(model().attribute("supergruppo", expectedGruppo))
                .andExpect(model().attribute("isCapogruppo", gruppoService.isResponsabile(expectedPersona.getId(), expectedGruppo.getId())))
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
        Gruppo expectedSupergruppo1 = gruppo1;
        Gruppo expectedSupergruppo2 = gruppo2;
        List<Gruppo> expectedSupergruppi = new ArrayList<>();
        expectedSupergruppi.add(expectedSupergruppo2);
        expectedSupergruppi.add(expectedSupergruppo1);
        expectedPersona.addSupergruppo(expectedSupergruppo2);
        expectedPersona.addSupergruppo(expectedSupergruppo1);

        supergruppoDAO.save(expectedSupergruppo1);
        supergruppoDAO.save(expectedSupergruppo2);
        personaDAO.save(expectedPersona);
        userDAO.save(user);


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

        return Stream.of(
                Arguments.of(user, persona1, gruppo1, gruppo2),
                Arguments.of(user1, persona2, gruppo4, gruppo3)
        );
    }


    @ParameterizedTest
    @MethodSource("provideGroupCandidatesList")
    public void groupCandidatesList(final Persona persona1, final Persona persona2, final User user1, final User user2) throws Exception {

        UserDetailImpl userDetails1 = new UserDetailImpl(user1);

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

        personaDAO.save(expectedPersona1);
        personaDAO.save(expectedPersona2);
        userDAO.save(user1);
        userDAO.save(user2);
        consiglioDidatticoDAO.save(expectedConsiglioDidattico);
        supergruppoDAO.save(expectedSupergruppo);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/candidati", expectedSupergruppo.getId())
                .with(user(userDetails1)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata", expectedPersona1.getId()))
                .andExpect(model().attribute("supergruppo", expectedSupergruppo))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(view().name("gruppo/aggiunta_membro"));
    }

    private static Stream<Arguments> provideGroupCandidatesList() {
        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");
        User user3 = new User("admin3", "admin3");
        User user4 = new User("admin4", "admin4");

        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");
        Persona persona3 = new Persona("persona3", "persona3", "persona");
        Persona persona4 = new Persona("persona4", "persona4", "persona");

        return Stream.of(
                Arguments.of(persona1, persona2, user1, user),
                Arguments.of(persona3, persona4, user3, user4)
        );
    }

    @ParameterizedTest
    @MethodSource("provideAddMembroCommissione")
    public void addMembroCommissione(final User user, final Persona persona, final Commissione commissione, final Gruppo gruppo) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = persona;
        expectedPersona.setUser(user);

        Commissione expectedCommissione = commissione;
        Gruppo expectedGruppo = gruppo;
        expectedGruppo.addPersona(expectedPersona);
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(persona);
        expectedGruppo.addCommissione(expectedCommissione);

        personaDAO.save(expectedPersona);
        userDAO.save(user);
        supergruppoDAO.save(expectedGruppo);
        supergruppoDAO.save(expectedCommissione);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}", expectedCommissione.getId(), expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
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

        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );

    }

    @ParameterizedTest
    @MethodSource("provideAddMembroGruppo")
    public void addMembroGruppo(final User user, final Persona persona, final Commissione commissione, final Gruppo gruppo) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = persona;
        expectedPersona.setUser(user);

        ConsiglioDidattico expectedConsiglioDidattico = new ConsiglioDidattico("Informatica");
        expectedConsiglioDidattico.addPersona(expectedPersona);

        Commissione expectedCommissione = commissione;
        Gruppo expectedGruppo = gruppo;
        expectedGruppo.setConsiglio(expectedConsiglioDidattico);
        expectedGruppo.addPersona(expectedPersona);
        expectedGruppo.setResponsabile(expectedPersona);
        expectedCommissione.addPersona(expectedPersona);
        expectedGruppo.addCommissione(expectedCommissione);

        personaDAO.save(expectedPersona);
        userDAO.save(user);
        supergruppoDAO.save(expectedGruppo);
        supergruppoDAO.save(expectedCommissione);


        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}", expectedGruppo.getId(), expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("gruppo/aggiunta_membro"));

    }

    private static Stream<Arguments> provideAddMembroGruppo() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");

        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");

        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);

        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione2", true, "");

        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );

    }


    @ParameterizedTest
    @MethodSource("provideRemoveMembroCommissione")
    public void removeMembroCommissione(final User user, final Persona persona, final Commissione commissione, final Gruppo gruppo) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = persona;
        expectedPersona.setUser(user);
        Commissione expectedCommissione = commissione;
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppo(expectedCommissione);
        Gruppo expectedGruppo = gruppo;
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

    private static Stream<Arguments> provideRemoveMembroCommissione() {

        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");

        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");

        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);

        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione2", true, "");

        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRemoveMembroSupergruppo")
    public void removeMembroGruppo(final User user, final Persona persona, final Commissione commissione, final Gruppo gruppo) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = persona;
        expectedPersona.setUser(user);
        Commissione expectedCommissione = commissione;
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppo(expectedCommissione);
        Gruppo expectedGruppo = gruppo;
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

    private static Stream<Arguments> provideRemoveMembroSupergruppo() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");

        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");

        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);

        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione2", true, "");

        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideAllMembriInCommissione")
    public void findAllMembriInCommissione(final User user, final Persona persona, final Commissione commissione, final Gruppo gruppo) throws Exception {


        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = persona;
        expectedPersona.setUser(user);
        Commissione expectedCommissione = commissione;
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppo(expectedCommissione);
        Gruppo expectedGruppo = gruppo;
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

    private static Stream<Arguments> provideAllMembriInCommissione() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");

        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");

        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);

        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione2", true, "");

        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );
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
        expectedPersona.addSupergruppo(expectedCommissione);
        Gruppo expectedGruppo = gruppo;
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

    private static Stream<Arguments> provideCloseCommissione() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");

        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");

        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);

        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione2", true, "");

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

        return Stream.of(
                Arguments.of(user, persona1, commissione1),
                Arguments.of(user1, persona2, commissione2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideNominaResponsabile")
    public void nominaResponsabile(final User user, final Persona persona, final Commissione commissione, final Gruppo gruppo) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = persona;
        expectedPersona.setUser(user);
        Commissione expectedCommissione = commissione;
        expectedCommissione.addPersona(expectedPersona);
        expectedCommissione.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppo(expectedCommissione);
        Gruppo expectedGruppo = gruppo;
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

    private static Stream<Arguments> provideNominaResponsabile() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");

        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");

        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);

        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione2", true, "");

        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );
    }


}
