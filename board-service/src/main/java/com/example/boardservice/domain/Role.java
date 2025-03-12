package com.example.boardservice.domain;

public enum Role {
    OWNER, ADMIN, MEMBER;

    public String getName() {
        return this.name();
    }

    public String getRole() {
        return this.name();
    }
}
