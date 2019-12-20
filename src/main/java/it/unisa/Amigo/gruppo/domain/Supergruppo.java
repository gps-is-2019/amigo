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
    boolean state;



    @ManyToMany
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Persona> persona = new HashSet<>();



}
