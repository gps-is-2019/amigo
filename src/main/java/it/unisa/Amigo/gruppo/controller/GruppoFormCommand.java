package it.unisa.Amigo.gruppo.controller;

import lombok.Data;

/**
 * Gestisce e mantiene le informazioni del form inerente alla creazione di una nuova commissione
 */
@Data
public class GruppoFormCommand {
    private String name;
    private String descrizione;
    private int idPersona;
}
