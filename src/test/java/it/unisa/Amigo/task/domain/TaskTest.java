package it.unisa.Amigo.task.domain;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TaskTest {

    @Test
    void descizioneNull() {
        LocalDate data1 = LocalDate.now();
        NullPointerException execeptionThrows = assertThrows(NullPointerException.class, () -> {
            Task task = new Task(null, data1, "task1", "completo");
        });
        assertEquals("descrizione is marked non-null but is null", execeptionThrows.getMessage());
    }

    @Test
    void dataScadenzaNull() {
        NullPointerException execeptionThrows = assertThrows(NullPointerException.class, () -> {
            Task task = new Task("t1", null, "task1", "completo");
        });
        assertEquals("dataScadenza is marked non-null but is null", execeptionThrows.getMessage());
    }

    @Test
    void NomeNull() {
        LocalDate data1 = LocalDate.now();
        NullPointerException execeptionThrows = assertThrows(NullPointerException.class, () -> {
            Task task = new Task("t1", data1, null, "completo");
        });
        assertEquals("nome is marked non-null but is null", execeptionThrows.getMessage());
    }

    @Test
    void statoNull() {
        LocalDate data1 = LocalDate.now();
        NullPointerException execeptionThrows = assertThrows(NullPointerException.class, () -> {
            Task task = new Task("t1", data1, "task1", null);
        });
        assertEquals("stato is marked non-null but is null", execeptionThrows.getMessage());
    }

}
