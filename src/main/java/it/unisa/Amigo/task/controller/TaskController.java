package it.unisa.Amigo.task.controller;

import it.unisa.Amigo.task.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo={id}")
    public String visualizzaListaTaskSupergruppo(Model model, @PathVariable(name = "id") int idSupergruppo) {

        return "/task/paginaVisualizzaListaTaskSupergruppo";
    }

}