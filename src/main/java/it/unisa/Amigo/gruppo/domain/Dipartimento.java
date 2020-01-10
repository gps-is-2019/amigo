package it.unisa.Amigo.gruppo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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

    private static final long serialVersionUID = 41L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NonNull
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ConsiglioDidattico> consiglioDidattico = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Supergruppo supergruppoGAQR;

    @ManyToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Persona> persone = new HashSet<>();

    public void addPersona(final Persona persona) {
        if (!persone.contains(persona)) {
            persone.add(persona);
            persona.addDipartimento(this);
        }
    }

}
