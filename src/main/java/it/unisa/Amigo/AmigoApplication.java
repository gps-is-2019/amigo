package it.unisa.Amigo;

import it.unisa.Amigo.autenticazione.dao.RoleDAO;
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
	public CommandLineRunner demo(
			UserDAO userDAO
			, PasswordEncoder encoder
			, DipartimentoDAO dipartimentoDAO
			, SupergruppoDAO supergruppoDAO
			, PersonaDAO personaDAO
			, ConsiglioDidatticoDAO consiglioDidatticoDAO
			, RoleDAO roleDAO
	){
		return args -> {

			log.info("Creating two admin and user");

			Role adminRole = new Role(Role.ADMIN_ROLE);
			adminRole = roleDAO.save(adminRole);

			log.info("Creating some sites");
			log.info("----------");

			ConsiglioDidattico consiglioDidattico = new ConsiglioDidattico("Informatica");
			consiglioDidattico = consiglioDidatticoDAO.save(consiglioDidattico);

			ConsiglioDidattico consiglioDidatticoBoh = new ConsiglioDidattico("Ingegneria");
			consiglioDidatticoBoh = consiglioDidatticoDAO.save(consiglioDidatticoBoh);

			Dipartimento dipartimentoInf = new Dipartimento("informatica");
			dipartimentoInf = dipartimentoDAO.save(dipartimentoInf);
			log.info(dipartimentoInf.toString());

			Dipartimento dipartimentoAereo = new Dipartimento("Aereospaziale");
			dipartimentoAereo = dipartimentoDAO.save(dipartimentoAereo);
			log.info(dipartimentoInf.toString());

			//Supergruppo supergruppoInf = new Supergruppo(1,"supergruppoInf","gruppo", true);
			Supergruppo supergruppoInf = new Supergruppo("superguruppoInf","gruppo",true);
			supergruppoInf = supergruppoDAO.save(supergruppoInf);
			log.info(supergruppoInf.toString());

			Persona ferrucci = new Persona("filomena" , "ferrucci", "presidenteCdS");
			log.info(ferrucci.getNome(), ferrucci.getCognome());
			ferrucci.setDipartimento(dipartimentoInf);
			ferrucci.addSupergruppo(supergruppoInf);
			ferrucci.addConsiglioDidattico(consiglioDidattico);
			ferrucci = personaDAO.save(ferrucci);

			supergruppoDAO.save(supergruppoInf);

			User userFerrucci = new User("ferrucci@c9.it",encoder.encode("admin123"));
			userFerrucci.addRole(adminRole);

			userFerrucci.setPersona(ferrucci);

			userFerrucci = userDAO.save(userFerrucci);





			Persona scarano = new Persona("vittorio" , "scarano", "capogruppo");
			scarano.setDipartimento(dipartimentoInf);
			scarano.addSupergruppo(supergruppoInf);
			scarano.addConsiglioDidattico(consiglioDidattico);
			scarano = personaDAO.save(scarano);

			User userScarano = new User("scarano@c9.it",encoder.encode("admin123"));
			userScarano.addRole(adminRole);
			userScarano.setPersona(scarano);

			scarano.setUser(userScarano);

			userScarano = userDAO.save(userScarano);



//			Persona delfina = new Persona( "delfina" , "malandrino", "delegato");
//			delfina.addDipartimento(dipartimentoInf);
//			delfina.addSupergruppo(supergruppoInf);
//			delfina.addConsiglioDidattico(consiglioDidattico);
//			delfina = personaDAO.save(delfina);
//
//			User userDelfina = new User("delfina@c9.it",encoder.encode("admin123"));
//			userDelfina.addRole(adminRole);
//			userDelfina.setPersona(delfina);
//
//			delfina.setUser(userDelfina);
//
//			userDelfina = userDAO.save(userDelfina);
//
			//userDAO.saveAll(Arrays.asList(userFerrucci,userScarano,userDelfina));

			log.info("----------");
		};
	}



}
