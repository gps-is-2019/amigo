package it.unisa.Amigo;

import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domain.Role;
import it.unisa.Amigo.autenticazione.domain.User;
import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.Commissione;
import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Gruppo;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
public class AmigoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AmigoApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(
            final UserDAO userDAO,
            final PersonaDAO personaDAO,
            final ConsiglioDidatticoDAO consiglioDidatticoDAO,
            final SupergruppoDAO supergruppoDAO,
            final PasswordEncoder encoder,
            final TaskDAO taskDAO,
            final DocumentoDAO documentoDAO
    ) {
        return args -> {
            Role userRole = new Role(Role.USER_ROLE);
            Role pqaRole = new Role(Role.PQA_ROLE);
            Role capogruppoRole = new Role(Role.CAPOGRUPPO_ROLE);
            Role ndvRole = new Role(Role.NDV_ROLE);
            Role cpdsRole = new Role(Role.CPDS_ROLE);

            User userFerrucci = new User("ferrucci@unisa.it", encoder.encode("ferrucci"));
            userFerrucci.addRole(userRole);
            userFerrucci.addRole(capogruppoRole);

            User userScarano = new User("vitsca@unisa.it", encoder.encode("scarano"));
            userScarano.addRole(userRole);

            User userMalandrino = new User("dmalandrino@unisa.it", encoder.encode("malandrino"));
            userMalandrino.addRole(userRole);
            userMalandrino.addRole(ndvRole);

            User userDePrisco = new User("robdep@unisa.it", encoder.encode("dePrisco"));
            userDePrisco.addRole(userRole);
            userDePrisco.addRole(pqaRole);

            User userPolese = new User("gpolese@unisa.it", encoder.encode("polese"));
            userPolese.addRole(userRole);

            User userGravino = new User("gravino@unisa.it", encoder.encode("gravino"));
            userGravino.addRole(userRole);
            userGravino.addRole(cpdsRole);

            User userVincenzi = new User("vincenzi@unisa.it", encoder.encode("vincenzi"));
            userVincenzi.addRole(userRole);
            userVincenzi.addRole(ndvRole);

            User userRossi = new User("mariorossi@unisa.it", encoder.encode("mario"));
            userRossi.addRole(userRole);
            userRossi.addRole(cpdsRole);

            Persona ferrucci = new Persona("Filomena", "Ferrucci", "Professore Ordinario");
            Persona scarano = new Persona("Vittorio", "Scarano", "Professore Ordinario");
            Persona malandrino = new Persona("Delfina", "Malandrino", "Professore Associato");
            Persona dePrisco = new Persona("Roberto", "De Prisco", "Professore Ordinario");
            Persona polese = new Persona("Giuseppe", "Polese", "Professore Associato");
            Persona gravino = new Persona("Carmine", "Gravino", "Professore Associato");
            Persona rossi = new Persona("Mario", "Rossi", "Professore Associato");
            Persona vincenzi = new Persona("Giovanni", "Vincenzi", "Professore Associato");

            ferrucci.setUser(userFerrucci);
            userFerrucci.setPersona(ferrucci);

            scarano.setUser(userScarano);
            userScarano.setPersona(scarano);

            malandrino.setUser(userMalandrino);
            userMalandrino.setPersona(malandrino);

            dePrisco.setUser(userDePrisco);
            userDePrisco.setPersona(dePrisco);

            polese.setUser(userPolese);
            userPolese.setPersona(polese);

            gravino.setUser(userGravino);
            userGravino.setPersona(gravino);

            rossi.setUser(userRossi);
            userRossi.setPersona(rossi);

            vincenzi.setUser(userVincenzi);
            userVincenzi.setPersona(vincenzi);

            Commissione commissioneAAL = new Commissione("Accompagnamento al lavoro", "Commissione", true, "");
            Commissione commissioneEL = new Commissione("Piattaforme EL", "Commissione", true, "");

            Gruppo GAQD = new Gruppo("GAQD-Informatica", "Gruppo", true);
            GAQD.addPersona(scarano);
            GAQD.addPersona(ferrucci);
            GAQD.addPersona(dePrisco);
            GAQD.addPersona(malandrino);
            GAQD.addPersona(gravino);
            GAQD.addPersona(rossi);
            GAQD.addPersona(vincenzi);
            GAQD.setResponsabile(ferrucci);
            ferrucci.addSupergruppoResponsabile(GAQD);
            GAQD.addCommissione(commissioneAAL);
            GAQD.addCommissione(commissioneEL);

            commissioneAAL.addPersona(scarano);
            commissioneAAL.addPersona(malandrino);
            commissioneAAL.addPersona(dePrisco);
            commissioneAAL.setResponsabile(scarano);
            commissioneEL.addPersona(dePrisco);
            commissioneEL.addPersona(scarano);
            commissioneEL.addPersona(malandrino);
            commissioneEL.setResponsabile(dePrisco);

            ConsiglioDidattico cd = new ConsiglioDidattico("Informatica");
            cd.setSupergruppo(GAQD);
            GAQD.setConsiglio(cd);
            cd.addPersona(rossi);
            cd.addPersona(ferrucci);
            cd.addPersona(scarano);
            cd.addPersona(malandrino);
            cd.addPersona(dePrisco);
            cd.addPersona(polese);
            cd.addPersona(gravino);
            cd.addPersona(vincenzi);

            LocalDate tmpDate;
            tmpDate = LocalDate.now();

            Task taskprova = new Task("t1", tmpDate, "Approvare piani di studio", "in valutazione");
            taskprova.setPersona(ferrucci);
            ferrucci.addTask(taskprova);
            taskprova.setSupergruppo(GAQD);
            GAQD.addTask(taskprova);

            Task taskprova2 = new Task("t2", tmpDate, "Approvare richieste cambio classe", "approvato");
            taskprova2.setPersona(scarano);
            scarano.addTask(taskprova2);
            taskprova2.setSupergruppo(GAQD);
            GAQD.addTask(taskprova2);

            Task taskprova3 = new Task("t3", tmpDate, "Definire date di esame", "incompleto");
            taskprova3.setPersona(ferrucci);
            ferrucci.addTask(taskprova3);
            taskprova3.setSupergruppo(GAQD);
            GAQD.addTask(taskprova3);

            Task taskprova4 = new Task("task approvato 1", tmpDate, "Convalidare esami di inglese", "approvato");
            taskprova4.setPersona(ferrucci);
            ferrucci.addTask(taskprova4);
            taskprova4.setSupergruppo(GAQD);
            GAQD.addTask(taskprova4);

            Documento documento = new Documento("src/main/resources/inputFile/modulocambioclasse.pdf", tmpDate, "modulocambioclasse.pdf", false, "application/pdf");
            documento.setTask(taskprova2);
            taskprova2.setDocumento(documento);

            Documento documento2 = new Documento("src/main/resources/inputFile/Sistema-AQ.pdf", tmpDate, "Sistema-AQ.pdf", true, "application/pdf");
            supergruppoDAO.save(GAQD);
            supergruppoDAO.save(commissioneAAL);
            supergruppoDAO.save(commissioneEL);
            consiglioDidatticoDAO.save(cd);
            personaDAO.saveAll(Arrays.asList(ferrucci, scarano, malandrino, dePrisco, polese, gravino, rossi, vincenzi));
            userDAO.saveAll(Arrays.asList(userFerrucci, userScarano, userMalandrino, userDePrisco, userPolese, userGravino, userRossi, userVincenzi));
            taskDAO.saveAll(Arrays.asList(taskprova, taskprova2, taskprova3, taskprova4));
            documentoDAO.saveAll(Arrays.asList(documento, documento2));
        };
    }
}
