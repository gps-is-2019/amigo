package it.unisa.Amigo.task.controller;

import lombok.*;

/**
 * Classe che si occuppa del passaggio di parametri dal Form html alla classe TaskController.
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TaskForm {

    /**
     * Identificativo del relativo task che verrà creato o modificato.
     */
    private Integer id;

    /**
     * Breve descrizione del task che verrà creato o modificato.
     */
    private String descrizione;

    /**
     * Indica la data di scadenza del task che verrà creato o modificato.
     */
    private String dataScadenza;

    /**
     * Indica il nome del task che verrà creato o modificato.
     */
    private String nome;

    /**
     * indica lo stato del task che verrà creato o modificato.
     */
    private String stato;

    /**
     * Indica lo stato del task che verrà creato o modificato.
     */
    private Integer idPersona;

}
