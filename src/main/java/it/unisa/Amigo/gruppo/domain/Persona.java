package it.unisa.Amigo.gruppo.domain;

import it.unisa.Amigo.autenticazione.domanin.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Persona implements Serializable {

    private final static long serialVersionUID = 43L;

    @NonNull
    @Id
    private  int id;

    @NonNull
    private String nome;

    @NonNull
    private String cognome;

    @NonNull
    private String ruolo;


    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Dipartimento dipartimento;


    @ManyToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Supergruppo> supergruppo = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user = new User();

    public void addSupergruppo(Supergruppo s){
        this.supergruppo.add(s);
    }

    public void addDipartimento(Dipartimento d){
        dipartimento = d;
    }



}
