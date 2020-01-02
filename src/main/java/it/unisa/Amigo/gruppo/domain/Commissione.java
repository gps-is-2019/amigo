package it.unisa.Amigo.gruppo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Gruppo gruppo;

    public Commissione(String name, String type, boolean state, String descrizione) {
        super(name, type, state);
        this.descrizione = descrizione;
    }

    public void setGruppo(Gruppo gruppo) {
        gruppo.addCommissione(this);
        this.gruppo = gruppo;
    }

}
