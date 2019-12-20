package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.DipartimentoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Dipartimento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GruppoServiceImpl implements GruppoService {

    @Autowired
    private final PersonaDAO personaDAO;

    @Autowired
    private  final SupergruppoDAO supergruppoDAO;

    @Autowired
    private final ConsiglioDidatticoDAO consiglioDidatticoDAO;

    @Autowired
    private final DipartimentoDAO dipartimentoDAO;

    @Override
    public List<Persona> visualizzaListaMembriSupergruppo(int id) {
        List<Persona> result = personaDAO.findBySupergruppi_id(id);
        return result;
    }

    @Override
    public List<Persona> visualizzaListaMembriConsiglioDidattico(int id) {
        List<Persona> result;
        result = personaDAO.findByConsigli_id(id);
        System.out.println(result.toString());
        return result;
    }

    @Override
    public List<Persona> visualizzaListaMembriDipartimento(int id) {
        List<Persona> result;
        result = personaDAO.findByDipartimenti_id(id);
        return result;
    }

    @Override
    public List<Supergruppo> visualizzaSupergruppi(int idPersona) {
        return supergruppoDAO.findAllByPersone_id(idPersona);
    }

    @Override
    public List<ConsiglioDidattico> visualizzaConsigliDidattici(int idPersona) {
        return consiglioDidatticoDAO.findAllByPersone_id(idPersona);
    }

    @Override
    public List<Dipartimento> visualizzaDipartimenti(int idPersona) {
        return dipartimentoDAO.findAllByPersone_id(idPersona);
    }


}
