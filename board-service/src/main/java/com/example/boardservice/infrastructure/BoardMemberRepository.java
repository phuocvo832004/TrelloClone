package com.example.boardservice.infrastructure;

import com.example.boardservice.domain.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardMemberRepository extends JpaRepository<BoardMember, UUID> {
    List<BoardMember> findByBoardId(UUID boardId);
    Optional<BoardMember> findByBoardIdAndUserId(UUID boardId, UUID userId);
}
