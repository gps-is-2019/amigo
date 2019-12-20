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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GruppoServiceImpl implements GruppoService {

    private final PersonaDAO personaDao;
    private final SupergruppoDAO supergruppoDao;
    private final DipartimentoDAO dipartimentoDao;
    private final ConsiglioDidatticoDAO consiglioDidatticoDAO;

    @Override
    public List<Persona> findAllMembriInConsiglioDidatticoNoSupergruppo(int idConsiglioDidattico, int idSupergruppo) {
        List<Persona> inConsiglio = personaDao.findByConsigliDidattici_id(idConsiglioDidattico);
        List<Persona> inSupergruppo = personaDao.findBySupergruppi_id(idSupergruppo);
        List<Persona> persone = new ArrayList<Persona>();
        for (Persona p: inConsiglio){
            for (Persona p1: inSupergruppo){
                if(p.getId()!=p1.getId())
                    persone.add(p);
            }
        }
        return persone;
    }

    @Override
    public List<Persona> findAllMembriInDipartimentoNoSupergruppo(int idDipartimento, int idSupergruppo) {
        return null;
    }

    public List<Persona> findAllMembriInConsiglioDidattico (int idConsiglioDidattico) {
        return personaDao.findByConsigliDidattici_id(idConsiglioDidattico);
    }

    @Override
    public List<Persona> findAllMembriInSupergruppo(int idSupergruppo) {
        return personaDao.findBySupergruppi_id(idSupergruppo);
    }

    public Persona findPersona(int id) {
        return personaDao.findById(id);
    }
    public Supergruppo findSupergruppo(int id) {
        return supergruppoDao.findById(id);
    }
    public Dipartimento findDipartimento(int id) {
        return dipartimentoDao.findById(id);
    }
    public ConsiglioDidattico findConsiglioDidattico(int id) {
        return consiglioDidatticoDAO.findById(id);
    }


    public void addMembro(Persona persona, Supergruppo supergruppo){
        supergruppo.addPersona(persona);
        supergruppoDao.save(supergruppo);
    }
}
