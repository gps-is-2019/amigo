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

    /**
     * Identificativo dell'oggetto Task.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * Breve descrizione dell'oggetto Task.
     */
    @NonNull
    private String descrizione;

    /**
     * Indica la data di Scadenze del relativo Task.
     */
    @NonNull
    private LocalDate dataScadenza; // cambiato in local date

    /**
     * Indica il nome del relativo Task.
     */
    @NonNull
    private String nome;

    /**
     * indica lo stato che può assumere il Task.
     */
    @NonNull
    private String stato;

    /**
     * Indica il supergruppo @{@link Supergruppo} del relativo Task.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Supergruppo supergruppo;

    /**
     * Indica il responsabile @{@link Persona} del relativo Task.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Persona persona;

    /**
     * Indica il documento @{@link Documento} del relativo Task.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Documento documento;
}