package com.freedom.character.domain;

import com.freedom.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Character
 * - User와 1:1 (FK: user_id)
 */
@Entity
@Table(name = "characters",
        indexes = @Index(name = "idx_character_user_id", columnList = "user_id"),
        uniqueConstraints = @UniqueConstraint(name = "uk_character_user_id", columnNames = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Character extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 단방향 보유(FK로만 무결성 보장)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "character_name", nullable = false, length = 20)
    private String characterName;

    @Builder
    public Character(Long userId, String characterName) {
        this.userId = userId;
        this.characterName = characterName;
    }
}
