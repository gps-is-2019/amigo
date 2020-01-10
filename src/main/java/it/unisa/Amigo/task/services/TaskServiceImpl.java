package it.unisa.Amigo.task.services;

import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Questa classe implementa i metodi  per la logica di Business del sottositema "Gruppo".
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    /**
     * Fornisce accesso alla classe di dominio "Task".
     */
    private final TaskDAO taskDAO;

    /**
     * Ritorna l'user @{@link Persona} incaricato del Task @{@link Task}.
     *
     * @param id identifica univocamente un task
     * @return la persona a cui quel task è assegnato
     */
    @Override
    public Persona getAssegnatarioTask(final Integer id) {
        Optional<Task> task = taskDAO.findById(id);
        return task.map(Task::getPersona).orElse(null);
    }

    /**
     * Definisce un nuovo Task @{@link Task}.
     *
     * @param descrizione del nuovo task
     * @param data        di scadenza del nuovo task
     * @param nome        del nuovo task
     * @param stato       del nuovo task
     * @param supergruppo di appartenenza del task da definire
     * @param persona     responsabile del task che si sta definendo
     * @return Task appena creato
     */
    @Override
    public Task definizioneTaskSupergruppo(final String descrizione, final LocalDate data, final String nome,
                                           final String stato,
                                           final Supergruppo supergruppo,
                                           final Persona persona) {
        Task task = new Task(descrizione, data, nome, stato);
        task.setSupergruppo(supergruppo);
        task.setPersona(persona);
        taskDAO.save(task);
        return task;
    }


    /**
     * Ritorna una lista di task @{@link Task} dell'utente passato.
     *
     * @param idPersona id della persona di cui si vuole conosce la lista di task
     * @return lista di task
     */
    @Override
    public List<Task> visualizzaTaskUser(final int idPersona) {
        return taskDAO.findAllByPersona_Id(idPersona);
    }

    /**
     * Ritorna la lista di task @{@link Task} del supergruppo @{@link Supergruppo} del supergruppo passato.
     *
     * @param idSupergruppo di cui si vogliono visualizzare i task
     * @return lista di task
     */
    @Override
    public List<Task> visualizzaTaskSuperGruppo(final int idSupergruppo) {
        return taskDAO.findAllBySupergruppo_Id(idSupergruppo);
    }

    /***
     * Ritorna il task @{@link Task} corrispondente dall'id cercato.
     * @param id identifica univocamente un task
     * @return task
     */
    @Override
    public Task getTaskById(final Integer id) {
        return taskDAO.findById(id).orElse(null);
    }

    /**
     * Metodo che permette il cambiamento di stato del task@{@link Task}, passato tramite il suo id, in approvato.
     *
     * @param idTask identifica univocamente un task
     */
    @Override
    public void accettazioneTask(final Integer idTask) {
        Task task = taskDAO.findById(idTask).orElse(null);
        assert task != null;
        task.setStato("approvato");
        taskDAO.save(task);
    }

    /**
     * Metodo che permette il cambiamento di stato del task@{@link Task}, passato tramite il suo id, in respinto.
     *
     * @param idTask identifica univocamente un task
     */
    @Override
    public void rifiutoTask(final Integer idTask) {
        Task task = taskDAO.findById(idTask).orElse(null);
        assert task != null;
        task.setStato("respinto");
        taskDAO.save(task);
    }

    /**
     * Metodo che permette il cambiamento di stato del task@{@link Task}, passato tramite il suo id, in completo.
     *
     * @param idTask identifica univocamente un task
     */
    @Override
    public void completaTask(final Integer idTask) {
        Task task = taskDAO.findById(idTask).orElse(null);
        assert task != null;
        task.setStato("in valutazione");
        taskDAO.save(task);
    }

    /**
     * Metodo che permette di camiare le informazioni di un task@{@link Task}.
     *
     * @param taskToUpdate task aggiornato
     */
    @Override
    public void updateTask(final Task taskToUpdate) {
        taskDAO.save(taskToUpdate);
    }
}
