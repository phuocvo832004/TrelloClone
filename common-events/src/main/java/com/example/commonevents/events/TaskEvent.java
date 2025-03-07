package com.example.commonevents.events;

import java.io.Serializable;
import java.util.UUID;

public class TaskEvent implements Serializable {
    private UUID taskId;
    private UUID boardId;
    private String title;
    private String status;
    private String eventType;

    public TaskEvent() {}

    public TaskEvent(UUID taskId, UUID boardId, String title, String status, String eventType) {
        this.taskId = taskId;
        this.boardId = boardId;
        this.title = title;
        this.status = status;
        this.eventType = eventType;
    }

    public UUID getTaskId() { return taskId; }
    public UUID getBoardId() { return boardId; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getEventType() { return eventType; }

    @Override
    public String toString() {
        return "TaskEvent{" +
                "taskId=" + taskId +
                ", boardId=" + boardId +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}

