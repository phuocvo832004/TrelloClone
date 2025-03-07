package com.example.boardservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Data
@Entity
@Table(name = "board_members")
public class BoardMember {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    private Role role;
}
