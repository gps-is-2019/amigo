package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Dipartimento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;

import java.util.List;

public interface GruppoService {
    List<Persona> visualizzaListaMembriSupergruppo(int id);

    List<Persona> visualizzaListaMembriConsiglioDidattico(int id);

    List<Persona> visualizzaListaMembriDipartimento(int id);

    List<Supergruppo> visualizzaSupergruppi(int idPersona);
    List<ConsiglioDidattico> visualizzaConsigliDidattici(int idPersona);
    List<Dipartimento> visualizzaDipartimenti(int idPersona);

}
