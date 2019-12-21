package it.unisa.Amigo;

import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.DipartimentoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.ConsiglioDidattico;
import it.unisa.Amigo.gruppo.domain.Dipartimento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
public class AmigoApplication {

	private static final Logger log = LoggerFactory.getLogger(AmigoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AmigoApplication.class, args);
	}

		@Bean
		public CommandLineRunner demo(UserDAO userDAO , PersonaDAO personaDAO, ConsiglioDidatticoDAO consiglioDidatticoDAO, SupergruppoDAO supergruppoDAO, PasswordEncoder encoder){
			return args -> {

				log.info("Creating two admin and user");

				Role adminRole = new Role(Role.ADMIN_ROLE);
				Role userRole = new Role(Role.USER_ROLE);


				User admin = new User("admin@c9.it",encoder.encode("admin123"));
				admin.addRole(adminRole);

				User userArmando = new User("armando@gmail.it",encoder.encode("magi123"));
				userArmando.addRole(userRole);

				User userMario = new User("mario@gmail.it.it",encoder.encode("magi123"));
				userMario.addRole(userRole);

				User userGiovanni = new User("giovanni@gmail.it.it",encoder.encode("magi123"));
				userGiovanni.addRole(userRole);

				User userRaffaele = new User("raffaele@gmail.it.it",encoder.encode("magi123"));
				userRaffaele.addRole(userRole);

				User userMarco = new User("marco@gmail.it.it",encoder.encode("magi123"));
				userMarco.addRole(userRole);

				User userAntonio = new User("antonio@gmail.it.it",encoder.encode("magi123"));
				userAntonio.addRole(userRole);

				Persona mario = new Persona("Mario","Inglese","ciao");
				Persona armando = new Persona("Armando","Conte","ciao");
				Persona giovanni = new Persona("Giovanni","Bello","ciao");
				Persona raffaele = new Persona("Raffaele","Magi","ciao");
				Persona marco = new Persona("Marco","De Stefano","ciao");
				Persona antonio = new Persona("Antonio","Lodato","ciao");

				mario.setUser(userMario);
				armando.setUser(userArmando);
				giovanni.setUser(userGiovanni);
				raffaele.setUser(userRaffaele);
				marco.setUser(userMarco);
				antonio.setUser(userAntonio);

				Supergruppo GAQD = new Supergruppo( "GAQD-Informatica","gruppo",true );
				GAQD.addPersona(armando);


				ConsiglioDidattico cd = new ConsiglioDidattico("Informatica");
				cd.setSupergruppo(GAQD);
				cd.addPersona(mario);
				cd.addPersona(armando);
				cd.addPersona(giovanni);
				cd.addPersona(raffaele);
				cd.addPersona(marco);
				cd.addPersona(antonio);





				supergruppoDAO.save(GAQD);
				consiglioDidatticoDAO.save(cd);
				personaDAO.saveAll(Arrays.asList(mario,armando,giovanni,raffaele,marco,antonio));
				userDAO.saveAll(Arrays.asList(userArmando,userMario,userGiovanni,userRaffaele,userMarco,userAntonio,admin));

				/*log.info("Saved {} persona",mario);
				log.info("Saved {} persona",armando);
				log.info("Saved {} persona",giovanni);
				log.info("Saved {} persona",raffaele);
				log.info("Saved {} persona",marco);
				log.info("Saved {} persona",antonio);*/
				log.info("Saved {} consiglioDidattico",cd);
				log.info("Saved {} supergruppo",GAQD);


				/*log.info("Saved {} user",userArmando);
				log.info("Saved {} user",userMario);
				log.info("Saved {} user",userGiovanni);
				log.info("Saved {} user",userRaffaele);
				log.info("Saved {} user",userMarco);
				log.info("Saved {} user",userAntonio);
				log.info("Saved {} user",admin);*/

				log.info("----------");
			};
		}
	}



