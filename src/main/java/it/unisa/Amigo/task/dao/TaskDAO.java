package it.unisa.Amigo.task.dao;


import com.sun.xml.bind.v2.model.core.ID;
import it.unisa.Amigo.task.domain.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Questa interfaccia si occupa di fornire un accesso astratto all'oggetto di dominio "Task".
 */
@Repository
public interface TaskDAO extends CrudRepository<Task, Integer> {

    List<Task> findAllBySupergruppo_Id(int idSupergruppo);

    List<Task> findAllByPersona_Id(int id);

    Task findById(ID id);
}
