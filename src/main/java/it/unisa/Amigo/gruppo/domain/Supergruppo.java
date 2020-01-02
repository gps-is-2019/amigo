package it.unisa.Amigo.gruppo.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Questa classe rappresenta l'oggetto di dominio "SuperGruppo"
 */
@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public class Supergruppo implements Serializable {

    private final static long serialVersionUID = 42L;
    @NonNull
    String name;
    @NonNull
    @Column(name = "type", updatable = false, insertable = false)
    String type;
    @NonNull
    Boolean state;
    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    ConsiglioDidattico consiglio;
    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Dipartimento dipartimento;
    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Persona responsabile;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    @ManyToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Persona> persone = new HashSet<>();

    public void addPersona(Persona persona) {
        if (!this.persone.contains(persona)) {
            persone.add(persona);
            persona.addSupergruppo(this);
        }
    }

    public void removePersona(Persona persona) {
        if (persone.contains(persona)) {
            persone.remove(persona);
            persona.removeSupergruppo(this);
        }
    }


}
