package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.DipartimentoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GruppoServiceImplIT {

    @Autowired
    private GruppoServiceImpl gruppoService;

    @Autowired
    private PersonaDAO personaDAO;

    @Autowired
    private SupergruppoDAO supergruppoDAO;

    @Autowired
    private ConsiglioDidatticoDAO consiglioDidatticoDAO;

    @Autowired
    private DipartimentoDAO dipartimentoDAO;

    private static Stream<Arguments> provideGetAssegnatarioTask() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "gruppo", true);
        Supergruppo supergruppo2 = new Supergruppo("GAQR Informatica", "gruppo", true);
        return Stream.of(
                Arguments.of(persona1, supergruppo),
                Arguments.of(persona2, supergruppo2)
        );
    }

    private static Stream<Arguments> providefindAllMembriInDipartimento() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        Dipartimento dipartimento = new Dipartimento("Informatica");
        Dipartimento dipartimento2 = new Dipartimento("Informatica2");
        return Stream.of(
                Arguments.of(persona1, dipartimento),
                Arguments.of(persona2, dipartimento2)
        );
    }

    private static Stream<Arguments> providefindAllSupergruppiOfPersona() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "Supergruppo", true);
        Supergruppo supergruppo1 = new Supergruppo("GAQR Informatica", "Supergruppo", true);
        return Stream.of(
                Arguments.of(persona1, supergruppo),
                Arguments.of(persona2, supergruppo1)
        );
    }

    private static Stream<Arguments> providefindAllConsigliDidatticiOfPersona() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        ConsiglioDidattico consiglioDidattico = new ConsiglioDidattico("Informatica");
        ConsiglioDidattico consiglioDidattico2 = new ConsiglioDidattico("Ingegneria");
        return Stream.of(
                Arguments.of(persona1, consiglioDidattico),
                Arguments.of(persona2, consiglioDidattico2)
        );
    }

    private static Stream<Arguments> providefindAllDipartimentiOfPersona() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        Dipartimento dipartimento = new Dipartimento("Informatica");
        Dipartimento dipartimento2 = new Dipartimento("Ingegneria");
        return Stream.of(
                Arguments.of(persona1, dipartimento),
                Arguments.of(persona2, dipartimento2)
        );
    }

    private static Stream<Arguments> providefindAllMembriInConsiglioDidatticoNoSupergruppo() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "Supergruppo", true);
        ConsiglioDidattico consiglioDidattico = new ConsiglioDidattico("Informatica");
        Persona persona3 = new Persona("Persona3", "Persona3", "Persona");
        Persona persona4 = new Persona("Persona4", "Persona4", "Persona");
        Supergruppo supergruppo2 = new Supergruppo("GAQR Informatica", "Supergruppo", true);
        ConsiglioDidattico consiglioDidattico2 = new ConsiglioDidattico("Informatica2");
        return Stream.of(
                Arguments.of(persona1, persona2, consiglioDidattico, supergruppo),
                Arguments.of(persona3, persona4, consiglioDidattico2, supergruppo2)
        );
    }

    private static Stream<Arguments> providefindPersona() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        return Stream.of(
                Arguments.of(persona1),
                Arguments.of(persona2)
        );
    }

    private static Stream<Arguments> providefindSupergruppo() {
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "Supergruppo", true);
        Supergruppo expectedSupergruppo2 = new Supergruppo("GAQR- Informatica", "Supergruppo", true);
        return Stream.of(
                Arguments.of(expectedSupergruppo),
                Arguments.of(expectedSupergruppo2)
        );
    }

    private static Stream<Arguments> providefindConsiglioBySupergruppo() {
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        ConsiglioDidattico expectedConsiglio = new ConsiglioDidattico("Informatica");
        Supergruppo expectedSupergruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        ConsiglioDidattico expectedConsiglio2 = new ConsiglioDidattico("Informatica2");
        return Stream.of(
                Arguments.of(expectedSupergruppo, expectedConsiglio),
                Arguments.of(expectedSupergruppo2, expectedConsiglio2)
        );
    }

    private static Stream<Arguments> provideisResponsabile() {
        Persona expectedPersona = new Persona("Mario", "Rossi", "ciao");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD-Informatica", "gruppo", true);
        Persona expectedPersona2 = new Persona("Luca", "Verdi", "ciao");
        Supergruppo expectedSupergruppo2 = new Supergruppo("GAQR-Informatica", "gruppo", true);
        return Stream.of(
                Arguments.of(expectedPersona, expectedSupergruppo),
                Arguments.of(expectedPersona2, expectedSupergruppo2)
        );
    }

    private static Stream<Arguments> providefindAllCommissioniByGruppo() {
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        return Stream.of(
                Arguments.of(expectedGruppo, expectedCommissione)
        );
    }

    private static Stream<Arguments> providefindAllMembriInGruppoNoCommissione() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        Persona persona3 = new Persona("Persona3", "Persona3", "Persona");
        Persona persona4 = new Persona("Persona4", "Persona4", "Persona");
        Commissione expectedCommissione2 = new Commissione("Commissione2", "Commissione", true, "Commissione");
        Gruppo expectedGruppo2 = new Gruppo("Gruppo2", "Gruppo", true);
        return Stream.of(
                Arguments.of(persona1, persona2, expectedCommissione, expectedGruppo),
                Arguments.of(persona3, persona4, expectedCommissione2, expectedGruppo2)
        );
    }

    private static Stream<Arguments> providecloseCommissione() {
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Commissione expectedCommissione2 = new Commissione("Commissione2", "Commissione", true, "Commissione2");
        return Stream.of(
                Arguments.of(expectedCommissione),
                Arguments.of(expectedCommissione2)
        );
    }

    private static Stream<Arguments> providecreateCommissione() {
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Commissione expectedCommissione2 = new Commissione("Commissione2", "Commissione", true, "Commissione2");
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        Gruppo expectedGruppo2 = new Gruppo("Gruppo2", "Gruppo", true);
        return Stream.of(
                Arguments.of(expectedCommissione, expectedGruppo),
                Arguments.of(expectedCommissione2, expectedGruppo2)
        );
    }

    private static Stream<Arguments> providenominaResponsabile() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Commissione actualCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        Commissione expectedCommissione2 = new Commissione("Commissione2", "Commissione", true, "Commissione");
        Commissione actualCommissione2 = new Commissione("Commissione2", "Commissione", true, "Commissione");
        return Stream.of(
                Arguments.of(persona1, expectedCommissione, actualCommissione),
                Arguments.of(persona2, expectedCommissione2, actualCommissione2)
        );
    }

    private static Stream<Arguments> providefindGruppoByCommissione() {
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Gruppo expectedGruppo2 = new Gruppo("Gruppo2", "Gruppo", true);
        Commissione expectedCommissione2 = new Commissione("Commissione2", "Commissione", true, "Commissione");
        return Stream.of(
                Arguments.of(expectedGruppo, expectedCommissione),
                Arguments.of(expectedGruppo2, expectedCommissione2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetAssegnatarioTask")
    void getAssegnatarioTask(Persona persona, Supergruppo supergruppo) {
        supergruppo.addPersona(persona);
        personaDAO.save(persona);
        supergruppoDAO.save(supergruppo);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona);
        List<Persona> actualPersone = gruppoService.findAllMembriInSupergruppo(supergruppo.getId());
        assertEquals(expectedPersone, actualPersone);
    }

    @ParameterizedTest
    @MethodSource("providefindAllMembriInDipartimento")
    void findAllMembriInDipartimento(Persona persona, Dipartimento dipartimento) {
        dipartimento.addPersona(persona);
        personaDAO.save(persona);
        dipartimentoDAO.save(dipartimento);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona);
        List<Persona> actualPersone = gruppoService.findAllMembriInDipartimento(dipartimento.getId());
        assertEquals(expectedPersone, actualPersone);
    }

    @ParameterizedTest
    @MethodSource("providefindAllSupergruppiOfPersona")
    void findAllSupergruppiOfPersona(Persona persona1, Supergruppo supergruppo) {
        supergruppo.addPersona(persona1);
        personaDAO.save(persona1);
        supergruppoDAO.save(supergruppo);
        List<Supergruppo> expectedSupergruppi = new ArrayList<>();
        expectedSupergruppi.add(supergruppo);
        List<Supergruppo> actualSupergruppi = gruppoService.findAllSupergruppiOfPersona(persona1.getId());
        assertEquals(expectedSupergruppi, actualSupergruppi);
    }

    @ParameterizedTest
    @MethodSource("providefindAllConsigliDidatticiOfPersona")
    void findAllConsigliDidatticiOfPersona(Persona persona1, ConsiglioDidattico consiglioDidattico) {
        consiglioDidattico.addPersona(persona1);
        personaDAO.save(persona1);
        consiglioDidatticoDAO.save(consiglioDidattico);
        List<ConsiglioDidattico> expectedConsigli = new ArrayList<>();
        expectedConsigli.add(consiglioDidattico);
        List<ConsiglioDidattico> actualConsigli = gruppoService.findAllConsigliDidatticiOfPersona(persona1.getId());
        assertEquals(expectedConsigli, actualConsigli);
    }

    @ParameterizedTest
    @MethodSource("providefindAllDipartimentiOfPersona")
    void findAllDipartimentiOfPersona(Persona persona1, Dipartimento dipartimento) {
        dipartimento.addPersona(persona1);
        personaDAO.save(persona1);
        dipartimentoDAO.save(dipartimento);
        List<Dipartimento> expectedDipartimenti = new ArrayList<>();
        expectedDipartimenti.add(dipartimento);
        List<Dipartimento> actualDipartimenti = gruppoService.findAllDipartimentiOfPersona(persona1.getId());
        assertEquals(expectedDipartimenti, actualDipartimenti);
    }

    @ParameterizedTest
    @MethodSource("providefindAllMembriInConsiglioDidatticoNoSupergruppo")
    void findAllMembriInConsiglioDidatticoNoSupergruppo(Persona persona1, Persona persona2, ConsiglioDidattico consiglioDidattico, Supergruppo supergruppo) {
        consiglioDidattico.addPersona(persona1);
        consiglioDidattico.addPersona(persona2);
        supergruppo.setConsiglio(consiglioDidattico);
        consiglioDidattico.setSupergruppo(supergruppo);
        supergruppo.addPersona(persona1);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona2);
        personaDAO.save(persona1);
        personaDAO.save(persona2);
        consiglioDidatticoDAO.save(consiglioDidattico);
        supergruppoDAO.save(supergruppo);
        List<Persona> actualPersone = gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(supergruppo.getId());
        assertEquals(actualPersone, expectedPersone);
    }

    @ParameterizedTest
    @MethodSource("providefindPersona")
    void findPersona(Persona expectedPersona) {
        personaDAO.save(expectedPersona);
        Persona actualPersona = gruppoService.findPersona(expectedPersona.getId());
        assertEquals(expectedPersona, actualPersona);
    }

    @ParameterizedTest
    @MethodSource("providefindSupergruppo")
    void findSupergruppo(Supergruppo expectedSupergruppo) {
        supergruppoDAO.save(expectedSupergruppo);
        Supergruppo actualSupergruppo = gruppoService.findSupergruppo(expectedSupergruppo.getId());
        assertEquals(expectedSupergruppo, actualSupergruppo);
    }

    @ParameterizedTest
    @MethodSource("providefindConsiglioBySupergruppo")
    void findConsiglioBySupergruppo(Supergruppo expectedSupergruppo, ConsiglioDidattico expectedConsiglio) {
        expectedSupergruppo.setConsiglio(expectedConsiglio);
        expectedConsiglio.setSupergruppo(expectedSupergruppo);
        consiglioDidatticoDAO.save(expectedConsiglio);
        supergruppoDAO.save(expectedSupergruppo);
        ConsiglioDidattico actualConsiglio = gruppoService.findConsiglioBySupergruppo(expectedSupergruppo.getId());
        assertEquals(actualConsiglio, expectedConsiglio);
    }

    @ParameterizedTest
    @MethodSource("provideisResponsabile")
    void isResponsabile(Persona expectedPersona, Supergruppo expectedSupergruppo) {
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        boolean expectedValue = gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId());
        assertTrue(expectedValue);
    }

    @ParameterizedTest
    @MethodSource("providefindAllCommissioniByGruppo")
    void findAllCommissioniByGruppo(Gruppo expectedGruppo, Commissione expectedCommissione) {
        List<Commissione> expectedCommissioni = new ArrayList<>();
        expectedCommissioni.add(expectedCommissione);
        expectedGruppo.addCommissione(expectedCommissione);
        supergruppoDAO.save(expectedGruppo);
        supergruppoDAO.save(expectedCommissione);
        List<Commissione> actualCommissioni = gruppoService.findAllCommissioniByGruppo(expectedGruppo.getId());
        assertEquals(actualCommissioni, expectedCommissioni);
    }

    @ParameterizedTest
    @MethodSource("providefindAllMembriInGruppoNoCommissione")
    void findAllMembriInGruppoNoCommissione(Persona persona1, Persona persona2, Commissione expectedCommissione, Gruppo expectedGruppo) {
        expectedGruppo.addPersona(persona1);
        expectedGruppo.addPersona(persona2);
        expectedCommissione.addPersona(persona1);
        expectedGruppo.addCommissione(expectedCommissione);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona2);
        personaDAO.save(persona1);
        personaDAO.save(persona2);
        supergruppoDAO.save(expectedGruppo);
        supergruppoDAO.save(expectedCommissione);
        List<Persona> acutalPersone = gruppoService.findAllMembriInGruppoNoCommissione(expectedCommissione.getId());
        assertEquals(expectedPersone, acutalPersone);
    }

    @ParameterizedTest
    @MethodSource("providecloseCommissione")
    void closeCommissione(Commissione expectedCommissione) {
        supergruppoDAO.save(expectedCommissione);
        gruppoService.closeCommissione(expectedCommissione.getId());
        Commissione actualCommissione = (Commissione) supergruppoDAO.findById(expectedCommissione.getId()).orElse(null);
        assert actualCommissione != null;
        assertEquals(actualCommissione.getState(), false);
    }

    @ParameterizedTest
    @MethodSource("providecreateCommissione")
    void createCommissione(Commissione expectedCommissione, Gruppo expectedGruppo) {
        supergruppoDAO.save(expectedGruppo);
        gruppoService.createCommissione(expectedCommissione, expectedGruppo.getId());
        assertTrue(gruppoService.findAllCommissioniByGruppo(expectedGruppo.getId()).contains(expectedCommissione));
    }

    @ParameterizedTest
    @MethodSource("providenominaResponsabile")
    void nominaResponsabile(Persona persona1, Commissione expectedCommissione, Commissione actualCommissione) {
        personaDAO.save(persona1);
        supergruppoDAO.save(expectedCommissione);
        gruppoService.nominaResponsabile(persona1.getId(), expectedCommissione.getId());
        actualCommissione.addPersona(persona1);
        actualCommissione.setResponsabile(persona1);
        assertEquals(persona1, gruppoService.findSupergruppo(expectedCommissione.getId()).getResponsabile());
    }

    @ParameterizedTest
    @MethodSource("providefindGruppoByCommissione")
    void findGruppoByCommissione(Gruppo expectedGruppo, Commissione expectedCommissione) {
        expectedGruppo.addCommissione(expectedCommissione);
        supergruppoDAO.save(expectedGruppo);
        supergruppoDAO.save(expectedCommissione);
        Gruppo actualGruppo = gruppoService.findGruppoByCommissione(expectedCommissione.getId());
        assertEquals(expectedGruppo, actualGruppo);
    }

}
