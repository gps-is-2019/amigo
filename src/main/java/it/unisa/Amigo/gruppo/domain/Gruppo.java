package it.unisa.Amigo.gruppo.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * La classe rappresenta l'oggetto di dominio "Gruppo"
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@DiscriminatorValue("Gruppo")
public class Gruppo extends Supergruppo implements Serializable {

    private final long serialVersionUID = 45L;

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Commissione> commissioni = new HashSet<>();

    public Gruppo(String nome, String tipo, boolean attivo){
        super(nome, tipo, attivo);
    }

    public void addCommissione(Commissione commissione){
        if(!this.commissioni.contains(commissione)) {
            commissioni.add(commissione);
            commissione.setGruppo(this);
        }
    }

}
