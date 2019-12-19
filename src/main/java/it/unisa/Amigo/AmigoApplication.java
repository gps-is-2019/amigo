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
	public CommandLineRunner demo(
			UserDAO userDAO
			, PasswordEncoder encoder
			, DipartimentoDAO dipartimentoDAO
			, SupergruppoDAO supergruppoDAO
			, PersonaDAO personaDAO
			, ConsiglioDidatticoDAO consiglioDidatticoDAO
	){
		return args -> {

			log.info("Creating two admin and user");

			Role adminRole = new Role(Role.ADMIN_ROLE);
			Role userRole = new Role(Role.USER_ROLE);


			User admin = new User("admin@c9.it",encoder.encode("admin123"));
			admin.addRole(adminRole);

			User user = new User("magig@c9.it",encoder.encode("magi123"));
			user.addRole(userRole);

			userDAO.saveAll(Arrays.asList(user,admin));
			log.info("Saved {} user",user);
			log.info("Saved {} user",admin);

			log.info("Creating some sites");
			log.info("----------");

			/*
			Site siteWithResposible = new Site("Costiera Amalfitana", LocalDate.ofYearDay(1997, 1), Site.TYPE_CULTURAL, "Campania");
			Responsible responsible = new Responsible("Giovanni","Magi");
			siteWithResposible.addResponsible(responsible);

			siteDao.save(siteWithResposible);
			siteDao.save(new Site("Isole Eolie",LocalDate.ofYearDay(2000,1),Site.TYPE_NATURAL,"Sicilia"));
			siteDao.save(new Site("Ville e Giardini Medicei",LocalDate.ofYearDay(2013,1),Site.TYPE_MIXED,"Toscana"));

			log.info("Reading all sites of type {}",Site.TYPE_NATURAL);

			siteDao.findAllByType(Site.TYPE_NATURAL).forEach(site -> {
				log.info("{}",site);
			});

			*/

			ConsiglioDidattico consiglioDidattico = new ConsiglioDidattico(1, "Informatica");
			consiglioDidatticoDAO.save(consiglioDidattico);

			ConsiglioDidattico consiglioDidatticoBoh = new ConsiglioDidattico(2, "Ingegneria");
			consiglioDidatticoDAO.save(consiglioDidatticoBoh);

			Dipartimento dipartimentoInf = new Dipartimento(1,"informatica");
			dipartimentoDAO.save(dipartimentoInf);
			log.info(dipartimentoInf.toString());

			Dipartimento dipartimentoAereo = new Dipartimento(2,"Aereospaziale");
			dipartimentoDAO.save(dipartimentoAereo);
			log.info(dipartimentoInf.toString());

			//Supergruppo supergruppoInf = new Supergruppo(1,"supergruppoInf","gruppo", true);
			Supergruppo supergruppoInf = new Supergruppo(1,"superguruppoInf","gruppo",true);
			supergruppoDAO.save(supergruppoInf);
			log.info(supergruppoInf.toString());

			Persona ferrucci = new Persona(111 , "filomena" , "ferrucci", "presidenteCdS");
			log.info(ferrucci.getNome(), ferrucci.getCognome());
			ferrucci.addDipartimento(dipartimentoAereo);
			ferrucci.addSupergruppo(supergruppoInf);
			ferrucci.addConsiglioDidattico(consiglioDidatticoBoh);
			personaDAO.save(ferrucci);

			Persona scarano = new Persona(222 , "vittorio" , "scarano", "capogruppo");
			scarano.addDipartimento(dipartimentoInf);
			scarano.addSupergruppo(supergruppoInf);
			scarano.addConsiglioDidattico(consiglioDidattico);
			personaDAO.save(scarano);

			Persona delfina = new Persona(333 , "delfina" , "malandrino", "delegato");
			delfina.addDipartimento(dipartimentoInf);
			delfina.addSupergruppo(supergruppoInf);
			delfina.addConsiglioDidattico(consiglioDidattico);
			personaDAO.save(delfina);



			log.info("----------");
		};
	}



}
