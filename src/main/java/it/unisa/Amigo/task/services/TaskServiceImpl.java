package it.unisa.Amigo.task.services;

import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService
{

    @Autowired
    private TaskDAO taskDAO;

    /***
     * Ritorna l'user @{@link Persona} incaricato del Task @{@link Task}
     * @param id del task assegnato
     * @return persona
     */
    @Override
    public Persona getAssegnatarioTask(int id) {
        Task task= taskDAO.findById(id);
        Persona result = task.getPersona();
        return result;
    }

    /***
     * Ritorna il documento @{@link Documento} del Task @{@link Task}
     * @param id del task di cui si vuole l'id
     * @return documento
     */
    @Override
    public Documento getDocumentoTask(int id) {
        return null;
    } //TODO

    /***
     * Aggiunge un documento @{@link Documento} al Task @{@link Task}
     * @param documento da aggiungere al task
     * @param idTask id del task al cui aggiungere il documento
     * @return Boolean
     */
    @Override
    public Boolean addDocumentoTask(Documento documento, int idTask) {
        return null;
    }


    /***
     * Definisce un nuovo Task @{@link Task}
     * @param descrizione del nuovo task
     * @param data di scadenza del nuovo task
     * @param nome del nuovo task
     * @param stato del nuovo task
     * @param supergruppo di appartenenza del task da definire
     * @param persona responsabile del task che si sta definendo
     * @return Boolean indicante il successo o il fallimento dell'azione
     */
    @Override
    public Boolean definizioneTaskSupergruppo(String descrizione, Date data, String nome, String stato, Supergruppo supergruppo, Persona persona){
        Task task = new Task(descrizione, data, nome, stato);
        task.setSupergruppo(supergruppo);
        task.setPersona(persona);
        try {
            taskDAO.save(task);
        } catch (Exception ex){
           return false;
        };
        return true;
    }


    /***
     * Ritorna una lista di task @{@link Task} dell'utente passato
     * @param idPersona id della persona di cui si vuole conosce la lista di task
     * @return lista di task
     */
    @Override
    public List<Task> visualizzaTaskUser(int idPersona) {
        return taskDAO.findAllByPersona_Id(idPersona);
    }

    //TODO da aggiornare su odd da Supergruppo supergruppo ad int idSupergruppo
    /***
     * Ritorna la lista di task @{@link Task} del supergruppo @{@link Supergruppo} del supergruppo passato
     * @param idSupergruppo di cui si vogliono visualizzare i task
     * @return lista di task
     */
    @Override
    public List<Task> visualizzaTaskSuperGruppo(int idSupergruppo) {
        List<Task> ris = taskDAO.findAllBySupergruppo_Id(idSupergruppo);
        return ris;
    }

    /***
     * Ritorna il task @{@link Task} corrispondente dall'id cercato
     * @param id del task
     * @return task
     */
    @Override
    public Task getTaskById(int id) {
        Task ris = taskDAO.findById(id);
        return ris;
    }

    /***
     * Metodo che permette il cambiamento di stato del task@{@link Task}, passato tramite il suo id, in approvato
     * @param idTask
     */
    @Override
    public void accettazioneTask(int idTask) {
        Task task = taskDAO.findById(idTask);
        task.setStato("approvato");
        taskDAO.save(task);
    }

    /***
     * Metodo che permette il cambiamento di stato del task@{@link Task}, passato tramite il suo id, in respinto
     * @param idTask
     */
    @Override
    public void rifiutoTask(int idTask) {
        Task task = taskDAO.findById(idTask);
        task.setStato("respinto");
        taskDAO.save(task);
    }

    @Override
    public void completaTask(int idTask) {
        Task task = taskDAO.findById(idTask);
        task.setStato("in valutazione");
        taskDAO.save(task);
    }

    @Override
    public void updateTask(Task taskToUpdate) {
        taskDAO.save(taskToUpdate);
    }
}
