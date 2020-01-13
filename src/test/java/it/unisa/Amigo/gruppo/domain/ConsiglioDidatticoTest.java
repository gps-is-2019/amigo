package it.unisa.Amigo.gruppo.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ConsiglioDidatticoTest {

    @Test
    void nameNull() {
        NullPointerException exceptionThrows = assertThrows(NullPointerException.class, () -> {
            ConsiglioDidattico consiglioDidattico = new ConsiglioDidattico(null);
        });
        assertEquals("name is marked non-null but is null", exceptionThrows.getMessage());
    }

}
