package it.unisa.Amigo.gruppo.domain;

import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Gruppo extends Supergruppo {

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Commissione> commissioni = new HashSet<>();

    public void addCommissione(Commissione commissione){
        if (!this.commissioni.contains(commissione)){
            commissioni.add(commissione);
            commissione.setGruppo(this);
        }
    }

    public void removeCommissione(Commissione commissione){
        if (!this.commissioni.contains(commissione)){
            commissioni.remove(commissione);
            commissione.setGruppo(null);
        }
    }


}
