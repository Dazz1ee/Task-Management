package com.taskmanagement.controllers;

import com.taskmanagement.models.*;
import com.taskmanagement.services.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
@Slf4j
public class TaskController {

    private final TaskService taskService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/new")
    public ResponseEntity<?> addNewTask(@RequestBody TaskRequest request) {
        kafkaTemplate.send("newTask", request);
        return ResponseEntity.ok("task added");
    }

    @GetMapping("/list")
    public ResponseEntity<?> getPageTasksNotTaken(@RequestParam(name = "page", defaultValue = "1") Integer numberPage, @RequestParam(name = "order", defaultValue = "date") String order) {
        List<Task> tasks;
        if (order.equals("priority")) {
            tasks = taskService.getPageTasksNotTakenPriorityOrder(numberPage);
        } else {
            tasks = taskService.getPageTasksNotTaken(numberPage);
        }
        return tasks.isEmpty() ? ResponseEntity.ok("No tasks available") : ResponseEntity.ok().body(tasks);
    }


    @GetMapping("/list-all")
    public ResponseEntity<?> getPageTasksTaken(@RequestParam(name = "page", defaultValue = "1") Integer numberPage, @RequestParam(name = "order", defaultValue = "date") String order) {
        List<Task> tasks;

        if (order.equals("priority")) {
            tasks = taskService.getPageTasksPriorityOrder(numberPage);
        } else {
            tasks = taskService.getPageTasks(numberPage);
        }

        return tasks.isEmpty() ? ResponseEntity.ok("No tasks") : ResponseEntity.ok().body(tasks);
    }


    @PostMapping("/take")
    public ResponseEntity<?> takeTask(@RequestBody Integer id, @AuthenticationPrincipal User user) {
        boolean status = taskService.takeTask(id, user);
        return status ? ResponseEntity.ok("Task taken") : ResponseEntity.badRequest().body("Task is already running");
    }


    @PostMapping("/observe")
    public ResponseEntity<?> observeTask(@RequestBody Integer id, @AuthenticationPrincipal User user) {
        if (taskService.isCompleted(id)) {
            return ResponseEntity.ok("The task already completed");
        }
        kafkaTemplate.send("observe", new MessageObserver(user.getUserId(), id));
        return ResponseEntity.ok("The task added to observations");
    }

    @GetMapping("/myTasks")
    public ResponseEntity<?> getTaskForUser(@AuthenticationPrincipal User user) {
        List<Task> tasks = taskService.getUsersTask(user);
        return tasks.isEmpty() ? ResponseEntity.ok("Empty") : ResponseEntity.ok(tasks);
    }

    @PostMapping("/next-stage")
    public ResponseEntity<?>  nextStage(@RequestBody Integer id, @AuthenticationPrincipal User user) {
        Task task = taskService.getTask(id);
        if (task.getUser().getUserId().equals(user.getUserId())) {
            if (task.getStage().equals(TaskStage.WAIT_REVIEW)) {
                return ResponseEntity.status(208).body("Task is in the last stage");
            }
            kafkaTemplate.send("update", id);
            return ResponseEntity.ok("Stage updated");
        } else {
            return ResponseEntity.status(423).body("This is done by another user");
        }
    }



}
