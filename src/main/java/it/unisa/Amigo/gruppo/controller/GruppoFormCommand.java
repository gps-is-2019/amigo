package it.unisa.Amigo.gruppo.controller;

import it.unisa.Amigo.gruppo.domain.Commissione;
import lombok.Data;

/**
 * Gestisce e mantiene le informazioni del form inerente alla creazione di una nuova commissione @{@link Commissione}
 */
@Data
public class GruppoFormCommand {
    String name;
    String descrizione;
}
