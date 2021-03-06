package it.unisa.Amigo.documento.domain;

import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.task.domain.Task;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Questa classe rappresenta l'oggetto di dominio "Documento"
 */
@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Documento implements Serializable {

    private static final long serialVersionUID = 48L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NonNull
    private String path;

    @NonNull
    private LocalDate dataInvio;

    @NonNull
    private String nome;

    @NonNull
    private boolean inRepository;

    @NonNull
    private String format;

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Consegna consegna;

    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Task task;
}
