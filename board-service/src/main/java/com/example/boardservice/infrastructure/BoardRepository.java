package com.example.boardservice.infrastructure;

import com.example.boardservice.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, UUID> {
    List<Board> findByOwnerId(UUID ownerId);
}
