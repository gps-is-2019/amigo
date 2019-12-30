package it.unisa.Amigo.task.services;


import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.domain.Task;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;


public interface TaskService {
    Persona getAssegnatarioTask(int id);

    //TODO ci serve la classe documento
    Documento getDocumentoTask(int id);
    Boolean addDocumentoTask(Documento documento, int idTask);
    //List<Documenti> visualizzaDocumentiApprovati(Supergruppo supergruppo);

    Boolean definizioneTaskSupergruppo(String descrizione, Date data, String nome, String stato, Supergruppo supergruppo, Persona persona);
    List<Task> visualizzaTaskUser(int idPersona);
    List<Task> visualizzaTaskSuperGruppo(int  idSupergruppo);

    Task getTaskById(int id);
    void accettazioneTask(int idTask);
    void rifiutoTask(int idTask);
    void completaTask(int idTask);
    void updateTask(Task taskToUpdate);
}