package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.domain.*;

import java.util.List;

/**
 * Questa interfaccia definisce i metodi  per la logica di Business del sottositema "Gruppo"
 */
public interface GruppoService {
    List<Persona> findAllMembriInSupergruppo(Integer idSupergruppo);

    List<Persona> findAllMembriInConsiglioDidattico(Integer idConsiglioDidattico);

    List<Persona> findAllMembriInDipartimento(Integer idDipartimento);

    List<Persona> findAllMembriInConsiglioDidatticoNoSupergruppo(Integer idSupergruppo);

    List<Persona> findAllMembriInGruppoNoCommissione(Integer idSupergruppo);

    Persona findPersona(Integer id);

    Persona getAuthenticatedUser();

    void addMembro(Persona persona, Supergruppo supergruppo);

    void removeMembro(Persona persona, Supergruppo supergruppo);

    boolean isResponsabile(Integer idPersona, Integer idSupergruppo);

    List<Supergruppo> findAllSupergruppiOfPersona(Integer idPersona);

    Supergruppo findSupergruppo(Integer id);

    List<ConsiglioDidattico> findAllConsigliDidatticiOfPersona(Integer idPersona);

    ConsiglioDidattico findConsiglioBySupergruppo(Integer idSupergruppo);

    List<Dipartimento> findAllDipartimentiOfPersona(Integer idPersona);

    List<Commissione> findAllCommissioniByGruppo(Integer idGruppo);

    void closeCommissione(Integer idSupergruppo);

    void createCommissione(Commissione commissione, Integer idSupergruppo);

    void nominaResponsabile(Integer idPersona, Integer idCommissione);

    Gruppo findGruppoByCommissione(Integer idCommissione);
}
