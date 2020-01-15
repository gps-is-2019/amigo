package it.unisa.Amigo.repository.controller;


import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domain.Role;
import it.unisa.Amigo.autenticazione.domain.User;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.repository.services.RepositoryService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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


    @ParameterizedTest
    @MethodSource("provideRepository")
    public void repository(User user) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        List<Documento> documenti = new ArrayList<>();
        when(repositoryService.searchDocumentInRepository("")).thenReturn(documenti);

        this.mockMvc.perform(get("/repository")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("flagPQA", 1))
                .andExpect(model().attribute("documenti", documenti))
                .andExpect(view().name("repository/repository"));
    }

    private static Stream<Arguments> provideRepository() {
        User user1 = new User("admin", "admin");
        User user2 = new User("admin1", "admin1");

        return Stream.of(
                Arguments.of(user1),
                Arguments.of(user2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUploadDocumento")
    public void uploadDocumento(User user, int status, String expectedViewName) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);

        this.mockMvc.perform(get("/repository/uploadDocumento")
                .with(user(userDetails)))
                .andExpect(status().is(status))
                .andExpect(view().name(expectedViewName));
    }
    private static Stream<Arguments> provideUploadDocumento() {
        User user1 = new User("admin", "admin");
        user1.addRole(new Role(Role.PQA_ROLE));
        User user2 = new User("admin1", "admin1");

        return Stream.of(
                Arguments.of(user1, 200,"repository/aggiunta_documento_repository")
               // Arguments.of(user2, 403,"403")
        );
    }

   /* @ParameterizedTest
    @MethodSource("provideUploadDocumento")
    public void uploadDocumento(User user, Persona expectedPersona) throws Exception {

        //User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        UserDetailImpl userDetails = new UserDetailImpl(user);
        //Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);

        this.mockMvc.perform(get("/repository/uploadDocumento")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("repository/aggiunta_documento_repository"));
    }

    private static Stream<Arguments> provideUploadDocumento() {
        User user4 = new User("Admin", "Admin");
        User user5 = new User(".", "pass");
        User user6 = new User("bounty", ".");

        Persona expectedPersona4 = new Persona(".", "123", "Administrator");
        Persona expectedPersona5 = new Persona("123", ".", "Administrator");
        Persona expectedPersona6 = new Persona("123", ".", ".");

        return Stream.of(
                Arguments.of(user4, expectedPersona4),
                Arguments.of(user5, expectedPersona5),
                Arguments.of(user6, expectedPersona6)
        );
    }

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
    }*/


}