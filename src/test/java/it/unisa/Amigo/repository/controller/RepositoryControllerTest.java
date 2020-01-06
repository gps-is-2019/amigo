package it.unisa.Amigo.repository.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.*;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.repository.services.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RepositoryControllerTest {

    @MockBean
    private GruppoService gruppoService;

    @MockBean
    private RepositoryService repositoryService;

    @Mock
    private Resource resource;

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void repository() throws Exception {

        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        List<Documento> documenti = new ArrayList<>();
        when(repositoryService.searchDocumentInRepository("")).thenReturn(documenti);


        this.mockMvc.perform(get("/repository")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("flagPQA", 1))
                .andExpect(model().attribute("documenti", documenti))
                .andExpect(view().name("repository/repository"));
    }


    @Test
    public void uploadDocumento() throws Exception {

        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);

        this.mockMvc.perform(get("/repository/uploadDocumento")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("repository/aggiunta_documento_repository"));
    }
/*
    @Test
    public void downloadDocumento() throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        Documento expectedDocumento = new Documento("src/main/resources/documents/dip.pdf", LocalDate.now(),
                "dip.pdf", false, "application/pdf");
        Resource resource = new UrlResource(Paths.get(expectedDocumento.getPath()).toUri());
        when(repositoryService.findDocumento(0)).thenReturn(expectedDocumento);
        when(repositoryService.downloadDocumento(expectedDocumento)).thenReturn(resource);


        String actualString = this.mockMvc.perform(get("/repository/{idDocument}", 0)
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8);
        String expectedString = FileCopyUtils.copyToString(reader);

        System.out.println(expectedString);
        assertEquals(actualString,expectedString);
    }

*/
}