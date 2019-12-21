package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.DipartimentoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Dipartimento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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


    @Test
    void visualizzaListaMembriSupergruppo() {
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Persona persona2 = new Persona("Persona2","Persona2","Persona");
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "gruppo", true);
        supergruppo.addPersona(persona1);
        supergruppo.addPersona(persona2);
        personaDAO.save(persona1);
        personaDAO.save(persona2);
        supergruppoDAO.save(supergruppo);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona1);
        expectedPersone.add(persona2);
        List<Persona> actualPersone = gruppoService.visualizzaListaMembriSupergruppo(supergruppo.getId());
        assertEquals(expectedPersone, actualPersone);
    }

    @Test
    void visualizzaListaMembriDipartimento() {
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Persona persona2 = new Persona("Persona2","Persona2","Persona");
        Dipartimento dipartimento = new Dipartimento("Informatica");
        dipartimento.addPersona(persona1);
        dipartimento.addPersona(persona2);
        personaDAO.save(persona1);
        personaDAO.save(persona2);
        dipartimentoDAO.save(dipartimento);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(persona1);
        expectedPersone.add(persona2);
        List<Persona> actualPersone = gruppoService.visualizzaListaMembriDipartimento(dipartimento.getId());
        assertEquals(expectedPersone, actualPersone);
    }

    @Test
    void visualizzaSupergruppi() {
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "gruppo", true);
        Supergruppo supergruppo1 = new Supergruppo("GAQR Informatica" , "gruppo", true);
        supergruppo.addPersona(persona1);
        supergruppo1.addPersona(persona1);
        personaDAO.save(persona1);
        supergruppoDAO.save(supergruppo);
        supergruppoDAO.save(supergruppo1);
        List<Supergruppo> expectedSupergruppi = new ArrayList<>();
        expectedSupergruppi.add(supergruppo1);
        expectedSupergruppi.add(supergruppo);
        List<Supergruppo> actualSupergruppi = gruppoService.visualizzaSupergruppi(persona1.getId());
        assertEquals(expectedSupergruppi, actualSupergruppi);
    }

    @Test
    void visualizzaConsigliDidattici() {
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        ConsiglioDidattico consiglioDidattico = new ConsiglioDidattico("Informatica");
        ConsiglioDidattico consiglioDidattico1 = new ConsiglioDidattico("Ingegneria");
        consiglioDidattico.addPersona(persona1);
        consiglioDidattico1.addPersona(persona1);
        personaDAO.save(persona1);
        consiglioDidatticoDAO.save(consiglioDidattico);
        consiglioDidatticoDAO.save(consiglioDidattico1);
        List<ConsiglioDidattico> expectedConsigli = new ArrayList<>();
        expectedConsigli.add(consiglioDidattico1);
        expectedConsigli.add(consiglioDidattico);
        List<ConsiglioDidattico> actualConsigli = gruppoService.visualizzaConsigliDidattici(persona1.getId());
        assertEquals(expectedConsigli, actualConsigli);
    }

    @Test
    void visualizzaDipartimenti() {
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Dipartimento dipartimento = new Dipartimento("Informatica");
        Dipartimento dipartimento1 = new Dipartimento("Ingegneria");
        dipartimento.addPersona(persona1);
        dipartimento1.addPersona(persona1);
        personaDAO.save(persona1);
        dipartimentoDAO.save(dipartimento);
        dipartimentoDAO.save(dipartimento1);
        List<Dipartimento> expectedDipartimenti = new ArrayList<>();
        expectedDipartimenti.add(dipartimento1);
        expectedDipartimenti.add(dipartimento);
        List<Dipartimento> actualDipartimenti = gruppoService.visualizzaDipartimenti(persona1.getId());
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


       personaDAO.save(persona1);
       personaDAO.save(persona2);
       consiglioDidatticoDAO.save(consiglioDidattico);
       supergruppoDAO.save(supergruppo);

        List<Persona> actualPersone = gruppoService.findAllMembriInConsiglioDidatticoNoSupergruppo(consiglioDidattico.getId(), supergruppo.getId());
        assertEquals(actualPersone, expectedPersone);
    }

    @Test
    void findPersona() {
        Persona expectedPersona = new Persona("Persona1","Persona1","Persona");
        personaDAO.save(expectedPersona);
        Persona actualPersona = gruppoService.findPersona(expectedPersona.getId());
        assertEquals(expectedPersona, actualPersona);
    }

    @Test
    void findSupergruppo() {
        Supergruppo expectedSupergruppo = new Supergruppo("GAQR- Informatica", "gruppo", true);
        supergruppoDAO.save(expectedSupergruppo);
        Supergruppo actualSupergruppo = gruppoService.findSupergruppo(expectedSupergruppo.getId());
        assertEquals(expectedSupergruppo, actualSupergruppo);
    }

    @Test
    void findConsiglioBySupergruppo() {
        Supergruppo expectedSupergruppo = new Supergruppo("GAQR- Informatica", "gruppo", true);
        ConsiglioDidattico expectedConsiglio = new ConsiglioDidattico("Informatica");
        expectedSupergruppo.setConsiglio(expectedConsiglio);
        expectedConsiglio.setSupergruppo(expectedSupergruppo);
        consiglioDidatticoDAO.save(expectedConsiglio);
        supergruppoDAO.save(expectedSupergruppo);
        ConsiglioDidattico actualConsiglio = gruppoService.findConsiglioBySupergruppo(expectedSupergruppo.getId());
        assertEquals(actualConsiglio, expectedConsiglio);
    }

    @Test
    void isResponsabile() {
        Persona expectedPersona = new Persona("Mario","Inglese","ciao");

        Supergruppo expectedSupergruppo = new Supergruppo( "GAQD-Informatica","gruppo",true );
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);

        boolean expectedValue = gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId());
        assertEquals(true, expectedValue);
    }


}
