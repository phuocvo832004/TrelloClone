package com.example.taskservice.controller;

import com.example.taskservice.model.Task;
import com.example.taskservice.model.TaskStatus;
import com.example.taskservice.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task createTask(@RequestParam UUID boardId,
                           @RequestParam String title,
                           @RequestParam String description) {
        return taskService.createTask(boardId, title, description);
    }

    @GetMapping("/board/{boardId}")
    public List<Task> getTasksByBoard(@PathVariable UUID boardId) {
        return taskService.getTasksByBoard(boardId);
    }

    @PutMapping("/{taskId}/status")
    public Task updateTaskStatus(@PathVariable UUID taskId,
                                 @RequestParam TaskStatus status) {
        return taskService.updateTaskStatus(taskId, status);
    }
}
