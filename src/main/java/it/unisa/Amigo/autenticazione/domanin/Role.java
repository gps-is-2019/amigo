package it.unisa.Amigo.autenticazione.domanin;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Questa classe modella l'oggetto ruolo il quale definisce due ruoli: ADMIN e USER
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class Role {

    private final static long serialVersionUID = 1L;
    public final static  String ADMIN_ROLE = "ADMIN";
    public final static  String CAPOGRUPPO_ROLE = "CAPOGRUPPO";
    public final static  String USER_ROLE = "USER";
    public final static  String PQA_ROLE = "PQA";
    public final static  String CPDS_ROLE = "CPDS";
    public final static  String NDV_ROLE = "NDV";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NonNull
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "roles")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> users = new HashSet<>();
}