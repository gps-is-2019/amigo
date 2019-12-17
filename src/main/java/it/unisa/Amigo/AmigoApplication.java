package it.unisa.Amigo;

import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
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
	public CommandLineRunner demo(UserDAO userDAO , PasswordEncoder encoder){
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


			log.info("----------");
		};
	}



}
