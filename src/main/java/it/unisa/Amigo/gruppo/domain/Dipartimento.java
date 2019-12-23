package it.unisa.Amigo.gruppo.domain;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Questa classe rappresenta l'oggetto di dominio "Dipartimento"
 */
@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Dipartimento implements Serializable {

    private final static long serialVersionUID = 41L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @NonNull
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set <ConsiglioDidattico> consiglioDidattico = new HashSet<>();;

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Supergruppo supergruppoGAQR;

    @ManyToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Persona> persone = new HashSet<>();

    public void addPersona(Persona persona){
        if(!persone.contains(persona)) {
            persone.add(persona);
            persona.addDipartimento(this);
        }
    }

}
