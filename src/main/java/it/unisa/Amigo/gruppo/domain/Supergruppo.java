package it.unisa.Amigo.gruppo.domain;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Supergruppo implements Serializable {

    private final static long serialVersionUID = 42L;

    @Id
    private int id;

    @NonNull
    String name;

    @NonNull
    String type;

    @NonNull
    boolean state;

    /*

    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Persona persona;

    */


}
