package com.example.boardservice.application;

import com.example.boardservice.domain.Board;
import com.example.boardservice.domain.BoardMember;
import com.example.boardservice.domain.Role;
import com.example.boardservice.infrastructure.BoardMemberRepository;
import com.example.boardservice.infrastructure.BoardRepository;
import com.example.boardservice.infrastructure.KafkaProducerService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final KafkaProducerService kafkaProducerService;
    private final BoardMemberRepository boardMemberRepository;
    private final RestTemplate restTemplate;
    private final String userServiceUrl = "http://user-service:8081";

    public BoardService(BoardRepository boardRepository, KafkaProducerService kafkaProducerService, BoardMemberRepository boardMemberRepository, RestTemplate restTemplate) {
        this.boardRepository = boardRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.boardMemberRepository = boardMemberRepository;
        this.restTemplate = restTemplate;
    }

    public Board updateBoard(UUID boardId, Board updatedBoard, UUID userId) {
        checkPermission(boardId, userId, Role.ADMIN);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        board.setName(updatedBoard.getName());
        return boardRepository.save(board);
    }

    @CacheEvict(value = "boards", key = "#board.ownerId")
    public Board createBoard(Board board) {
        Board savedBoard = boardRepository.save(board);
        kafkaProducerService.sendMessage("board-events", "Board created: " + board.getName());
        return savedBoard;
    }

    @Caching(evict = {
            @CacheEvict(value = "boards", key = "#boardRepository.findById(#boardId).get().ownerId", beforeInvocation = true)
    })
    public void deleteBoard(UUID boardId, UUID userId) {
        String url = userServiceUrl + "/users/exists?userId=" + userId;
        Boolean userExists = restTemplate.getForObject(url, Boolean.class);

        if (Boolean.FALSE.equals(userExists)) {
            throw new RuntimeException("User không tồn tại");
        }

        boardRepository.deleteById(boardId);
    }



    @Cacheable(value = "boards", key = "#ownerId")
    public List<Board> getUserBoards(UUID ownerId) {
        return boardRepository.findByOwnerId(ownerId);
    }

    public BoardMember addMember(UUID boardId, UUID userId, Role role) {
        // Gọi User Service để kiểm tra user có tồn tại không
        String url = userServiceUrl + "/users/exists?userId=" + userId;
        Boolean userExists = restTemplate.getForObject(url, Boolean.class);

        if (Boolean.FALSE.equals(userExists)) {
            throw new RuntimeException("User không tồn tại trong hệ thống");
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        if (boardMemberRepository.findByBoardIdAndUserId(boardId, userId).isPresent()) {
            throw new RuntimeException("User đã là thành viên của board");
        }

        BoardMember member = new BoardMember();
        member.setBoard(board);
        member.setUserId(userId);
        member.setRole(role);

        return boardMemberRepository.save(member);
    }

    public void checkPermission(UUID boardId, UUID userId, Role requiredRole) {
        BoardMember member = boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this board"));

        if (member.getRole().ordinal() > requiredRole.ordinal()) {
            throw new RuntimeException("User does not have permission for this action");
        }
    }

    public List<BoardMember> getBoardMembers(UUID boardId) {
        return boardMemberRepository.findByBoardId(boardId);
    }


    public void removeMember(UUID boardId, UUID userId, UUID adminId) {
        checkPermission(boardId, adminId, Role.ADMIN);
        BoardMember member = boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this board"));

        if (member.getRole() == Role.OWNER) {
            throw new RuntimeException("Cannot remove the board owner");
        }

        boardMemberRepository.delete(member);
    }



}
