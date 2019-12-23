package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Dipartimento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;

import java.util.List;

/**
 * Questa interfaccia definisce i metodi  per la logica di Business del sottositema "Gruppo"
 */
public interface GruppoService {
    List<Persona> visualizzaListaMembriSupergruppo(int idSupergruppo);
    List<Persona> visualizzaListaMembriConsiglioDidattico(int idConsiglioDidattico);
    List<Persona> visualizzaListaMembriDipartimento(int idDipartimento);
    List<Supergruppo> visualizzaSupergruppi(int idPersona);
    List<ConsiglioDidattico> visualizzaConsigliDidattici(int idPersona);
    List<Dipartimento> visualizzaDipartimenti(int idPersona);
    ConsiglioDidattico findConsiglioBySupergruppo(int idSupergruppo);
    List<Persona> findAllMembriInConsiglioDidatticoNoSupergruppo(int idSupergruppo);
    Persona findPersona(int id);
    Supergruppo findSupergruppo(int id);
    void addMembro(Persona persona, Supergruppo supergruppo);
    void removeMembro(Persona persona, Supergruppo supergruppo);
    boolean isResponsabile(int idPersona, int idSupergruppo);
    Persona visualizzaPersonaLoggata();

}
