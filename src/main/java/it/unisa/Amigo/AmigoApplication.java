package it.unisa.Amigo;

import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.*;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
				, DocumentoDAO documentoDAO
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

				Persona ferrucci = new Persona("Filomena","Ferrucci","Capogruppo");
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

				Commissione commissioneAAL = new Commissione("Accompagnamento al lavoro", "Commissione", true, "");
				Commissione commissioneEL = new Commissione("Piattaforme EL", "Commissione", true, "");



				Gruppo GAQD = new Gruppo( "GAQD-Informatica","Gruppo",true );
				GAQD.addPersona(scarano);
				GAQD.addPersona(ferrucci);
				GAQD.addPersona(dePrisco);
				GAQD.addPersona(malandrino);
				GAQD.addPersona(gravino);
				GAQD.setResponsabile(ferrucci);
				GAQD.addCommissione(commissioneAAL);
				GAQD.addCommissione(commissioneEL);

				commissioneAAL.addPersona(scarano);
				commissioneAAL.addPersona(malandrino);
				commissioneAAL.setResponsabile(scarano);
				commissioneAAL.addPersona(dePrisco);
				commissioneEL.addPersona(dePrisco);
				commissioneEL.setResponsabile(dePrisco);

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
//				Date tmpDate;
//				SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
//				tmpDate = formatter.parse("1-1-2019");
				LocalDate tmpDate;
				tmpDate = LocalDate.of(2020, 4, 20);

				Task taskprova = new Task("t1" , tmpDate, "task1" , "in valutazione");

				taskprova.setPersona(ferrucci);
				ferrucci.addTask(taskprova);

				taskprova.setSupergruppo(GAQD);
				GAQD.addTask(taskprova);


				Task taskprova2 = new Task("t2" , tmpDate , "task2" , "approvato");
				taskprova2.setPersona(scarano);
				scarano.addTask(taskprova2);

				taskprova2.setSupergruppo(GAQD);
				GAQD.addTask(taskprova2);

				Task taskprova3 = new Task("t3" , tmpDate , "task3" , "incompleto");
				taskprova3.setPersona(ferrucci);
				ferrucci.addTask(taskprova3);

				taskprova3.setSupergruppo(GAQD);
				GAQD.addTask(taskprova3);

				Task taskprova4 = new Task("task approvato 1" , tmpDate , "task approvato1" , "approvato");
				taskprova4.setPersona(ferrucci);
				ferrucci.addTask(taskprova4);

				taskprova4.setSupergruppo(GAQD);
				GAQD.addTask(taskprova4);


				Documento documento = new Documento("src/main/resources/documents/test.txt" , tmpDate , "test",  false , ""  );
				taskprova4.setDocumento(documento);
				documento.setTask(taskprova4);

				Task taskprova5 = new Task("task approvato 2" , tmpDate , "taskapprovato2" , "approvato");
				taskprova5.setPersona(ferrucci);
				ferrucci.addTask(taskprova5);

				taskprova5.setSupergruppo(GAQD);
				GAQD.addTask(taskprova5);

				Documento documento2 = new Documento("src/main/resources/documents/gestioneaccount.png" , tmpDate , "gestioneaccount.png",  false , ".png"  );
				taskprova5.setDocumento(documento2);
				documento2.setTask(taskprova5);


				supergruppoDAO.save(GAQD);
				supergruppoDAO.save(commissioneAAL);
				supergruppoDAO.save(commissioneEL);
				consiglioDidatticoDAO.save(cd);
				personaDAO.saveAll(Arrays.asList(ferrucci,scarano,malandrino,dePrisco,polese,gravino));
				userDAO.saveAll(Arrays.asList(userFerrucci,userScarano,userMalandrino,userDePrisco,userPolese,userGravino));
				taskDAO.saveAll(Arrays.asList(taskprova,taskprova2,taskprova3,taskprova4,taskprova5));
				documentoDAO.saveAll(Arrays.asList(documento,documento2));
			};
		}
	}



