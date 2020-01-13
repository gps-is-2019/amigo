package it.unisa.Amigo.gruppo.domain;

import it.unisa.Amigo.task.domain.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Questa classe rappresenta l'oggetto di dominio "SuperGruppo"
 */
@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public class Supergruppo implements Serializable {

    private static final long serialVersionUID = 42L;
    @NonNull
    private String name;
    @NonNull
    @Column(name = "type", updatable = false, insertable = false)
    private String type;
    @NonNull
    private Boolean state;
    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ConsiglioDidattico consiglio;
    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Dipartimento dipartimento;
    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Persona responsabile;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @ManyToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Persona> persone = new HashSet<>();

    public void addPersona(final Persona persona) {
        if (!this.persone.contains(persona)) {
            persone.add(persona);
            persona.addSupergruppo(this);
        }
    }

    public void removePersona(final Persona persona) {
        if (persone.contains(persona)) {
            persone.remove(persona);
            persona.removeSupergruppo(this);
        }
    }

    //TODO da vedere relazione con task

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Task> tasks = new HashSet<>();

    public void addTask(final Task task) {
        tasks.add(task);
    }

}
