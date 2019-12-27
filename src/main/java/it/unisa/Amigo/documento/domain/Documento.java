package it.unisa.Amigo.documento.domain;

import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.task.domain.Task;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Documento implements Serializable {

    private final static long serialVersionUID = 48L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @NonNull
    private Date dataInvio;

    @NonNull
    private Date dataRicezione;

    @NonNull
    private String descrizione;

    @NonNull
    private String nome;

    @NonNull
    private String stato;

    @NonNull
    private boolean inRepository;

    @Lob
    private byte[] file;

    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Consegna consegna;

    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Task task;

}
