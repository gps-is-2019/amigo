package it.unisa.Amigo.gruppo.domain;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Supergruppo implements Serializable {

    private final static long serialVersionUID = 42L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @NonNull
    String name;

    @NonNull
    String type;

    @NonNull
    Boolean state;

    @ManyToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Persona> persone = new HashSet<>();

    public void addPersona(Persona persona){
        if(!persone.contains(persona)){
            persone.add(persona);
            persona.addSupergruppo(this);
        }
    }



}
