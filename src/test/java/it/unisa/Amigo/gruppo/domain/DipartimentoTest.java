package it.unisa.Amigo.gruppo.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class DipartimentoTest {
    @Test
    void nameNull() {
        NullPointerException exceptionThrows = assertThrows(NullPointerException.class, () -> {
            Dipartimento dipartimento = new Dipartimento(null);
        });
        assertEquals("name is marked non-null but is null", exceptionThrows.getMessage());
    }
}
