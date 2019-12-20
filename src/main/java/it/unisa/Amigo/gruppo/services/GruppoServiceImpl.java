package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GruppoServiceImpl implements GruppoService {

    @Autowired
    private final PersonaDAO personaDAO;

    @Autowired
    private  final SupergruppoDAO supergruppoDAO;

    @Override
    public List<Persona> visualizzaListaMembriSupergruppo(int id) {
        Optional<Supergruppo> supergruppo = supergruppoDAO.findById(id);
        if(!supergruppo.isPresent()){
            return Collections.emptyList();
        }
        List<Persona> result = personaDAO.findBySupergruppo(supergruppo.get());

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
