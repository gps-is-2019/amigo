package it.unisa.Amigo.repository.controller;


import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domain.Role;
import it.unisa.Amigo.autenticazione.domain.User;
import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.repository.services.RepositoryService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@AutoConfigureMockMvc
public class RepositoryControllerIT {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PersonaDAO personaDAO;

    @Autowired
    private DocumentoDAO documentoDAO;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void afterEach() {
        documentoDAO.deleteAll();
        personaDAO.deleteAll();
        userDAO.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideRepository")
    public void repository(User user) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        List<Documento> documenti = repositoryService.searchDocumentInRepository("");
        userDAO.save(user);

        this.mockMvc.perform(get("/repository")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("flagPQA", false))
                .andExpect(model().attribute("documenti", documenti))
                .andExpect(view().name("repository/repository"));
    }

    private static Stream<Arguments> provideRepository() {
        User user1 = new User("admin", "admin");
        return Stream.of(
                Arguments.of(user1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUploadDocumento")
    public void uploadDocumento(User user, int status, String expectedViewName) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        userDAO.save(user);
        this.mockMvc.perform(get("/repository/uploadDocumento")
                .with(user(userDetails)))
                .andExpect(status().is(status))
                .andExpect(view().name(expectedViewName));
    }

    private static Stream<Arguments> provideUploadDocumento() {
        User user1 = new User("admin", "admin");
        user1.addRole(new Role(Role.PQA_ROLE));


        return Stream.of(
                Arguments.of(user1, 200, "repository/aggiunta_documento_repository")
        );
    }

    @ParameterizedTest
    @MethodSource("provideUploadDocumentoPost")
    void uploadDocumento(User user, MockMultipartFile file, boolean flag, String nameModel, String contentModel) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        int expectedRepositorySize = flag ? 1 : 0;

        this.mockMvc.perform(multipart("/repository/uploadDocumento").file(file)
                .with(csrf())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("addFlag", flag))
                .andExpect(model().attribute(nameModel, contentModel))
                .andExpect(model().attribute("flagPQA", true))
                .andExpect(model().attribute("documenti", hasSize(expectedRepositorySize)))
                .andExpect(view().name("repository/repository"));
    }

    private static Stream<Arguments> provideUploadDocumentoPost() {
        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[1]);
        MockMultipartFile file2 = new MockMultipartFile("file", "test.txt", "application/pdf", new byte[1]);
        MockMultipartFile file3 = new MockMultipartFile("file", "test.zip", "application/pdf", new byte[1]);
        MockMultipartFile file4 = new MockMultipartFile("file", "test.rar", "application/pdf", new byte[1]);
        MockMultipartFile file5 = new MockMultipartFile("file", "test.exe", "application/pdf", new byte[1]);
        MockMultipartFile file6 = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[0]);
        MockMultipartFile file7 = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[10485761]);

        return Stream.of(
                Arguments.of(user, file, true, "documentoNome", file.getOriginalFilename()),
                Arguments.of(user, file2, true, "documentoNome", file2.getOriginalFilename()),
                Arguments.of(user, file3, true, "documentoNome", file3.getOriginalFilename()),
                Arguments.of(user, file4, true, "documentoNome", file4.getOriginalFilename()),
                Arguments.of(user, file5, false, "errorMessage", "Formato del file non supportato"),
                Arguments.of(user, file6, false, "errorMessage", "Dimensioni del file non supportate"),
                Arguments.of(user, file7, false, "errorMessage", "Dimensioni del file non supportate")

        );
    }

    @ParameterizedTest
    @MethodSource("provideDownloadDocumento")
    void downloadDocumentoNull(User user, Documento documento) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        documentoDAO.save(documento);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "https://i.makeagif.com/media/6-18-2016/i4va3h.gif");
        ResponseEntity<Resource> expectedResponse = new ResponseEntity<>(headers, HttpStatus.FOUND);

        this.mockMvc.perform(get("/documento/{idDocument}", documento.getId())
                .with(csrf())
                .with(user(userDetails)))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("https://i.makeagif.com/media/6-18-2016/i4va3h.gif"));

    }

    @SneakyThrows
    private static Stream<Arguments> provideDownloadDocumento() {
        User user1 = new User("ferrucci@unista.it", "ferrucci");
        Documento documento = new Documento();
        documento.setPath("src/test/resources/documents/file.txt");
        documento.setNome("test.txt");
        documento.setFormat("text/plain");
        File file = new File("src/test/resources/documents/file.txt");
        file.createNewFile();


        return Stream.of(
                Arguments.of(user1, documento)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDownloadDocumento")
    void downloadDocumento(User user, Documento documento) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        documento.setInRepository(true);
        documento = documentoDAO.save(documento);
        this.mockMvc.perform(get("/documento/{idDocument}", documento.getId())
                .with(csrf())
                .with(user(userDetails)))
                .andExpect(status().is(200))
                .andExpect(header().exists("Content-Disposition"));

    }
}

