package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.DipartimentoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class GruppoServiceImplTest {

    @InjectMocks
    private GruppoServiceImpl gruppoService;

    @Mock
    private PersonaDAO personaDAO;

    @Mock
    private SupergruppoDAO supergruppoDAO;

    @Mock
    private ConsiglioDidatticoDAO consiglioDidatticoDAO;

    @Mock
    private DipartimentoDAO dipartimentoDAO;

    private static Stream<Arguments> providefindAllMembriInSupergruppo() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "gruppo", true);
        return Stream.of(
                Arguments.of(persona1, supergruppo),
                Arguments.of(persona2, supergruppo)
        );
    }

    private static Stream<Arguments> providefindAllMembriInConsiglioDidattico() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        ConsiglioDidattico consiglioDidattico = new ConsiglioDidattico("Consiglio Informatica");
        return Stream.of(
                Arguments.of(persona1, consiglioDidattico),
                Arguments.of(persona2, consiglioDidattico)
        );
    }

    private static Stream<Arguments> providefindAllMembriInDipartimento() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        Dipartimento dipartimento = new Dipartimento("Informatica");
        return Stream.of(
                Arguments.of(persona1, dipartimento),
                Arguments.of(persona2, dipartimento)
        );
    }

    private static Stream<Arguments> providefindAllSupergruppiOfPersona() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "gruppo", true);
        Supergruppo supergruppo1 = new Supergruppo("GAQR Informatica", "gruppo", true);
        return Stream.of(
                Arguments.of(persona1, supergruppo),
                Arguments.of(persona1, supergruppo1)
        );
    }

    private static Stream<Arguments> providefindAllConsigliDidatticiOfPersona() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        ConsiglioDidattico consiglioDidattico = new ConsiglioDidattico("Informatica");
        ConsiglioDidattico consiglioDidattico1 = new ConsiglioDidattico("Ingegneria");
        return Stream.of(
                Arguments.of(persona1, consiglioDidattico),
                Arguments.of(persona1, consiglioDidattico1)
        );
    }

    private static Stream<Arguments> providefindAllDipartimentiOfPersona() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Dipartimento dipartimento = new Dipartimento("Informatica");
        Dipartimento dipartimento1 = new Dipartimento("Ingegneria");
        return Stream.of(
                Arguments.of(persona1, dipartimento),
                Arguments.of(persona1, dipartimento1)
        );
    }

    private static Stream<Arguments> providefindAllMembriInConsiglioDidatticoNoSupergruppo() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        Persona persona3 = new Persona("Persona3", "Persona3", "Persona");
        ConsiglioDidattico consiglioDidattico = new ConsiglioDidattico("Informatica");
        Supergruppo supergruppo = new Supergruppo("GAQR Informatica", "gruppo", true);
        return Stream.of(
                Arguments.of(persona1, persona2, consiglioDidattico, supergruppo),
                Arguments.of(persona2, persona3, consiglioDidattico, supergruppo)
        );
    }

    private static Stream<Arguments> providefindPersona() {
        Persona expectedPersona = new Persona("Persona1", "Persona1", "Persona");
        Persona expectedPersona2 = new Persona("Persona2", "Persona2", "Persona");
        return Stream.of(
                Arguments.of(expectedPersona),
                Arguments.of(expectedPersona2)
        );
    }

    private static Stream<Arguments> providefindSupergruppo() {
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo expectedSupergruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        return Stream.of(
                Arguments.of(expectedSupergruppo),
                Arguments.of(expectedSupergruppo2)
        );
    }

    private static Stream<Arguments> provideaddMembro() {
        Persona expectedPersona = new Persona("Persona1", "Persona1", "Persona");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo expectedSupergruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        return Stream.of(
                Arguments.of(expectedPersona, expectedSupergruppo),
                Arguments.of(expectedPersona, expectedSupergruppo2)
        );
    }

    private static Stream<Arguments> provideremoveMembro() {
        Persona expectedPersona = new Persona("Persona1", "Persona1", "Persona");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo expectedSupergruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        return Stream.of(
                Arguments.of(expectedPersona, expectedSupergruppo),
                Arguments.of(expectedPersona, expectedSupergruppo2)
        );
    }

    private static Stream<Arguments> providefindConsiglioBySupergruppo() {
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo expectedSupergruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        ConsiglioDidattico expectedConsiglio = new ConsiglioDidattico("Informatica");
        return Stream.of(
                Arguments.of(expectedSupergruppo, expectedConsiglio),
                Arguments.of(expectedSupergruppo2, expectedConsiglio)
        );
    }

    private static Stream<Arguments> provideisResponsabile() {
        Persona expectedPersona = new Persona("Mario", "Rossi", "ciao");
        Persona expectedPersona2 = new Persona("Luca", "Verdi", "ciao");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD-Informatica", "gruppo", true);
        return Stream.of(
                Arguments.of(expectedPersona, expectedSupergruppo),
                Arguments.of(expectedPersona2, expectedSupergruppo)
        );
    }

    private static Stream<Arguments> providefindAllCommissioniByGruppo() {
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        Gruppo expectedGruppo2 = new Gruppo("Gruppo2", "Gruppo", true);
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Commissione expectedCommissione2 = new Commissione("Commissione2", "Commissione2", true, "Commissione2");
        return Stream.of(
                Arguments.of(expectedCommissione, expectedGruppo),
                Arguments.of(expectedCommissione2, expectedGruppo2)
        );
    }

    private static Stream<Arguments> providefindAllMembriInGruppoNoCommissione() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        Persona persona3 = new Persona("Persona3", "Persona3", "Persona");
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        Commissione expectedCommissione2 = new Commissione("Commissione2", "Commissione", true, "Commissione");
        Gruppo expectedGruppo2 = new Gruppo("Gruppo2", "Gruppo", true);
        return Stream.of(
                Arguments.of(persona1, persona2, expectedCommissione, expectedGruppo),
                Arguments.of(persona2, persona3, expectedCommissione2, expectedGruppo2)
        );
    }

    private static Stream<Arguments> providecloseCommissione() {
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Commissione expectedCommissione2 = new Commissione("Commissione2", "Commissione", true, "Commissione");
        return Stream.of(
                Arguments.of(expectedCommissione),
                Arguments.of(expectedCommissione2)
        );
    }

    private static Stream<Arguments> providecreateCommissione() {
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        Gruppo expectedGruppo2 = new Gruppo("Gruppo2", "Gruppo", true);
        Commissione actualCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");

        return Stream.of(
                Arguments.of(expectedCommissione, actualCommissione, expectedGruppo),
                Arguments.of(expectedCommissione, actualCommissione, expectedGruppo2)
        );
    }

    private static Stream<Arguments> providenominaResponsabile() {
        Persona persona = new Persona("Persona1", "Persona1", "Persona");
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Commissione actualCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        Persona persona2 = new Persona("Persona2", "Persona2", "Persona");
        Commissione expectedCommissione2 = new Commissione("Commissione2", "Commissione", true, "Commissione");
        Commissione actualCommissione2 = new Commissione("Commissione2", "Commissione", true, "Commissione");
        return Stream.of(
                Arguments.of(persona, expectedCommissione, actualCommissione),
                Arguments.of(persona2, expectedCommissione2, actualCommissione2)
        );
    }

    private static Stream<Arguments> providefindGruppoByCommissione() {
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        Gruppo actualGruppo = new Gruppo("Gruppo", "Gruppo", true);
        Gruppo expectedGruppo2 = new Gruppo("Gruppo2", "Gruppo", true);
        Gruppo actualGruppo2 = new Gruppo("Gruppo2", "Gruppo", true);
        return Stream.of(
                Arguments.of(expectedGruppo, actualGruppo),
                Arguments.of(expectedGruppo2, actualGruppo2)
        );
    }

    @ParameterizedTest
    @MethodSource("providefindAllMembriInSupergruppo")
    void findAllMembriInSupergruppo(Persona persona, Supergruppo supergruppo) {
        supergruppo.addPersona(persona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona);
        when(personaDAO.findBySupergruppi_id(supergruppo.getId())).thenReturn(expectedPersone);
        List<Persona> actualPersone = gruppoService.findAllMembriInSupergruppo(supergruppo.getId());
        assertEquals(expectedPersone, actualPersone);
    }

    @ParameterizedTest
    @MethodSource("providefindAllMembriInConsiglioDidattico")
    void findAllMembriInConsiglioDidattico(Persona persona, ConsiglioDidattico consiglioDidattico) {
        consiglioDidattico.addPersona(persona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona);
        when(personaDAO.findByConsigli_id(consiglioDidattico.getId())).thenReturn(expectedPersone);
        List<Persona> actualPersone = gruppoService.findAllMembriInConsiglioDidattico(consiglioDidattico.getId());
        assertEquals(expectedPersone, actualPersone);
    }

    @ParameterizedTest
    @MethodSource("providefindAllMembriInDipartimento")
    void findAllMembriInDipartimento(Persona persona, Dipartimento dipartimento) {
        dipartimento.addPersona(persona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona);
        when(personaDAO.findByDipartimenti_id(dipartimento.getId())).thenReturn(expectedPersone);
        List<Persona> actualPersone = gruppoService.findAllMembriInDipartimento(dipartimento.getId());
        assertEquals(expectedPersone, actualPersone);
    }

    @ParameterizedTest
    @MethodSource("providefindAllSupergruppiOfPersona")
    void findAllSupergruppiOfPersona(Persona persona, Supergruppo supergruppo) {
        supergruppo.addPersona(persona);
        List<Supergruppo> expectedSupergruppi = new ArrayList<>();
        expectedSupergruppi.add(supergruppo);
        when(supergruppoDAO.findAllByPersone_id(persona.getId())).thenReturn(expectedSupergruppi);
        List<Supergruppo> actualSupergruppi = gruppoService.findAllSupergruppiOfPersona(persona.getId());
        assertEquals(expectedSupergruppi, actualSupergruppi);
    }

    @ParameterizedTest
    @MethodSource("providefindAllConsigliDidatticiOfPersona")
    void findAllConsigliDidatticiOfPersona(Persona persona, ConsiglioDidattico consiglioDidattico) {
        consiglioDidattico.addPersona(persona);
        List<ConsiglioDidattico> expectedConsigli = new ArrayList<>();
        expectedConsigli.add(consiglioDidattico);
        when(consiglioDidatticoDAO.findAllByPersone_id(persona.getId())).thenReturn(expectedConsigli);
        List<ConsiglioDidattico> actualConsigli = gruppoService.findAllConsigliDidatticiOfPersona(persona.getId());
        assertEquals(expectedConsigli, actualConsigli);
    }

    @ParameterizedTest
    @MethodSource("providefindAllDipartimentiOfPersona")
    void findAllDipartimentiOfPersona(Persona persona, Dipartimento dipartimento) {
        dipartimento.addPersona(persona);
        List<Dipartimento> expectedDipartimenti = new ArrayList<>();
        expectedDipartimenti.add(dipartimento);
        when(dipartimentoDAO.findAllByPersone_id(persona.getId())).thenReturn(expectedDipartimenti);
        List<Dipartimento> actualDipartimenti = gruppoService.findAllDipartimentiOfPersona(persona.getId());
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
        when(supergruppoDAO.findById(supergruppo.getId())).thenReturn(Optional.of(supergruppo));
        List<Persona> actualPersone = gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(supergruppo.getId());
        assertEquals(actualPersone, expectedPersone);
    }

    @ParameterizedTest
    @MethodSource("providefindPersona")
    void findPersona(Persona expectedPersona) {
        when(personaDAO.findById(expectedPersona.getId())).thenReturn(Optional.of(expectedPersona));
        Persona actualPersona = gruppoService.findPersona(expectedPersona.getId());
        assertEquals(expectedPersona, actualPersona);
    }

    @ParameterizedTest
    @MethodSource("providefindSupergruppo")
    void findSupergruppo(Supergruppo expectedSupergruppo) {
        when(supergruppoDAO.findById(expectedSupergruppo.getId())).thenReturn(Optional.of(expectedSupergruppo));
        Supergruppo actualSupergruppo = gruppoService.findSupergruppo(expectedSupergruppo.getId());
        assertEquals(expectedSupergruppo, actualSupergruppo);
    }

    @ParameterizedTest
    @MethodSource("provideaddMembro")
    void addMembro(Persona expectedPersona, Supergruppo expectedSupergruppo) {
        int oldSize = expectedSupergruppo.getPersone().size();
        gruppoService.addMembro(expectedPersona, expectedSupergruppo);
        int actualSize = expectedSupergruppo.getPersone().size();
        assertEquals(oldSize + 1, actualSize);
    }

    @ParameterizedTest
    @MethodSource("provideremoveMembro")
    void removeMembro(Persona expectedPersona, Supergruppo expectedSupergruppo) {
        int oldSize = expectedSupergruppo.getPersone().size();
        gruppoService.addMembro(expectedPersona, expectedSupergruppo);
        int actualSize = expectedSupergruppo.getPersone().size();
        assertEquals(oldSize + 1, actualSize);
    }

    @ParameterizedTest
    @MethodSource("providefindConsiglioBySupergruppo")
    void findConsiglioBySupergruppo(Supergruppo expectedSupergruppo, ConsiglioDidattico expectedConsiglio) {
        expectedSupergruppo.setConsiglio(expectedConsiglio);
        expectedConsiglio.setSupergruppo(expectedSupergruppo);
        when(consiglioDidatticoDAO.findBySupergruppo_id(expectedSupergruppo.getId())).thenReturn(expectedConsiglio);
        ConsiglioDidattico actualConsiglio = gruppoService.findConsiglioBySupergruppo(expectedSupergruppo.getId());
        assertEquals(actualConsiglio, expectedConsiglio);
    }

    @ParameterizedTest
    @MethodSource("provideisResponsabile")
    void isResponsabile(Persona expectedPersona, Supergruppo expectedSupergruppo) {
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        when(supergruppoDAO.findById(expectedSupergruppo.getId())).thenReturn(Optional.of(expectedSupergruppo));
        boolean expectedValue = gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId());
        assertTrue(expectedValue);
    }

    @ParameterizedTest
    @MethodSource("providefindAllCommissioniByGruppo")
    void findAllCommissioniByGruppo(Commissione expectedCommissione, Gruppo expectedGruppo) {
        List<Commissione> expectedCommissioni = new ArrayList<>();
        expectedCommissioni.add(expectedCommissione);
        expectedGruppo.addCommissione(expectedCommissione);
        when(supergruppoDAO.findById(expectedGruppo.getId())).thenReturn(Optional.of(expectedGruppo));
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
        when(supergruppoDAO.findById(expectedCommissione.getId())).thenReturn(Optional.of(expectedCommissione));
        List<Persona> acutalPersone = gruppoService.findAllMembriInGruppoNoCommissione(expectedCommissione.getId());
        assertEquals(expectedPersone, acutalPersone);
    }

    @ParameterizedTest
    @MethodSource("providecloseCommissione")
    void closeCommissione(Commissione expectedCommissione) {
        when(supergruppoDAO.findById(expectedCommissione.getId())).thenReturn(Optional.of(expectedCommissione));
        gruppoService.closeCommissione(expectedCommissione.getId());
        assertEquals(false, expectedCommissione.getState());
    }

    @ParameterizedTest
    @MethodSource("providecreateCommissione")
    void createCommissione(Commissione expectedCommissione, Commissione actualCommissione, Gruppo expectedGruppo) {
        when(supergruppoDAO.findById(expectedGruppo.getId())).thenReturn(Optional.of(expectedGruppo));
        gruppoService.createCommissione(expectedCommissione, expectedGruppo.getId());
        actualCommissione.setGruppo(expectedGruppo);
        assertEquals(actualCommissione, expectedCommissione);
    }

    @ParameterizedTest
    @MethodSource("providenominaResponsabile")
    void nominaResponsabile(Persona persona1, Commissione expectedCommissione, Commissione actualCommissione) {
        when(personaDAO.findById(persona1.getId())).thenReturn(Optional.of(persona1));
        when(supergruppoDAO.findById(expectedCommissione.getId())).thenReturn(Optional.of(expectedCommissione));
        actualCommissione.addPersona(persona1);
        actualCommissione.setResponsabile(persona1);
        assertEquals(actualCommissione, expectedCommissione);
    }

    @ParameterizedTest
    @MethodSource("providefindGruppoByCommissione")
    void findGruppoByCommissione(Gruppo expectedGruppo, Gruppo actualGruppo) {
        when(supergruppoDAO.findById(expectedGruppo.getId())).thenReturn(Optional.of(expectedGruppo));
        assertEquals(expectedGruppo, actualGruppo);
    }
}
