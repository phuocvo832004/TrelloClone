package com.example.taskservice.service;

import com.example.taskservice.event.TaskEvent;
import com.example.taskservice.kafka.TaskEventProducer;
import com.example.taskservice.model.Task;
import com.example.taskservice.model.TaskStatus;
import com.example.taskservice.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final RestTemplate restTemplate;
    private final TaskEventProducer eventProducer;
    private KafkaTemplate<String, TaskEvent> kafkaTemplate;
    @Value("${board.service.url}")
    private String boardServiceUrl;

    public TaskService(TaskRepository taskRepository, RestTemplate restTemplate, TaskEventProducer eventProducer) {
        this.taskRepository = taskRepository;
        this.restTemplate = restTemplate;
        this.eventProducer = eventProducer;
    }

    public void publishTaskEvent(Task task, String eventType) {
        TaskEvent event = new TaskEvent(task.getId(), task.getBoardId(), task.getTitle(), task.getStatus(), eventType);
        kafkaTemplate.send("task-events", event);
        System.out.println("📤 Sent Task Event: " + event);
    }

    public Task createTask(UUID boardId, String title, String description) {
        // 🔍 Kiểm tra xem Board có tồn tại không
        String url = boardServiceUrl + "/boards/exists?boardId=" + boardId;
        Boolean boardExists = restTemplate.getForObject(url, Boolean.class);

        if (Boolean.FALSE.equals(boardExists)) {
            throw new RuntimeException("Board không tồn tại");
        }

        Task task = new Task();
        task.setBoardId(boardId);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(TaskStatus.TODO);
        Task savedTask = taskRepository.save(task);

        // 📩 Gửi sự kiện Task Created lên Kafka
        eventProducer.sendTaskEvent(new TaskEvent(
                savedTask.getId(),
                savedTask.getBoardId(),
                savedTask.getTitle(),
                savedTask.getStatus(),
                "CREATED"
        ));

        logger.info("✅ Task Created: {}", savedTask);
        return savedTask;
    }

    public List<Task> getTasksByBoard(UUID boardId) {
        return taskRepository.findByBoardId(boardId);
    }

    public Task updateTaskStatus(UUID taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);

        // 📩 Gửi sự kiện Task Updated lên Kafka
        eventProducer.sendTaskEvent(new TaskEvent(
                updatedTask.getId(),
                updatedTask.getBoardId(),
                updatedTask.getTitle(),
                updatedTask.getStatus(),
                "UPDATED"
        ));

        logger.info("✅ Task Updated: {}", updatedTask);
        return updatedTask;
    }


}
