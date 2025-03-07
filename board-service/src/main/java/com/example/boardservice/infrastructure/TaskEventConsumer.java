package com.example.boardservice.infrastructure;

import com.example.commonevents.events.TaskEvent;
import com.example.boardservice.domain.BoardTask;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskEventConsumer {
    private final BoardTaskRepository boardTaskRepository;

    public TaskEventConsumer(BoardTaskRepository boardTaskRepository) {
        this.boardTaskRepository = boardTaskRepository;
    }

    @KafkaListener(topics = "task-events", groupId = "board-group")
    public void consumeTaskEvent(TaskEvent event) {
        System.out.println("Received Task Event: " + event);

        switch (event.getEventType()) {
            case "TASK_CREATED":
                handleTaskCreated(event);
                break;
            case "TASK_UPDATED":
                handleTaskUpdated(event);
                break;
            default:
                System.out.println("⚠ Unknown event type: " + event.getEventType());
        }
    }

    private void handleTaskCreated(TaskEvent event) {
        BoardTask boardTask = new BoardTask(event.getTaskId(), event.getBoardId(), event.getTitle(), event.getStatus());
        boardTaskRepository.save(boardTask);
        System.out.println("✅ Task created in Board Service: " + boardTask);
    }

    private void handleTaskUpdated(TaskEvent event) {
        Optional<BoardTask> optionalBoardTask = boardTaskRepository.findById(event.getTaskId());
        if (optionalBoardTask.isPresent()) {
            BoardTask boardTask = optionalBoardTask.get();
            boardTask.setStatus(event.getStatus());
            boardTaskRepository.save(boardTask);
            System.out.println("✅ Task updated in Board Service: " + boardTask);
        } else {
            System.out.println("⚠ Task not found for update: " + event.getTaskId());
        }
    }
}
