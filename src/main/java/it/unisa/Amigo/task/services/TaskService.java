package it.unisa.Amigo.task.services;


import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.consegna.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.domain.Task;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;


public interface TaskService {
    User getAssegnatarioTask(int id);
    Documento getDocumentoTask(int id);
    Boolean addDocumentoTask(Documento documento, int idTask);
    Boolean definizioneTaskSupergruppo(String descrizione, Date data, String nome, String stato, Supergruppo supergruppo, String email);
    List<Task> visualizzaTaskUser(User user);
    List<Task> visualizzaTaskSuperGruppo(Supergruppo supergruppo);
    //List<Documenti> visualizzaDocumentiApprovati(Supergruppo supergruppo);
    List<Task> searchTaskById(int id);

}