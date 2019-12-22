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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<Persona> visualizzaListaMembriSupergruppo(int idSupergruppo) {
        List<Persona> result = personaDAO.findBySupergruppi_id(idSupergruppo);
        return result;
    }

    @Override
    public List<Persona> visualizzaListaMembriConsiglioDidattico(int idConsiglio) {
        return  personaDAO.findByConsigli_id(idConsiglio);
    }

    @Override
    public List<Persona> visualizzaListaMembriDipartimento(int idDipartimento) {
        return  personaDAO.findByDipartimenti_id(idDipartimento);
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


    @Override
    public List<Persona> findAllMembriInConsiglioDidatticoNoSupergruppo(int idConsiglioDidattico, int idSupergruppo) {
        List<Persona> inConsiglio = personaDAO.findByConsigli_id(idConsiglioDidattico);
        List<Persona> inSupergruppo = personaDAO.findBySupergruppi_id(idSupergruppo);
        List<Persona> persone = new ArrayList<Persona>();
        for (Persona p: inConsiglio){
            if(!inSupergruppo.contains(p))
                persone.add(p);
        }
        return persone;
    }


    @Override
    public Persona findPersona(int id) {
        return personaDAO.findById(id);
    }
    @Override
    public Supergruppo findSupergruppo(int id) {
        return supergruppoDAO.findById(id);
    }


    @Override
    public void addMembro(Persona persona, Supergruppo supergruppo){
        supergruppo.addPersona(persona);
        supergruppoDAO.save(supergruppo);
        personaDAO.save(persona);
    }

    @Override
    public void removeMembro(Persona persona, Supergruppo supergruppo){
        supergruppo.removePersona(persona);
        supergruppoDAO.save(supergruppo);
        personaDAO.save(persona);
    }

    @Override
    public boolean isResponsabile(int idPersona, int idSupergruppo) {
        Supergruppo supergruppo = supergruppoDAO.findById(idSupergruppo);
        if(idPersona==supergruppo.getResponsabile().getId())
            return true;
        return false;
    }


    @Override
    public Persona visualizzaPersonaLoggata() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return personaDAO.findByUser_email(auth.getName());
    }

    @Override
    public ConsiglioDidattico findConsiglioBySupergruppo(int idSupergruppo){
        return consiglioDidatticoDAO.findBySupergruppo_id(idSupergruppo);
    }

}
