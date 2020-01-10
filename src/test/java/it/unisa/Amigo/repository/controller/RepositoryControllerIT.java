package it.unisa.Amigo.repository.controller;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
/*
//TODO

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

    @ParameterizedTest
    @MethodSource("provideRepository")
    public void repository(User user, Persona expectedPersona) throws Exception {

        //User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        //Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        personaDAO.save(expectedPersona);
        userDAO.save(user);
        repositoryService.addDocumentoInRepository(new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes()));
        List<Documento> expectedDocumenti = new ArrayList<>();
        expectedDocumenti = repositoryService.searchDocumentInRepository("");

        this.mockMvc.perform(get("/repository")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("documenti", expectedDocumenti))
                .andExpect(view().name("repository/repository"));
    }

    private static Stream<Arguments> provideRepository() {
        User user1 = new User("Admin", "Admin");
        User user2 = new User(".", "pass");
        User user3 = new User("bounty", ".");

        Persona expectedPersona1 = new Persona(null, "123", "Administrator");
        Persona expectedPersona2 = new Persona("123", ".", "Administrator");
        Persona expectedPersona3 = new Persona("123", ".", ".");

        return Stream.of(
                Arguments.of(user1, expectedPersona1),
                Arguments.of(user2, expectedPersona2),
                Arguments.of(user3, expectedPersona3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUploadDocumento")
    public void uploadDocumento(User user, Persona expectedPersona) throws Exception {

        user.addRole(new Role(Role.PQA_ROLE));
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);

        personaDAO.save(expectedPersona);
        userDAO.save(user);

        this.mockMvc.perform(get("/repository/uploadDocumento")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("repository/aggiunta_documento_repository"));
    }

    private static Stream<Arguments> provideUploadDocumento() {
        User user4 = new User("Admin", "Admin");
        User user5 = new User(".", "pass");
        User user6 = new User("bounty", ".");

        Persona expectedPersona4 = new Persona("Admin", "Admin", "Administrator");
        Persona expectedPersona5 = new Persona("123", ".", "Administrator");
        Persona expectedPersona6 = new Persona("123", ".", ".");

        return Stream.of(
                Arguments.of(user4, expectedPersona4),
                Arguments.of(user5, expectedPersona5),
                Arguments.of(user6, expectedPersona6)
        );
    }

/*
    @Test
    public void downloadDocumento() throws Exception {
    }
*/

