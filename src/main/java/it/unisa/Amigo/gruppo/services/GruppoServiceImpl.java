package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.DipartimentoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Questa classe implementa i metodi  per la logica di Business del sottositema "Gruppo"
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GruppoServiceImpl implements GruppoService {

    private final PersonaDAO personaDAO;

    private final SupergruppoDAO supergruppoDAO;

    private final ConsiglioDidatticoDAO consiglioDidatticoDAO;

    private final DipartimentoDAO dipartimentoDAO;

    /***
     * Ritorna la lista di persone @{@link Persona} presenti nel supergruppo @{@link Supergruppo}
     * @param idSupergruppo id del supergruppo di cui si vogliono visualizzare i membri
     * @return lista di persone
     */
    @Override
    public List<Persona> findAllMembriInSupergruppo(Integer idSupergruppo) {
        List<Persona> result = personaDAO.findBySupergruppi_id(idSupergruppo);
        return result;
    }

    /***
     * Ritorna la lista di persone @{@link Persona} presenti nel consiglio didattico @{@link ConsiglioDidattico}
     * @param idConsiglio id del consiglio didattico di cui si vogliono visualizzare i membri
     * @return lista di persone
     */
    @Override
    public List<Persona> findAllMembriInConsiglioDidattico(Integer idConsiglio) {
        return personaDAO.findByConsigli_id(idConsiglio);
    }

    /***
     * Ritorna la lista di persone @{@link Persona} presenti nel dipartimento @{@link Dipartimento}
     * @param idDipartimento id del dipartimento di cui si vogliono visualizzare i membri
     * @return lista di persone
     */
    @Override
    public List<Persona> findAllMembriInDipartimento(Integer idDipartimento) {
        return personaDAO.findByDipartimenti_id(idDipartimento);
    }

    /***
     * Ritorna la lista di supergruppi @{@link Supergruppo} di una persona @{@link Persona}
     * @param idPersona id della persona di cui si vogliono visualizzare i supergruppi a cui appartiene
     * @return lista di supergruppi
     */
    @Override
    public List<Supergruppo> findAllSupergruppiOfPersona(Integer idPersona) {
        return supergruppoDAO.findAllByPersone_id(idPersona);
    }

    /***
     * Ritorna la lista di consigli didattici @{@link ConsiglioDidattico} di una persona @{@link Persona}
     * @param idPersona id della persona di cui si vogliono visualizzare i consigli didattici a cui appartiene
     * @return lista di consigli didattici
     */
    @Override
    public List<ConsiglioDidattico> findAllConsigliDidatticiOfPersona(Integer idPersona) {
        return consiglioDidatticoDAO.findAllByPersone_id(idPersona);
    }

    /***
     * Ritorna la lista di dipartimenti @{@link Dipartimento} di una persona @{@link Persona}
     * @param idPersona id della persona di cui si vogliono visualizzare i dipartimenti a cui appartiene
     * @return lista di dipartimenti
     */
    @Override
    public List<Dipartimento> findAllDipartimentiOfPersona(Integer idPersona) {
        return dipartimentoDAO.findAllByPersone_id(idPersona);
    }

    /***
     * Ritorna la lista di tutte le persone @{@link Persona} che sono nel consiglio didattico @{@link ConsiglioDidattico} ma non nel supergruppo @{@link Supergruppo}
     * @param idSupergruppo id del supergruppo
     * @return lista di persone
     */
    @Override
    public List<Persona> findAllMembriInConsiglioDidatticoNoSupergruppo(Integer idSupergruppo) {
        Supergruppo supergruppo = supergruppoDAO.findById(idSupergruppo).get();
        Set<Persona> inSupergruppo = supergruppo.getPersone();/*personaDAO.findBySupergruppi_id(idSupergruppo);*/
        Set<Persona> inConsiglio = supergruppo.getConsiglio().getPersone();//personaDAO.findByConsigli_id(idConsiglioDidattico);
        //inConsiglio.removeAll(inSupergruppo);
        List<Persona> persone = new ArrayList<>();
        for (Persona p : inConsiglio) {
            if (!inSupergruppo.contains(p))
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
    public Persona findPersona(Integer id) {
        return personaDAO.findById(id).get();
    }

    /***
     * Ritorna il supergruppo @{@link Supergruppo} in base al suo id
     * @param id id del supergruppo
     * @return supergruppo
     */
    @Override
    public Supergruppo findSupergruppo(Integer id) {
        return supergruppoDAO.findById(id).get();
    }

    /***
     * Consente di aggiungere un membro @{@link Persona} al supergruppo @{@link Supergruppo}
     * @param persona persona da aggiungere
     * @param supergruppo in cui aggiungere la persona
     */
    @Override
    public void addMembro(Persona persona, Supergruppo supergruppo) {
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
    public void removeMembro(Persona persona, Supergruppo supergruppo) {
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
    public boolean isResponsabile(Integer idPersona, Integer idSupergruppo) {
        Supergruppo supergruppo = supergruppoDAO.findById(idSupergruppo).get();
        return idPersona == supergruppo.getResponsabile().getId();
    }

    /***
     * Ritorna la persona @{@link Persona} loggata nel sistema
     * @return persona
     */
    @Override
    public Persona getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return personaDAO.findByUser_email(auth.getName());
    }

    /**
     * Restituisce tutte le commissioni @{@link Commissione} di un gruppo @{@link Gruppo}
     *
     * @param idGruppo il gruppo in relazione alle commissioni
     * @return la lista delle commissioni restistuite dalla chiamata al DAO
     */
    @Override
    public List<Commissione> findAllCommissioniByGruppo(Integer idGruppo) {
        Gruppo gruppo = (Gruppo) supergruppoDAO.findById(idGruppo).get();
        List<Commissione> result = new ArrayList<>();
        result.addAll(gruppo.getCommissioni());
        return result;
    }

    /**
     * Restituisce la lista delle persone che si trovano  in un determinato gruppo @{@link Gruppo}, ma che non sono ancora
     * state assegnate a nessuna commissione @{@link Commissione}
     *
     * @param idSupergruppo l'id della commissione in relazione al gruppo
     * @return la lista delle persone
     */
    @Override
    public List<Persona> findAllMembriInGruppoNoCommissione(Integer idSupergruppo) {
        Commissione commissione = (Commissione) supergruppoDAO.findById(idSupergruppo).get();
        Set<Persona> inSupergruppo = commissione.getPersone();
        Set<Persona> inGruppo = commissione.getGruppo().getPersone();
        List<Persona> persone = new ArrayList<>();
        for (Persona p : inGruppo) {
            if (!inSupergruppo.contains(p))
                persone.add(p);
        }
        return persone;
    }

    /**
     * Chiude una commissione  @{@link Commissione}, rendendone impossibile qualunque modifica da parte dell'utente
     *
     * @param idSupergruppo l'id della commissione che deve essere chiusa
     */
    @Override
    public void closeCommissione(Integer idSupergruppo) {
        Commissione commissione = (Commissione) supergruppoDAO.findById(idSupergruppo).get();
        commissione.setState(false);
        supergruppoDAO.save(commissione);
    }

    /**
     * Esegue la creazione di una nuova commissione @{@link Commissione} e la aggiunge al gruppo @{@link Gruppo}
     *
     * @param commissione
     * @param idSupergruppo
     */
    @Override
    public void createCommissione(Commissione commissione, Integer idSupergruppo) {
        Gruppo gruppo = (Gruppo) supergruppoDAO.findById(idSupergruppo).get();
        gruppo.addCommissione(commissione);
        supergruppoDAO.save(commissione);
    }

    /***
     * Ritorna il consiglio didattico @{@link ConsiglioDidattico} in base ad un superguppo @{@link Supergruppo}
     * @param idSupergruppo id del supergruppo di cui si vuole il consiglio didattico
     * @return consiglio didattico
     */
    @Override
    public ConsiglioDidattico findConsiglioBySupergruppo(Integer idSupergruppo) {
        return consiglioDidatticoDAO.findBySupergruppo_id(idSupergruppo);
    }

    /**
     * Esegue il cambiamento del responsabile @{@link Persona} di una commissione @{@link Commissione}
     *
     * @param idPersona     l'id della persona che diventerà il responsabile
     * @param idCommissione l'id della commissione che riceverà un nuovo responsabile
     */
    @Override
    public void nominaResponsabile(Integer idPersona, Integer idCommissione) {
        Persona responsabile = personaDAO.findById(idPersona).get();
        Commissione commissione = (Commissione) supergruppoDAO.findById(idCommissione).get();
        commissione.addPersona(responsabile);
        commissione.setResponsabile(responsabile);
        personaDAO.save(responsabile);
        supergruppoDAO.save(commissione);
    }

    /**
     * Ritorna un gruppo @{@link Gruppo} in base ad una commissione @{@link Commissione}
     *
     * @param idCommissione la commissione in relazione al gruppo
     * @return il gruppo restituito dalla chiamata al DAO
     */
    @Override
    public Gruppo findGruppoByCommissione(Integer idCommissione) {
        Commissione commissione = (Commissione) supergruppoDAO.findById(idCommissione).get();
        Gruppo result = commissione.getGruppo();
        return result;
    }

    @Override
    public List<Persona> findAllByRuolo(String ruolo){
        return personaDAO.findAllByUser_Roles_Name(ruolo.toUpperCase());
    }

    @Override
    public List<String> findAllRoleOfPersona(Integer idPersona) {
        List<String> r = new ArrayList<>();
        Set<Role> ruoli = personaDAO.findById(idPersona).get().getUser().getRoles();
        for(Role ruolo :ruoli){
            r.add(ruolo.getName());
        }
        return r;
    }
}
