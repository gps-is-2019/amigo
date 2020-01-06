package it.unisa.Amigo.gruppo.domain;

import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.task.domain.Task;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Questa classe rappresenta l'oggetto di dominio "Persona"
 */
@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Persona implements Serializable {

    private final static long serialVersionUID = 48L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @NonNull
    private String nome;

    @NonNull
    private String cognome;

    @NonNull
    private String ruolo;


    @ManyToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Dipartimento> dipartimenti = new HashSet<>();


    @ManyToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Supergruppo> supergruppi = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ConsiglioDidattico> consigli = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Supergruppo> supergruppiResponsabile = new HashSet<>();

    public void addConsiglioDidatttico(ConsiglioDidattico consiglioDidattico) {
        if (!consigli.contains(consiglioDidattico)) {
            consigli.add(consiglioDidattico);
            consiglioDidattico.addPersona(this);
        }
    }

    public void addSupergruppo(Supergruppo supergruppo) {
        if (!supergruppi.contains(supergruppo)) {
            supergruppi.add(supergruppo);
            supergruppo.addPersona(this);
        }
    }

    public void addDipartimento(Dipartimento dipartimento) {
        if (!dipartimenti.contains(dipartimento)) {
            dipartimenti.add(dipartimento);
            dipartimento.addPersona(this);
        }
    }

    public void removeSupergruppo(Supergruppo supergruppo) {
        if (supergruppi.contains(supergruppo)) {
            supergruppi.remove(supergruppo);
            supergruppo.removePersona(this);
        }
    }

    public void addSupergruppoResponsabile(Supergruppo supergruppo) {
        if (!supergruppiResponsabile.contains(supergruppo)) {
            supergruppiResponsabile.add(supergruppo);
            supergruppo.setResponsabile(this);
        }
    }


    //TODO vedere relazione con task
    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Task> tasks = new HashSet<>();

    public void addTask(Task task){
        tasks.add(task);
    }
}
