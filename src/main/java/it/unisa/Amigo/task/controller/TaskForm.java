package it.unisa.Amigo.task.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class TaskForm {

    private int id;

    @NonNull
    private String descrizione;

    @NonNull
    private String dataScadenza;

    @NonNull
    private String nome;

    @NonNull
    private String stato;

    @NonNull
    private int idPersona;

}
