package com.freedom.character.infra;

import com.freedom.character.domain.Character;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Character 저장소
 */
public interface CharacterRepository extends JpaRepository<Character, Long> {

    Optional<Character> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
