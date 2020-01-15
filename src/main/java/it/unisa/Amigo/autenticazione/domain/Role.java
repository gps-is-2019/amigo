package it.unisa.Amigo.autenticazione.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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

    private static final long serialVersionUID = 1L;
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String CAPOGRUPPO_ROLE = "CAPOGRUPPO";
    public static final String USER_ROLE = "USER";
    public static final String PQA_ROLE = "PQA";
    public static final String CPDS_ROLE = "CPDS";
    public static final  String NDV_ROLE = "NDV";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @EqualsAndHashCode.Exclude
    private Long id;

    @NonNull
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "roles")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> users = new HashSet<>();
}
