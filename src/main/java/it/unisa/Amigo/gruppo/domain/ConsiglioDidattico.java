package it.unisa.Amigo.gruppo.domain;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ConsiglioDidattico implements Serializable
{
    private final static long serialVersionUID = 40L;

    @Id
    private int id;

    @NonNull
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Dipartimento dipartimento;

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Supergruppo supergruppoGAQD;



}
