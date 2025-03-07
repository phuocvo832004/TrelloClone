package com.example.boardservice.infrastructure;

import com.example.boardservice.domain.BoardTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BoardTaskRepository extends JpaRepository<BoardTask, UUID> {
}
