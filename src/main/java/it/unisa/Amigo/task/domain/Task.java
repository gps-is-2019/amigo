package it.unisa.Amigo.task.domain;

import it.unisa.Amigo.consegna.domain.Documento;
import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    //@DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dataScadenza;

    @NonNull
    private String nome;

    @NonNull
    private String stato;

    private String stringData;


    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Supergruppo supergruppo;

    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Persona persona;

//    @OneToOne(cascade = CascadeType.ALL)
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private Documento documento;


}