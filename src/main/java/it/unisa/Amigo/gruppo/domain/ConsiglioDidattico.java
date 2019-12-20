package it.unisa.Amigo.gruppo.domain;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ConsiglioDidattico implements Serializable
{
    private final static long serialVersionUID = 40L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @NonNull
    private String name;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Dipartimento dipartimento;

    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Supergruppo supergruppoGAQD;

    @ManyToMany
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Persona> persone;




}
