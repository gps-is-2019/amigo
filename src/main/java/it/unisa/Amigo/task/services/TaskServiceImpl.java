package it.unisa.Amigo.task.services;

import it.unisa.Amigo.consegna.services.ConsegnaService;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.services.DocumentoService;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Questa classe implementa i metodi  per la logica di Business del sottositema "Task".
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    /**
     * Fornisce accesso alla classe di dominio "Task".
     */
    private final TaskDAO taskDAO;

    private final GruppoService gruppoService;
    private final DocumentoService documentoService;
    private final ConsegnaService consegnaService;

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
     * @param descrizione   del nuovo task
     * @param data          di scadenza del nuovo task
     * @param nome          del nuovo task
     * @param stato         del nuovo task
     * @param supergruppoId di appartenenza del task da definire
     * @param personaId     responsabile del task che si sta definendo
     * @return Task appena creato
     */
    @Override
    public Task definizioneTaskSupergruppo(final String descrizione, final LocalDate data, final String nome,
                                           final String stato,
                                           final Integer supergruppoId,
                                           final Integer personaId) {
        Task task = new Task(descrizione, data, nome, stato);
        task.setSupergruppo(gruppoService.findSupergruppo(supergruppoId));
        task.setPersona(gruppoService.findPersona(personaId));
        taskDAO.save(task);
        return task;
    }


    /**
     * Ritorna una lista di task @{@link Task} dell'utente passato.
     *
     * @return lista di task
     */
    @Override
    public List<Task> visualizzaTaskUser() {

        return taskDAO.findAllByPersona_Id(gruppoService.getCurrentPersona().getId());
    }

    /**
     * Ritorna la lista di task @{@link Task} del supergruppo passato.
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
        Task task = taskDAO.findById(idTask).get();
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
        Task task = taskDAO.findById(idTask).get();
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
        Task task = taskDAO.findById(idTask).get();
        task.setStato("in valutazione");
        taskDAO.save(task);
    }

    /**
     * Metodo che permette di cambiare le informazioni di un task@{@link Task}.
     *
     * @param taskToUpdate task aggiornato
     * @param assegneeId   id dell'assegnatario
     */
    @Override
    public void updateTask(final Task taskToUpdate, final Integer assegneeId) {
        Persona assegnee = gruppoService.findPersona(assegneeId);
        taskToUpdate.setPersona(assegnee);
        taskDAO.save(taskToUpdate);
    }

    /**
     * Allega un documento @{@link Documento} ad un task @{@link Task}
     *
     * @param t           task a cui allegare il documento
     * @param fileName    nome del file
     * @param fileContent file da allegare
     * @param type        formato del file
     * @return task
     */
    @Override
    public Task attachDocumentToTask(final Task t, final String fileName, final byte[] fileContent, final String type) {
        Documento documento = documentoService.addDocumento(fileName, fileContent, type);
        t.setDocumento(documento);
        documento.setTask(t);
        return taskDAO.save(t);
    }

    /**
     * Controlla se l'utente loggato puà creare un task
     *
     * @param idSupergruppo supergruppo nel quale creare il task
     * @return true se l'utente loggato ha i permessi, false altrimenti
     */
    @Override
    public boolean currentPersonaCanCreateTask(final Integer idSupergruppo) {
        return gruppoService.isResponsabile(gruppoService.getCurrentPersona().getId(), idSupergruppo);
    }

    /**
     * Recupera i documenti approvati all'interno di un supergruppo
     *
     * @param idSupergruppo supergruppo dal quale prelevare i documenti approvati
     * @return lista di documenti approvati
     */
    @Override
    public List<Documento> getApprovedDocumentiOfSupergruppo(final Integer idSupergruppo) {
        return documentoService.approvedDocuments(idSupergruppo);
    }

    /**
     * Recupera i possibili assegnatari per un task all'interno di uno specifico supergruppo
     *
     * @param idSupergruppo supergruppo nel quale cercare i possibili assegnatari
     * @return lista di possibili assegnatari
     */
    @Override
    public List<Persona> getPossibleTaskAssegnees(final Integer idSupergruppo) {
        return gruppoService.findAllMembriInSupergruppo(idSupergruppo);
    }

    /**
     * Inoltra un documento approvato al PQA
     *
     * @param taskId task contenente il documento da inoltrare al PQA
     */
    @Override
    public void forwardApprovedTaskToPqa(final Integer taskId) {
        Documento documento = taskDAO.findById(taskId).get().getDocumento();
        consegnaService.inoltraPQAfromGruppo(documento);
    }

    /**
     * Controlla se l'utente loggato ha i permessi per vedere il task
     *
     * @param idTask task che si vuole visualizzare
     * @return true se l'utente loggato ha i permessi, false altrimenti
     */
    @Override
    public boolean currentPersonaCanViewTask(final Integer idTask) {
        Persona currentPersona = gruppoService.getCurrentPersona();
        Task currentTask = taskDAO.findById(idTask).get();

        return currentTask.getSupergruppo().getPersone().contains(currentPersona);
    }

    /**
     * Ottiene il documento allegato al task
     *
     * @param task dal quale prelevare il documento
     * @return file sotto forma di resource
     */
    @Override
    public Resource getResourceFromTask(final Task task) {
        return documentoService.loadAsResource(task.getDocumento());
    }
}
