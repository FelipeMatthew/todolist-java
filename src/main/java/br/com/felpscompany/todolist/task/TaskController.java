package br.com.felpscompany.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.felpscompany.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private InterfaceTaskRepository taskRepository;

  // CREATE
  @PostMapping("/")
  public ResponseEntity createTask(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    // Get the attribute created on filter 
    var idUser = request.getAttribute("idUser");
    taskModel.setIdUser((UUID) idUser);

    // VALIDATION DATE TIME
    var currentDate = LocalDateTime.now();

    // If task > getStart || getEnd - return error
    if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt()) ) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body("Start of finish date needs to be greater than current date");
    }

    // If endData less than start data
    if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body("Start date needs to be less than end date");
    }

    // Query
    var task = taskRepository.save(taskModel);
    return ResponseEntity.status(HttpStatus.OK).body(task);
    }
  
  // LIST
  @GetMapping("/")
  public List<TaskModel> listTasks(HttpServletRequest request) {
    var idUser = request.getAttribute("idUser");
    var tasksList = this.taskRepository.findByIdUser((UUID) idUser);

    return tasksList;
  }
  
  // UPDATE
  @PutMapping("/{id}")
  public ResponseEntity updateTasks(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {  
    var task = this.taskRepository.findById(id).orElseThrow(null);

    // Task validator 
    if(task == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body("Task don't exist");
    }

    var idUser = request.getAttribute("idUser");

    // Validate user and id user
    if(!task.getIdUser().equals(idUser)) {
       return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body("User don't have permission to edit this task");
    }

    // vai mesclar os dados
    Utils.copyNonNullProperties(taskModel, task);

    var taskUpdated = this.taskRepository.save(task);
    return ResponseEntity.ok().body(taskUpdated);
  }

  
}
