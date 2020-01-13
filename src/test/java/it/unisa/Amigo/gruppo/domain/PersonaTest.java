package it.unisa.Amigo.gruppo.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PersonaTest {
    @Test
    void nomeNull() {
        NullPointerException exceptionThrows = assertThrows(NullPointerException.class, () -> {
            Persona persona = new Persona(null, "conte", "");
        });
        assertEquals("nome is marked non-null but is null", exceptionThrows.getMessage());
    }

    @Test
    void cognomeNull() {
        NullPointerException exceptionThrows = assertThrows(NullPointerException.class, () -> {
            Persona persona = new Persona("armando", null, "");
        });
        assertEquals("cognome is marked non-null but is null", exceptionThrows.getMessage());
    }

    @Test
    void ruoloNull() {
        NullPointerException exceptionThrows = assertThrows(NullPointerException.class, () -> {
            Persona persona = new Persona("armando", "conte", null);
        });
        assertEquals("ruolo is marked non-null but is null", exceptionThrows.getMessage());
    }
}
