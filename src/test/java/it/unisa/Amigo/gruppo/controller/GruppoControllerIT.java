package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domain.User;
import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.*;
import it.unisa.Amigo.gruppo.services.GruppoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @AfterEach
    void afterEach() {
        supergruppoDAO.deleteAll();
        consiglioDidatticoDAO.deleteAll();
        personaDAO.deleteAll();
        userDAO.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideFindAllMembriInSupergruppi")
    public void findAllMembriInSupergruppo(final User userArg, final Persona personaArg, final Gruppo gruppo, final ConsiglioDidattico consiglioDidattico) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(userArg);
        personaArg.setUser(userArg);
        userArg.setPersona(personaArg);
        gruppo.addPersona(personaArg);
        gruppo.setResponsabile(personaArg);
        gruppo.setConsiglio(consiglioDidattico);
        consiglioDidattico.addPersona(personaArg);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(personaArg);
        personaDAO.save(personaArg);
        userDAO.save(userArg);
        supergruppoDAO.save(gruppo);
        consiglioDidatticoDAO.save(consiglioDidattico);

        System.out.println(gruppo.getId());
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

        return Stream.of(
                Arguments.of(user, persona1, gruppo1, consiglio1),
                Arguments.of(user1, persona2, gruppo2, consiglio2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideAllSupergruppi")
    public void findAllSupergruppi(final User userArg, final Persona personaArg, final Gruppo gruppo1, final Gruppo gruppo2) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(userArg);
        userArg.setPersona(personaArg);
        personaArg.setUser(userArg);
        personaArg.addSupergruppo(gruppo2);
        personaArg.addSupergruppo(gruppo1);

        supergruppoDAO.save(gruppo1);
        supergruppoDAO.save(gruppo2);
        personaDAO.save(personaArg);
        userDAO.save(userArg);


        this.mockMvc.perform(get("/gruppi")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata", personaArg.getId()))
                .andExpect(model().attribute("supergruppi", Arrays.asList(gruppo1, gruppo2)))
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

        persona1.setUser(user1);
        persona2.setUser(user2);
        user1.setPersona(persona1);
        user2.setPersona(persona2);

        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona1);

        ConsiglioDidattico expectedConsiglioDidattico = new ConsiglioDidattico("Informatica");
        expectedConsiglioDidattico.addPersona(persona1);
        expectedConsiglioDidattico.addPersona(persona2);

        Supergruppo expectedSupergruppo = new Supergruppo("GAQR - Informatica", "Supergruppo", true);
        expectedSupergruppo.addPersona(persona2);
        expectedSupergruppo.setConsiglio(expectedConsiglioDidattico);

        personaDAO.save(persona1);
        personaDAO.save(persona2);
        userDAO.save(user1);
        userDAO.save(user2);
        consiglioDidatticoDAO.save(expectedConsiglioDidattico);
        supergruppoDAO.save(expectedSupergruppo);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/candidati", expectedSupergruppo.getId())
                .with(user(userDetails1)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personaLoggata", persona1.getId()))
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
        persona.setUser(user);
        user.setPersona(persona);

        gruppo.addPersona(persona);
        commissione.addPersona(persona);
        commissione.setResponsabile(persona);
        gruppo.addCommissione(commissione);

        personaDAO.save(persona);
        userDAO.save(user);
        supergruppoDAO.save(gruppo);
        supergruppoDAO.save(commissione);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}", commissione.getId(), persona.getId())
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
        persona.setUser(user);
        user.setPersona(persona);

        ConsiglioDidattico expectedConsiglioDidattico = new ConsiglioDidattico("Informatica");
        expectedConsiglioDidattico.addPersona(persona);

        gruppo.setConsiglio(expectedConsiglioDidattico);
        gruppo.addPersona(persona);
        gruppo.setResponsabile(persona);
        commissione.addPersona(persona);
        gruppo.addCommissione(commissione);

        personaDAO.save(persona);
        userDAO.save(user);
        supergruppoDAO.save(gruppo);
        supergruppoDAO.save(commissione);


        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/add/{idPersona}", gruppo.getId(), persona.getId())
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
        persona.setUser(user);
        user.setPersona(persona);
        commissione.addPersona(persona);
        commissione.setResponsabile(persona);
        persona.addSupergruppo(commissione);
        gruppo.addCommissione(commissione);
        gruppo.addPersona(persona);
        gruppo.setResponsabile(persona);


        personaDAO.save(persona);
        userDAO.save(user);
        supergruppoDAO.save(gruppo);
        supergruppoDAO.save(commissione);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/remove/{idPersona}", commissione.getId(), persona.getId())
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
        user.setPersona(persona);
        persona.setUser(user);
        // persona.setUser(user);
        //commissione.addPersona(persona);
        //commissione.setResponsabile(persona);
        //persona.addSupergruppo(commissione);
        //gruppo.addCommissione(commissione);
        //gruppo.addPersona(persona);
        //gruppo.setResponsabile(persona);

        personaDAO.save(persona);
        userDAO.save(user);
        supergruppoDAO.save(gruppo);
        supergruppoDAO.save(commissione);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/remove/{idPersona}", gruppo.getId(), persona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(view().name("gruppo/gruppo_detail"));
    }

    private static Stream<Arguments> provideRemoveMembroSupergruppo() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");

        User user = new User("admin3", "admin");
        User user1 = new User("admin1", "admin1");

        persona1.setUser(user);
        persona2.setUser(user1);

        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);

        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione2", true, "");

        commissione1.addPersona(persona1);
        commissione1.setResponsabile(persona1);
        commissione2.addPersona(persona2);
        commissione2.setResponsabile(persona2);

        gruppo1.addPersona(persona1);
        gruppo1.setResponsabile(persona1);
        gruppo1.addCommissione(commissione1);
        gruppo2.addPersona(persona2);
        gruppo2.setResponsabile(persona2);
        gruppo2.addCommissione(commissione2);

        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideAllMembriInCommissione")
    public void findAllMembriInCommissione(final User user, final Persona persona, final Commissione commissione, final Gruppo gruppo) throws Exception {


        UserDetailImpl userDetails = new UserDetailImpl(user);
        persona.setUser(user);
        user.setPersona(persona);
        commissione.addPersona(persona);
        commissione.setResponsabile(persona);
        persona.addSupergruppo(commissione);
        gruppo.addCommissione(commissione);
        gruppo.addPersona(persona);
        gruppo.setResponsabile(persona);

        commissione.addPersona(persona);
        commissione.setResponsabile(persona);
        gruppo.addCommissione(commissione);

        List<Persona> persone = new ArrayList<>();
        persone.add(persona);


        personaDAO.save(persona);
        userDAO.save(user);
        supergruppoDAO.save(gruppo);
        supergruppoDAO.save(commissione);

        this.mockMvc.perform(get("/gruppi/{id}/commissione_detail/{id_commissione}", gruppo.getId(), commissione.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isCapogruppo", true))
                .andExpect(model().attribute("isResponsabile", true))
                .andExpect(model().attribute("persone", persone))
                .andExpect(model().attribute("supergruppo", commissione))
                .andExpect(model().attribute("personaLoggata", persona.getId()))
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
        Commissione commissione2 = new Commissione("Commissione2", "Commissione", true, "");

        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCloseCommissione")
    public void closeCommissione(final User user, final Persona persona, final Commissione commissione, final Gruppo gruppo) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(user);
        persona.setUser(user);
        user.setPersona(persona);
        commissione.addPersona(persona);
        commissione.setResponsabile(persona);
        persona.addSupergruppo(commissione);
        gruppo.addCommissione(commissione);
        gruppo.addPersona(persona);
        gruppo.setResponsabile(persona);

        commissione.addPersona(persona);
        commissione.setResponsabile(persona);
        gruppo.addCommissione(commissione);

        List<Persona> persone = new ArrayList<>();
        persone.add(persona);


        personaDAO.save(persona);
        userDAO.save(user);
        supergruppoDAO.save(gruppo);
        supergruppoDAO.save(commissione);
        commissione.setState(false);

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

    private static Stream<Arguments> provideCloseCommissione() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");

        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");

        Gruppo gruppo1 = new Gruppo("GAQD- Informatica", "Gruppo", true);
        Gruppo gruppo2 = new Gruppo("GAQR- Informatica", "Gruppo", true);

        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        Commissione commissione2 = new Commissione("Commissione2", "Commissione", true, "");

        return Stream.of(
                Arguments.of(user1, persona2, commissione2, gruppo2),
                Arguments.of(user, persona1, commissione1, gruppo1)

        );
    }

    @ParameterizedTest
    @MethodSource("provideCreateCommissioneForm")
    public void createCommissioneForm(final User user, final Persona persona, final Commissione commissione) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        persona.setUser(user);
        user.setPersona(persona);

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

    private static Stream<Arguments> provideCreateCommissioneForm() {
        Persona persona1 = new Persona("persona1", "persona1", "persona");
        Persona persona2 = new Persona("persona2", "persona2", "persona");

        User user = new User("admin", "admin");
        User user1 = new User("admin1", "admin1");

        Commissione commissione1 = new Commissione("Commissione", "Commissione", true, "");
        commissione1.setId(1);
        Commissione commissione2 = new Commissione("Commissione2", "Commissione", true, "");
        commissione2.setId(2);

        return Stream.of(
                Arguments.of(user, persona1, commissione1),
                Arguments.of(user1, persona2, commissione2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideNominaResponsabile")
    public void nominaResponsabile(final User user, final Persona persona, final Commissione commissione, final Gruppo gruppo) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        persona.setUser(user);
        user.setPersona(persona);
        commissione.addPersona(persona);
        commissione.setResponsabile(persona);
        persona.addSupergruppo(commissione);
        gruppo.addCommissione(commissione);
        gruppo.addPersona(persona);
        gruppo.setResponsabile(persona);

        commissione.addPersona(persona);
        gruppo.addCommissione(commissione);

        List<Persona> persone = new ArrayList<>();
        persone.add(persona);


        personaDAO.save(persona);
        userDAO.save(user);
        supergruppoDAO.save(gruppo);
        supergruppoDAO.save(commissione);

        this.mockMvc.perform(get("/gruppi/commissioni/{idCommissione}/nominaResponsabile/{idPersona}", commissione.getId(), persona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("idCommissione", commissione.getId()))
                .andExpect(model().attribute("isCapogruppo", true))
                .andExpect(model().attribute("isResponsabile", true))
                .andExpect(model().attribute("persone", persone))
                .andExpect(model().attribute("supergruppo", commissione))
                .andExpect(model().attribute("personaLoggata", persona.getId()))
                .andExpect(model().attribute("flagNomina", 1))
                .andExpect(model().attribute("responsabile", persona))
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
        Commissione commissione2 = new Commissione("Commissione2", "Commissione", true, "");

        return Stream.of(
                Arguments.of(user, persona1, commissione1, gruppo1),
                Arguments.of(user1, persona2, commissione2, gruppo2)
        );
    }


}
