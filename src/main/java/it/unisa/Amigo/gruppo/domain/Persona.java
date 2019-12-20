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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @NonNull
    private String nome;

    @NonNull
    private String cognome;

    @NonNull
    private String ruolo;


    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Dipartimento dipartimento;


    @ManyToMany (cascade = CascadeType.MERGE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Supergruppo> supergruppo = new HashSet<>();

    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;



    @ManyToMany(cascade = CascadeType.MERGE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ConsiglioDidattico> consigli = new HashSet<>();

    public void addSupergruppo(Supergruppo s){
        this.supergruppo.add(s);
    }

//    public void addDipartimento(Dipartimento d){
//        dipartimento = d;
//    }

    public void addConsiglioDidattico(ConsiglioDidattico consiglio){ this.consigli.add(consiglio);}



}
