package it.unisa.Amigo.repository.controller;


import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.controller.GruppoFormCommand;
import it.unisa.Amigo.gruppo.dao.ConsiglioDidatticoDAO;
import it.unisa.Amigo.gruppo.dao.DipartimentoDAO;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.*;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.repository.services.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)

@SpringBootTest
@AutoConfigureMockMvc
public class RepositoryControllerIT {


    @Autowired
    private GruppoService gruppoService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PersonaDAO personaDAO;

    @Autowired
    private MockMvc mockMvc;





    @Test
    public void repository() throws Exception {

        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        personaDAO.save(expectedPersona);
        userDAO.save(user);

        repositoryService.addDocumentoInRepository(new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes()));
        List<Documento> expectedDocumenti = new ArrayList<>();
        expectedDocumenti = repositoryService.serarchDcoumentInRepository("");

        this.mockMvc.perform(get("/repository")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("documenti", expectedDocumenti))
                .andExpect(view().name("repository/repository"));
    }

    @Test
    public void uploadDocumento() throws Exception {

        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        personaDAO.save(expectedPersona);
        userDAO.save(user);

        this.mockMvc.perform(get("/repository/uploadDocumento")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("repository/aggiunta_documento_repository"));
    }

/*
    @Test
    public void downloadDocumento() throws Exception {

*/
}
