package it.unisa.Amigo.task.domain;


import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import lombok.NonNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.CascadeType;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Questa classe rappresenta l'oggetto di dominio "Task".
 */
@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Task implements Serializable {

    private final static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @NonNull
    private String descrizione;

    @NonNull
    //@DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataScadenza; // cambiato in local date

    @NonNull
    private String nome;

    @NonNull
    private String stato;


    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Supergruppo supergruppo;

    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Persona persona;

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Documento documento;
}