package com.freedom.onboarding.application;

import com.freedom.auth.domain.User;
import com.freedom.auth.infra.UserJpaRepository;
import com.freedom.character.infra.CharacterRepository;
import com.freedom.character.domain.Character;
import com.freedom.common.exception.custom.CharacterAlreadyCreatedException;
import com.freedom.common.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 온보딩 유스케이스 서비스
 * - 캐릭터 생성
 * - 온보딩 상태 조회(캐릭터 생성 여부 + 캐릭터 이름)
 */
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final UserJpaRepository userJpaRepository;
    private final CharacterRepository characterRepository;

    /**
     * 캐릭터 생성(최초 1회)
     * - 이미 생성되어 있으면 예외
     * - 생성 성공 시 User.characterCreated = true 로 마킹
     */
    @Transactional
    public Long createCharacter(Long userId, String characterName) {
        // 1) 유저 검증
        User user = userJpaRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException("userId=" + userId);
        }

        // 2) 중복 생성 방지
        boolean alreadyExists = characterRepository.existsByUserId(userId);
        if (alreadyExists) {
            throw new CharacterAlreadyCreatedException("characterName=" + characterName);
        }

        // 3) 캐릭터 저장
        Character character = Character.builder()
                .userId(userId)
                .characterName(characterName) // 캐릭터 이름은 캐릭터 테이블에만 존재
                .build();

        Long characterId = characterRepository.save(character).getId();

        // 4) 유저 플래그 업데이트(도메인 메서드 사용)
        user.hasCharacterCreated();

        return characterId;
    }

    /**
     * 온보딩 상태 조회
     * - 캐릭터 생성 여부
     * - 캐릭터 이름(없으면 null)
     */
    @Transactional
    public OnboardingState getState(Long userId) {
        // 1) 유저 검증
        User user = userJpaRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        // 2) 캐릭터 존재 여부 확인
        boolean characterCreated = user.hasCharacterCreated();
        String characterName = null;

        // 3) 생성되어 있다면 이름 조회
        if (characterCreated) {
            Character character = characterRepository.findByUserId(userId).orElse(null);
            if (character != null) {
                characterName = character.getCharacterName();
            } else {
                // 데이터 불일치(플래그는 true인데 행이 없음) 방어
                characterCreated = false;
            }
        }

        return new OnboardingState(characterCreated, characterName);
    }

    /**
     * 컨트롤러로 전달할 내부 상태 모델
     */
    public static class OnboardingState {
        public final boolean characterCreated;
        public final String characterName;

        public OnboardingState(boolean characterCreated, String characterName) {
            this.characterCreated = characterCreated;
            this.characterName = characterName; // 없으면 null
        }
    }
}
