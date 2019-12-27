package it.unisa.Amigo.gruppo.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class Commissione extends Supergruppo implements Serializable {

    private final long serialVersionUID = 17L;


    public Commissione(String name, String type, boolean state){
        super(name, type, state);
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
