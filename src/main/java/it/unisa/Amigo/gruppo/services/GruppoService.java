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
    List<Supergruppo> findAllSupergruppi(int idPersona);
    List<ConsiglioDidattico> findAllConsigliDidattici(int idPersona);
    List<Dipartimento> findAllDipartimenti(int idPersona);
    ConsiglioDidattico findConsiglioBySupergruppo(int idSupergruppo);
    List<Persona> findAllMembriInConsiglioDidatticoNoSupergruppo(int idSupergruppo);
    Persona findPersona(int id);
    Supergruppo findSupergruppo(int id);
    void addMembro(Persona persona, Supergruppo supergruppo);
    void removeMembro(Persona persona, Supergruppo supergruppo);
    boolean isResponsabile(int idPersona, int idSupergruppo);
    boolean isCapogruppo(int idPersona);
    Persona getAuthenticatedUser();
    List<Commissione> findAllCommissioniByGruppo(int idGruppo);
    List<Persona> findAllMembriInGruppoNoCommissione(int idSupergruppo);
    void closeCommissione(int idSupergruppo);
    void createCommissione(Commissione commissione, int idSupergruppo);
}
