package it.unisa.Amigo.gruppo.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
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
        System.out.println("Commissioni presenti alla chiamata di add commissione: ");
        for(Commissione c : commissioni){
            System.out.println(c.getName());
        }
        System.out.println("La commissione: " + commissione.getId() + " esiste nel gruppo :" + this.getName() + "? " + this.commissioni.contains(commissione)+"\n\n");
        if(!this.commissioni.contains(commissione)) {
            commissioni.add(commissione);
            commissione.setGruppo(this);
        }
    }

    public void removeCommissione(Commissione commissione){
        if (this.commissioni.contains(commissione)){
            commissioni.remove(commissione);
            commissione.setGruppo(null);
        }
    }


}
