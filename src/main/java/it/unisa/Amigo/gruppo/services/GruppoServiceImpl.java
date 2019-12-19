package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GruppoServiceImpl implements GruppoService {

    @Autowired
    private final PersonaDAO personaDAO;

    @Override
    public List<Persona> visualizzaListaMembriSupergruppo(int id) {
        List<Persona> result;
        result = personaDAO.findBySupergruppo_id(id);
        System.out.println(result.toString());
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
        result = personaDAO.findByDipartimento_id(id);
        System.out.println(result.toString());
        return result;
    }
}
