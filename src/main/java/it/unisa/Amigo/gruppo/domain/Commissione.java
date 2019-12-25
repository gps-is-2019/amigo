package it.unisa.Amigo.gruppo.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Commissione extends Supergruppo {

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Gruppo gruppo;

}
