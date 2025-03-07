package com.example.taskservice.event;

import com.example.taskservice.model.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class TaskEvent {
    private UUID taskId;
    private UUID boardId;
    private String title;
    private TaskStatus status;
    private String eventType; // "CREATED", "UPDATED"

    public TaskEvent(UUID taskId, UUID boardId, String title, TaskStatus status, String eventType) {
        this.taskId = taskId;
        this.boardId = boardId;
        this.title = title;
        this.status = status;
        this.eventType = eventType;
    }

}
