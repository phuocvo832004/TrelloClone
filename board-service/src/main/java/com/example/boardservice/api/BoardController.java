package com.example.boardservice.api;

import com.example.boardservice.application.BoardService;
import com.example.boardservice.domain.Board;
import com.example.boardservice.domain.BoardMember;
import com.example.boardservice.domain.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    public Board createBoard(@RequestBody Board board) {
        return boardService.createBoard(board);
    }

    @GetMapping("/{userId}")
    public List<Board> getUserBoards(@PathVariable UUID userId) {
        return boardService.getUserBoards(userId);
    }
    @PostMapping("/{boardId}/members")
    public BoardMember addMember(@PathVariable UUID boardId,
                                 @RequestParam UUID userId,
                                 @RequestParam Role role) {
        return boardService.addMember(boardId, userId, role);
    }

    @DeleteMapping("/{boardId}/members")
    public void removeMember(@PathVariable UUID boardId,
                             @RequestParam UUID userId,
                             @RequestParam UUID adminId) {
        boardService.removeMember(boardId, userId, adminId);
    }

    @DeleteMapping("/{boardId}")
    public void deleteBoard(@PathVariable UUID boardId, @RequestParam UUID userId) {
        boardService.deleteBoard(boardId, userId);
    }


    @GetMapping("/{boardId}/members")
    public ResponseEntity<List<BoardMember>> getBoardMembers(@PathVariable UUID boardId) {
        return ResponseEntity.ok(boardService.getBoardMembers(boardId));
    }

}
