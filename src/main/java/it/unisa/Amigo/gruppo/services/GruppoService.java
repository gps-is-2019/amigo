package it.unisa.Amigo.gruppo.services;

import it.unisa.Amigo.gruppo.domain.Persona;

import java.util.List;

public interface GruppoService
{
    List<Persona> visualizzaListaMembriSupergruppo(int id);
}
