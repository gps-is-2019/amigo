package it.unisa.Amigo;

import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Date;


@SpringBootApplication
public class AmigoApplication {
	public static void main(String[] args) {
		SpringApplication.run(AmigoApplication.class, args);
	}

		@Bean
		public CommandLineRunner demo(
				UserDAO userDAO
				, PersonaDAO personaDAO
				, ConsiglioDidatticoDAO consiglioDidatticoDAO
				, SupergruppoDAO supergruppoDAO
				, PasswordEncoder encoder
				, TaskDAO taskDAO
		){
			return args -> {

				Role userRole = new Role(Role.USER_ROLE);
				Role pqaRole = new Role(Role.PQA_ROLE);

				User userFerrucci = new User("ferrucci@unisa.it",encoder.encode("ferrucci"));
				userFerrucci.addRole(userRole);

				User userScarano = new User("vitsca@unisa.it",encoder.encode("scarano"));
				userScarano.addRole(userRole);

				User userMalandrino = new User("dmalandrino@unisa.it",encoder.encode("malandrino"));
				userMalandrino.addRole(userRole);

				User userDePrisco= new User("robdep@unisa.it",encoder.encode("dePrisco"));
				userDePrisco.addRole(userRole);
				userDePrisco.addRole(pqaRole);

				User userPolese = new User("gpolese@unisa.it",encoder.encode("polese"));
				userPolese.addRole(userRole);

				User userGravino = new User("gravino@unisa.it",encoder.encode("gravino"));
				userGravino.addRole(userRole);

				//TODO ho cambiato il ruolo per testare la pagina : ruolo ferrucci da Professore Ordinario a capogruppo
				Persona ferrucci = new Persona("Filomena","Ferrucci","capogruppo");
				Persona scarano = new Persona("Vittorio","Scarano","Professore Ordinario");
				Persona malandrino = new Persona("Delfina","Malandrino","Professore Associato");
				Persona dePrisco = new Persona("Roberto","De Prisco","Professore Ordinario");
				Persona polese = new Persona("Giuseppe","Polese","Professore Associato");
				Persona gravino = new Persona("Carmine","Gravino","Professore Associato");

				ferrucci.setUser(userFerrucci);
				scarano.setUser(userScarano);
				malandrino.setUser(userMalandrino);
				dePrisco.setUser(userDePrisco);
				polese.setUser(userPolese);
				gravino.setUser(userGravino);

				Supergruppo GAQD = new Supergruppo( "GAQD-Informatica","Gruppo",true );
				GAQD.addPersona(scarano);
				GAQD.addPersona(ferrucci);
				GAQD.setResponsabile(ferrucci);

				ConsiglioDidattico cd = new ConsiglioDidattico("Informatica");
				cd.setSupergruppo(GAQD);
				GAQD.setConsiglio(cd);
				cd.addPersona(ferrucci);
				cd.addPersona(scarano);
				cd.addPersona(malandrino);
				cd.addPersona(dePrisco);
				cd.addPersona(polese);
				cd.addPersona(gravino);



				//TODO add task

				Task taskprova = new Task("t1" , new Date(), "task1" , "in valutazione");

				taskprova.setPersona(ferrucci);
				ferrucci.addTask(taskprova);

				taskprova.setSupergruppo(GAQD);
				GAQD.addTask(taskprova);


				Task taskprova2 = new Task("t2" , new Date() , "task2" , "approvato");
				taskprova2.setPersona(scarano);
				scarano.addTask(taskprova2);

				taskprova2.setSupergruppo(GAQD);
				GAQD.addTask(taskprova2);

				//

				supergruppoDAO.save(GAQD);
				consiglioDidatticoDAO.save(cd);
				personaDAO.saveAll(Arrays.asList(ferrucci,scarano,malandrino,dePrisco,polese,gravino));
				userDAO.saveAll(Arrays.asList(userFerrucci,userScarano,userMalandrino,userDePrisco,userPolese,userGravino));

				///////// TODO add taskDAO

				taskDAO.saveAll(Arrays.asList(taskprova,taskprova2));







			};
		}
	}



