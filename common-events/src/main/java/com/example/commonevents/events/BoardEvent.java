package com.example.commonevents.events;

import java.io.Serializable;
import java.util.UUID;

public class BoardEvent implements Serializable {
    private UUID boardId;
    private String boardName;
    private String eventType; // "BOARD_CREATED", "BOARD_UPDATED"

    public BoardEvent() {}

    public BoardEvent(UUID boardId, String boardName, String eventType) {
        this.boardId = boardId;
        this.boardName = boardName;
        this.eventType = eventType;
    }

    public UUID getBoardId() { return boardId; }
    public String getBoardName() { return boardName; }
    public String getEventType() { return eventType; }

    @Override
    public String toString() {
        return "BoardEvent{" +
                "boardId=" + boardId +
                ", boardName='" + boardName + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
