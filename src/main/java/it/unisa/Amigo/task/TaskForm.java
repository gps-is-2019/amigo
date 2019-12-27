package it.unisa.Amigo.task;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class TaskForm {

    @NonNull
    private String descrizione;

    @NonNull
    private Date dataScadenza;

    @NonNull
    private String nome;

    @NonNull
    private String stato;

}