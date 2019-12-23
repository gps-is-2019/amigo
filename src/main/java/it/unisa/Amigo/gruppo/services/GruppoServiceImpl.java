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

/**
 * Questa classe implementa i metodi  per la logica di Business del sottositema "Gruppo"
 */
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

    /***
     * Ritorna la lista di persone @{@link Persona} presenti nel supergruppo @{@link Supergruppo}
     * @param idSupergruppo id del supergruppo di cui si vogliono visualizzare i membri
     * @return lista di persone
     */
    @Override
    public List<Persona> visualizzaListaMembriSupergruppo(int idSupergruppo) {
        List<Persona> result = personaDAO.findBySupergruppi_id(idSupergruppo);
        return result;
    }

    /***
     * Ritorna la lista di persone @{@link Persona} presenti nel consiglio didattico @{@link ConsiglioDidattico}
     * @param idConsiglio id del consiglio didattico di cui si vogliono visualizzare i membri
     * @return lista di persone
     */
    @Override
    public List<Persona> visualizzaListaMembriConsiglioDidattico(int idConsiglio) {
        return  personaDAO.findByConsigli_id(idConsiglio);
    }

    /***
     * Ritorna la lista di persone @{@link Persona} presenti nel dipartimento @{@link Dipartimento}
     * @param idDipartimento id del dipartimento di cui si vogliono visualizzare i membri
     * @return lista di persone
     */
    @Override
    public List<Persona> visualizzaListaMembriDipartimento(int idDipartimento) {
        return  personaDAO.findByDipartimenti_id(idDipartimento);
    }

    /***
     * Ritorna la lista di supergruppi @{@link Supergruppo} di una persona @{@link Persona}
     * @param idPersona id della persona di cui si vogliono visualizzare i supergruppi a cui appartiene
     * @return lista di supergruppi
     */
    @Override
    public List<Supergruppo> visualizzaSupergruppi(int idPersona) {
        return supergruppoDAO.findAllByPersone_id(idPersona);
    }

    /***
     * Ritorna la lista di consigli didattici @{@link ConsiglioDidattico} di una persona @{@link Persona}
     * @param idPersona id della persona di cui si vogliono visualizzare i consigli didattici a cui appartiene
     * @return lista di consigli didattici
     */
    @Override
    public List<ConsiglioDidattico> visualizzaConsigliDidattici(int idPersona) {
        return consiglioDidatticoDAO.findAllByPersone_id(idPersona);
    }

    /***
     * Ritorna la lista di dipartimenti @{@link Dipartimento} di una persona @{@link Persona}
     * @param idPersona id della persona di cui si vogliono visualizzare i dipartimenti a cui appartiene
     * @return lista di dipartimenti
     */
    @Override
    public List<Dipartimento> visualizzaDipartimenti(int idPersona) {
        return dipartimentoDAO.findAllByPersone_id(idPersona);
    }

    /***
     * Ritorna la lista di tutte le persone @{@link Persona} che sono nel consiglio didattico @{@link ConsiglioDidattico} ma non nel supergruppo @{@link Supergruppo}
     * @param idConsiglioDidattico id del consiglio didattico
     * @param idSupergruppo id del supergruppo
     * @return lista di persone
     */
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

    /***
     * Ritorna la persona @{@link Persona} in base al suo id
     * @param id id della persona da cercare
     * @return persona
     */
    @Override
    public Persona findPersona(int id) {
        return personaDAO.findById(id);
    }

    /***
     * Ritorna il supergruppo @{@link Supergruppo} in base al suo id
     * @param id id del supergruppo
     * @return supergruppo
     */
    @Override
    public Supergruppo findSupergruppo(int id) {
        return supergruppoDAO.findById(id);
    }

    /***
     * Consente di aggiungere un membro @{@link Persona} al supergruppo @{@link Supergruppo}
     * @param persona persona da aggiungere
     * @param supergruppo in cui aggiungere la persona
     */
    @Override
    public void addMembro(Persona persona, Supergruppo supergruppo){
        supergruppo.addPersona(persona);
        supergruppoDAO.save(supergruppo);
        personaDAO.save(persona);
    }

    /***
     * Consente di rimuovere un membro @{@link Persona} da un supergruppo @{@link Supergruppo}
     * @param persona persona da rimuovere
     * @param supergruppo supergruppo da cui rimuovere la persona
     */
    @Override
    public void removeMembro(Persona persona, Supergruppo supergruppo){
        supergruppo.removePersona(persona);
        supergruppoDAO.save(supergruppo);
        personaDAO.save(persona);
    }

    /***
     * Ritorna un booleano per dire se una persona @{@link Persona} è responsabile di un supergruppo @{@link Supergruppo}
     * @param idPersona id della persona
     * @param idSupergruppo id del supergruppo
     * @return true se la persona è responsabile di quel supergruppo, false altrimenti
     */
    @Override
    public boolean isResponsabile(int idPersona, int idSupergruppo) {
        Supergruppo supergruppo = supergruppoDAO.findById(idSupergruppo);
        if(idPersona==supergruppo.getResponsabile().getId())
            return true;
        return false;
    }

    /***
     * Ritorna la persona @{@link Persona} loggata nel sistema
     * @return persona
     */
    @Override
    public Persona visualizzaPersonaLoggata() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return personaDAO.findByUser_email(auth.getName());
    }

    /***
     * Ritorna il consiglio didattico @{@link ConsiglioDidattico} in base ad un superguppo @{@link Supergruppo}
     * @param idSupergruppo id del supergruppo di cui si vuole il consiglio didattico
     * @return consiglio didattico
     */
    @Override
    public ConsiglioDidattico findConsiglioBySupergruppo(int idSupergruppo){
        return consiglioDidatticoDAO.findBySupergruppo_id(idSupergruppo);
    }

}
