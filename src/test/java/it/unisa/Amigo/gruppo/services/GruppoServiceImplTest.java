package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.dao.*;
import it.unisa.Amigo.gruppo.domain.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class GruppoServiceImplTest {

    @InjectMocks
    private GruppoServiceImpl gruppoService;

    @Mock
    private  PersonaDAO personaDAO;

    @Mock
    private   SupergruppoDAO supergruppoDAO;

    @Mock
    private  ConsiglioDidatticoDAO consiglioDidatticoDAO;

    @Mock
    private  DipartimentoDAO dipartimentoDAO;

    @Mock
    private CommissioneDAO commissioneDAO;

    @Mock
    private GruppoDAO gruppoDAO;


    @Test
    void findAllMembriInSupergruppo() {
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Persona persona2 = new Persona("Persona2","Persona2","Persona");
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "gruppo", true);
        supergruppo.addPersona(persona1);
        supergruppo.addPersona(persona2);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona1);
        expectedPersone.add(persona2);
        when(personaDAO.findBySupergruppi_id(supergruppo.getId())).thenReturn(expectedPersone);
        List<Persona> actualPersone = gruppoService.findAllMembriInSupergruppo(supergruppo.getId());
        assertEquals(expectedPersone, actualPersone);
    }

    @Test
    void findAllMembriInConsiglioDidattico() {
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Persona persona2 = new Persona("Persona2","Persona2","Persona");
        ConsiglioDidattico consiglioDidattico = new ConsiglioDidattico("Consiglio Informatica");
        consiglioDidattico.addPersona(persona1);
        consiglioDidattico.addPersona(persona2);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona1);
        expectedPersone.add(persona2);
        when(personaDAO.findByConsigli_id(consiglioDidattico.getId())).thenReturn(expectedPersone);
        List<Persona> actualPersone = gruppoService.findAllMembriInConsiglioDidattico(consiglioDidattico.getId());
        assertEquals(expectedPersone, actualPersone);
    }

    @Test
    void findAllMembriInDipartimento() {
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Persona persona2 = new Persona("Persona2","Persona2","Persona");
        Dipartimento dipartimento = new Dipartimento("Informatica");
        dipartimento.addPersona(persona1);
        dipartimento.addPersona(persona2);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona1);
        expectedPersone.add(persona2);
        when(personaDAO.findByDipartimenti_id(dipartimento.getId())).thenReturn(expectedPersone);
        List<Persona> actualPersone = gruppoService.findAllMembriInDipartimento(dipartimento.getId());
        assertEquals(expectedPersone, actualPersone);
    }

    @Test
    void findAllSupergruppiOfPersona() {
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "gruppo", true);
        Supergruppo supergruppo1 = new Supergruppo("GAQR Informatica" , "gruppo", true);
        supergruppo.addPersona(persona1);
        supergruppo1.addPersona(persona1);
        List<Supergruppo> expectedSupergruppi = new ArrayList<>();
        expectedSupergruppi.add(supergruppo1);
        expectedSupergruppi.add(supergruppo);
        when(supergruppoDAO.findAllByPersone_id(persona1.getId())).thenReturn(expectedSupergruppi);
        List<Supergruppo> actualSupergruppi = gruppoService.findAllSupergruppiOfPersona(persona1.getId());
        assertEquals(expectedSupergruppi, actualSupergruppi);
    }

    @Test
    void findAllConsigliDidatticiOfPersona() {
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        ConsiglioDidattico consiglioDidattico = new ConsiglioDidattico("Informatica");
        ConsiglioDidattico consiglioDidattico1 = new ConsiglioDidattico("Ingegneria");
        consiglioDidattico.addPersona(persona1);
        consiglioDidattico1.addPersona(persona1);
        List<ConsiglioDidattico> expectedConsigli = new ArrayList<>();
        expectedConsigli.add(consiglioDidattico);
        expectedConsigli.add(consiglioDidattico1);
        when(consiglioDidatticoDAO.findAllByPersone_id(persona1.getId())).thenReturn(expectedConsigli);
        List<ConsiglioDidattico> actualConsigli = gruppoService.findAllConsigliDidatticiOfPersona(persona1.getId());
        assertEquals(expectedConsigli, actualConsigli);
    }

    @Test
    void findAllDipartimentiOfPersona() {
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Dipartimento dipartimento = new Dipartimento("Informatica");
        Dipartimento dipartimento1 = new Dipartimento("Ingegneria");
        dipartimento.addPersona(persona1);
        dipartimento1.addPersona(persona1);
        List<Dipartimento> expectedDipartimenti = new ArrayList<>();
        expectedDipartimenti.add(dipartimento);
        expectedDipartimenti.add(dipartimento1);
        when(dipartimentoDAO.findAllByPersone_id(persona1.getId())).thenReturn(expectedDipartimenti);
        List<Dipartimento> actualDipartimenti = gruppoService.findAllDipartimentiOfPersona(persona1.getId());
        assertEquals(expectedDipartimenti, actualDipartimenti);
    }

    @Test
    void findAllMembriInConsiglioDidatticoNoSupergruppo() {

        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Persona persona2 = new Persona("Persona2","Persona2","Persona");

        ConsiglioDidattico consiglioDidattico = new ConsiglioDidattico("Informatica");
        consiglioDidattico.addPersona(persona1);
        consiglioDidattico.addPersona(persona2);

        Supergruppo supergruppo = new Supergruppo("GAQR Informatica" , "gruppo", true);
        supergruppo.setConsiglio(consiglioDidattico);
        consiglioDidattico.setSupergruppo(supergruppo);
        supergruppo.addPersona(persona1);


        List<Persona> expectedSupergruppoPersone = new ArrayList<>();
        expectedSupergruppoPersone.add(persona1);

        List<Persona> exptectedConsiglioPersone = new ArrayList<>();
        exptectedConsiglioPersone.add(persona1);
        exptectedConsiglioPersone.add(persona2);

        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona2);

        when(supergruppoDAO.findById(supergruppo.getId())).thenReturn(supergruppo);


        List<Persona> actualPersone = gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(supergruppo.getId());
        assertEquals(actualPersone, expectedPersone);
    }


    @Test
    void findPersona() {
        Persona expectedPersona = new Persona("Persona1","Persona1","Persona");
        when(personaDAO.findById(expectedPersona.getId())).thenReturn(expectedPersona);
        Persona actualPersona = gruppoService.findPersona(expectedPersona.getId());
        assertEquals(expectedPersona, actualPersona);
    }

    @Test
    void findSupergruppo() {
        Supergruppo expectedSupergruppo = new Supergruppo("GAQR- Informatica", "gruppo", true);
        when(supergruppoDAO.findById(expectedSupergruppo.getId())).thenReturn(expectedSupergruppo);
        Supergruppo actualSupergruppo = gruppoService.findSupergruppo(expectedSupergruppo.getId());
        assertEquals(expectedSupergruppo, actualSupergruppo);
    }


    @Test
    void addMembro() {
        Persona expectedPersona = new Persona("Persona1","Persona1","Persona");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQR- Informatica", "gruppo", true);
        int oldSize = expectedSupergruppo.getPersone().size();
        gruppoService.addMembro(expectedPersona, expectedSupergruppo);
        int actualSize = expectedSupergruppo.getPersone().size();
        assertEquals(oldSize+1, actualSize);
    }

    @Test
    void removeMembro() {
        Persona expectedPersona = new Persona("Persona1","Persona1","Persona");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQR- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        int oldSize = expectedSupergruppo.getPersone().size();
        gruppoService.removeMembro(expectedPersona, expectedSupergruppo);
        int actualSize = expectedSupergruppo.getPersone().size();
        assertEquals(oldSize-1, actualSize);
    }

    @Test
    void findConsiglioBySupergruppo() {
        Supergruppo expectedSupergruppo = new Supergruppo("GAQR- Informatica", "gruppo", true);
        ConsiglioDidattico expectedConsiglio = new ConsiglioDidattico("Informatica");
        expectedSupergruppo.setConsiglio(expectedConsiglio);
        expectedConsiglio.setSupergruppo(expectedSupergruppo);
        when(consiglioDidatticoDAO.findBySupergruppo_id(expectedSupergruppo.getId())).thenReturn(expectedConsiglio);
        ConsiglioDidattico actualConsiglio = gruppoService.findConsiglioBySupergruppo(expectedSupergruppo.getId());
        assertEquals(actualConsiglio, expectedConsiglio);
    }

    @Test
    void isResponsabile() {

        Persona expectedPersona = new Persona("Mario","Inglese","ciao");

        Supergruppo expectedSupergruppo = new Supergruppo( "GAQD-Informatica","gruppo",true );
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);

        when(supergruppoDAO.findById(expectedSupergruppo.getId())).thenReturn(expectedSupergruppo);
        boolean expectedValue = gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId());
        assertEquals(true, expectedValue);
    }

    @Test
    void findAllCommissioniByGruppo() {
         Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
         Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true,  "Commissione");
         Commissione expectedCommissione2 = new Commissione("Commissione2", "Commissione2", true,  "Commissione2");
         List<Commissione> expectedCommissioni = new ArrayList<>();
         expectedCommissioni.add(expectedCommissione);
         expectedCommissioni.add(expectedCommissione2);
         expectedGruppo.addCommissione(expectedCommissione);
         expectedGruppo.addCommissione(expectedCommissione2);

         when(commissioneDAO.findAllByGruppo_id(expectedGruppo.getId())).thenReturn(expectedCommissioni);

         List<Commissione> actualCommissioni = gruppoService.findAllCommissioniByGruppo(expectedGruppo.getId());

         assertEquals(actualCommissioni, expectedCommissioni);
    }

    @Test
    void findAllMembriInGruppoNoCommissione() {

        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Persona persona2 = new Persona("Persona2","Persona2","Persona");

        List<Persona> persone = new ArrayList<>();
        persone.add(persona1);
        persone.add(persona2);

        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true,  "Commissione");
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        expectedGruppo.addPersona(persona1);
        expectedGruppo.addPersona(persona2);
        expectedCommissione.addPersona(persona1);
        expectedGruppo.addCommissione(expectedCommissione);

        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona2);

        when(commissioneDAO.findById(expectedCommissione.getId())).thenReturn(expectedCommissione);

        List<Persona> acutalPersone = gruppoService.findAllMembriInGruppoNoCommissione(expectedCommissione.getId());
        assertEquals(expectedPersone, acutalPersone);
    }

    @Test
    void closeCommissione() {
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true, "Commissione");
        when(commissioneDAO.findById(expectedCommissione.getId())).thenReturn(expectedCommissione);
        gruppoService.closeCommissione(expectedCommissione.getId());
        assertEquals(false, expectedCommissione.getState());
    }

    @Test
    void createCommissione() {
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true,  "Commissione");
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);


        when(gruppoDAO.findById(expectedGruppo.getId())).thenReturn(expectedGruppo);

        gruppoService.createCommissione(expectedCommissione, expectedGruppo.getId());

        Commissione actualCommissione = new Commissione("Commissione", "Commissione", true,  "Commissione");
        actualCommissione.setGruppo(expectedGruppo);
        assertEquals(actualCommissione, expectedCommissione);
    }


    @Test
    void nominaResponsabile() {

        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Commissione expectedCommissione = new Commissione("Commissione", "Commissione", true,  "Commissione");

        when(personaDAO.findById(persona1.getId())).thenReturn(persona1);
        when(commissioneDAO.findById(expectedCommissione.getId())).thenReturn(expectedCommissione);

        Commissione actualCommissione = new Commissione("Commissione", "Commissione", true,  "Commissione");
        actualCommissione.addPersona(persona1);
        actualCommissione.setResponsabile(persona1);

        assertEquals(actualCommissione,expectedCommissione);
    }

    @Test
    void findGruppoByCommissione() {
        Gruppo expectedGruppo = new Gruppo("Gruppo", "Gruppo", true);
        when(gruppoDAO.findById(expectedGruppo.getId())).thenReturn(expectedGruppo);
        Gruppo actualGruppo = new Gruppo("Gruppo", "Gruppo", true);
        assertEquals(expectedGruppo, actualGruppo);
    }

}