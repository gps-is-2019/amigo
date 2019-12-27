package it.unisa.Amigo.task.domain;


import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Questa classe rappresenta l'oggetto di dominio "Dipartimento"
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
    private Date dataScadenza;

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