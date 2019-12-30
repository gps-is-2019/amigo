package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.domain.*;

import java.util.List;

/**
 * Questa interfaccia definisce i metodi  per la logica di Business del sottositema "Gruppo"
 */
public interface GruppoService {
    List<Persona> findAllMembriInSupergruppo(int idSupergruppo);
    List<Persona> findAllMembriInConsiglioDidattico(int idConsiglioDidattico);
    List<Persona> findAllMembriInDipartimento(int idDipartimento);
    List<Persona> findAllMembriInConsiglioDidatticoNoSupergruppo(int idSupergruppo);
    List<Persona> findAllMembriInGruppoNoCommissione(int idSupergruppo);
    Persona findPersona(int id);
    Persona getAuthenticatedUser();
    void addMembro(Persona persona, Supergruppo supergruppo);
    void removeMembro(Persona persona, Supergruppo supergruppo);
    boolean isResponsabile(int idPersona, int idSupergruppo);
    List<Supergruppo> findAllSupergruppiOfPersona(int idPersona);
    Supergruppo findSupergruppo(int id);
    List<ConsiglioDidattico> findAllConsigliDidatticiOfPersona(int idPersona);
    ConsiglioDidattico findConsiglioBySupergruppo(int idSupergruppo);
    List<Dipartimento> findAllDipartimentiOfPersona(int idPersona);
    List<Commissione> findAllCommissioniByGruppo(int idGruppo);
    void closeCommissione(int idSupergruppo);
    void createCommissione(Commissione commissione, int idSupergruppo);
    void nominaResponsabile(int idPersona, int idCommissione);
    Gruppo findGruppoByCommissione(int idCommissione);
}
