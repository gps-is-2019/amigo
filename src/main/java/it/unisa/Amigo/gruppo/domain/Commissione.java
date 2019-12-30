package it.unisa.Amigo.gruppo.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;


/**
 * La classe rappresenta l'oggetto di dominio "Commissione"
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@DiscriminatorValue("Commissione")
public class Commissione extends Supergruppo implements Serializable {

    private final long serialVersionUID = 17L;
    private String descrizione;

    public Commissione(String name, String type, boolean state, String descrizione){
        super(name, type, state);
        this.descrizione = descrizione;
    }

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Gruppo gruppo;

    public void setGruppo( Gruppo gruppo){
        gruppo.addCommissione(this);
        this.gruppo = gruppo;
    }

}
