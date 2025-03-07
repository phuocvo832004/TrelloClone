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
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private int taskCount = 0;
}
