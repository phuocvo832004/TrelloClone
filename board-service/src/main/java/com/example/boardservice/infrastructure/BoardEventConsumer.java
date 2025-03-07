package com.example.boardservice.infrastructure;

import com.example.commonevents.events.TaskEvent;
import com.example.boardservice.domain.Board;
import com.example.boardservice.infrastructure.BoardRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BoardEventConsumer {

    private final BoardRepository boardRepository;

    public BoardEventConsumer(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @KafkaListener(topics = "task-events", groupId = "board-service-group")
    public void handleTaskEvent(TaskEvent taskEvent) {
        switch (taskEvent.getEventType()) {
            case "TASK_CREATED" -> handleTaskCreated(taskEvent);
            case "TASK_UPDATED" -> handleTaskUpdated(taskEvent);
            case "TASK_DELETED" -> handleTaskDeleted(taskEvent);
            default -> System.out.println("Unknown event type: " + taskEvent.getEventType());
        }
    }

    private void handleTaskCreated(TaskEvent event) {
        Board board = boardRepository.findById(event.getBoardId())
                .orElseThrow(() -> new RuntimeException("Board not found"));

        board.setTaskCount(board.getTaskCount() + 1);
        boardRepository.save(board);
        System.out.println("Task added to board: " + board.getId());
    }

    private void handleTaskUpdated(TaskEvent event) {
        System.out.println("Task updated: " + event.getTaskId());
    }

    private void handleTaskDeleted(TaskEvent event) {
        Board board = boardRepository.findById(event.getBoardId())
                .orElseThrow(() -> new RuntimeException("Board not found"));

        board.setTaskCount(Math.max(0, board.getTaskCount() - 1));
        boardRepository.save(board);
        System.out.println("Task removed from board: " + board.getId());
    }
}
