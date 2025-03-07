package com.example.boardservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "board_tasks")
public class BoardTask {
    @Id
    private UUID taskId;
    private UUID boardId;
    private String title;
    private String status;

    public BoardTask() {}

    public BoardTask(UUID taskId, UUID boardId, String title, String status) {
        this.taskId = taskId;
        this.boardId = boardId;
        this.title = title;
        this.status = status;
    }

    public UUID getTaskId() { return taskId; }
    public UUID getBoardId() { return boardId; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "BoardTask{" +
                "taskId=" + taskId +
                ", boardId=" + boardId +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
