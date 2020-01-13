package it.unisa.Amigo.gruppo.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SupergruppoTest {
    @Test
    void nameNull() {
        NullPointerException exceptionThrows = assertThrows(NullPointerException.class, () -> {
            Supergruppo supergruppo = new Supergruppo(null, "", true);
        });
        assertEquals("name is marked non-null but is null", exceptionThrows.getMessage());
    }

    @Test
    void typeNull() {
        NullPointerException exceptionThrows = assertThrows(NullPointerException.class, () -> {
            Supergruppo supergruppo = new Supergruppo("", null, true);
        });
        assertEquals("type is marked non-null but is null", exceptionThrows.getMessage());
    }

    @Test
    void stateNull() {
        NullPointerException exceptionThrows = assertThrows(NullPointerException.class, () -> {
            Supergruppo supergruppo = new Supergruppo("", "", null);
        });
        assertEquals("state is marked non-null but is null", exceptionThrows.getMessage());
    }
}
