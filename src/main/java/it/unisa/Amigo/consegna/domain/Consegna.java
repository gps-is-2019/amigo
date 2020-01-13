package it.unisa.Amigo.consegna.domain;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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

    private static final long serialVersionUID = 1L;
    public static final String PQA_LOCAZIONE = "PQA";
    public static final String NDV_LOCAZIONE = "NDV";
    public static final String USER_LOCAZIONE = "USER";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NonNull
    private LocalDate dataConsegna;

    @NonNull
    private String stato;

    @NonNull
    private String locazione;

    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Documento documento;

    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Persona mittente;

    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Persona destinatario;
}
