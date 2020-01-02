package it.unisa.Amigo;

import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.gruppo.dao.*;
import it.unisa.Amigo.gruppo.domain.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@SpringBootApplication
public class AmigoApplication {
	public static void main(String[] args) {
		SpringApplication.run(AmigoApplication.class, args);
	}

		@Bean
		public CommandLineRunner demo( UserDAO userDAO , PersonaDAO personaDAO, ConsiglioDidatticoDAO consiglioDidatticoDAO, SupergruppoDAO supergruppoDAO, PasswordEncoder encoder){
			return args -> {

				Role userRole = new Role(Role.USER_ROLE);

				User userFerrucci = new User("ferrucci@unisa.it",encoder.encode("ferrucci"));
				userFerrucci.addRole(userRole);

				User userScarano = new User("vitsca@unisa.it",encoder.encode("scarano"));
				userScarano.addRole(userRole);

				User userMalandrino = new User("dmalandrino@unisa.it",encoder.encode("malandrino"));
				userMalandrino.addRole(userRole);

				User userDePrisco= new User("robdep@unisa.it",encoder.encode("dePrisco"));
				userDePrisco.addRole(userRole);

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

				supergruppoDAO.save(GAQD);
				supergruppoDAO.save(commissioneAAL);
				supergruppoDAO.save(commissioneEL);
				consiglioDidatticoDAO.save(cd);
				personaDAO.saveAll(Arrays.asList(ferrucci,scarano,malandrino,dePrisco,polese,gravino));
				userDAO.saveAll(Arrays.asList(userFerrucci,userScarano,userMalandrino,userDePrisco,userPolese,userGravino));
			};
		}
	}



