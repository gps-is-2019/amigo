package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Dipartimento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;

import java.util.List;

public interface GruppoService {

    public List<Persona> findAllMembriInConsiglioDidatticoNoSupergruppo(int idConsiglioDidattico, int idSupergruppo);
    public List<Persona> findAllMembriInDipartimentoNoSupergruppo(int idDipartimento, int idSupergruppo);
    public List<Persona> findAllMembriInConsiglioDidattico (int idConsiglioDidattico);
    public List<Persona> findAllMembriInSupergruppo(int idSupergruppo);
    public Persona findPersona(int id);
    public Supergruppo findSupergruppo(int id);
    public ConsiglioDidattico findConsiglioDidattico(int id);
    public Dipartimento findDipartimento(int id);
    public void addMembro(Persona persona, Supergruppo supergruppo);
}
