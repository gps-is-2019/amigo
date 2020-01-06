package it.unisa.Amigo.consegna.domain;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * La classe rappresenta l'oggetto di dominio "Consegna"
 */
@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Consegna implements Serializable {

    private final static long serialVersionUID = 1L;
    public final static String PQA_LOCAZIONE = "PQA";
    public final static String NDV_LOCAZIONE = "NDV";
    public final static String USER_LOCAZIONE = "USER";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @NonNull
    private LocalDate dataConsegna;

    @NonNull
    private String stato;

    @NonNull
    private String locazione;

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Documento documento;

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Persona mittente;

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Persona destinatario;
}