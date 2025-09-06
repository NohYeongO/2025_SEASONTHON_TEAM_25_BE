package com.freedom.scrap.application;

import com.freedom.auth.domain.User;
import com.freedom.auth.infra.UserJpaRepository;
import com.freedom.common.dto.PageResponse;
import com.freedom.common.exception.custom.UserQuizNotFoundException;
import com.freedom.common.logging.Loggable;
import com.freedom.quiz.domain.entity.UserQuiz;
import com.freedom.quiz.infra.UserQuizRepository;
import com.freedom.scrap.application.dto.QuizScrapDto;
import com.freedom.scrap.domain.entity.QuizScrap;
import com.freedom.scrap.domain.service.CreateQuizScrapService;
import com.freedom.scrap.domain.service.FindQuizScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizScrapFacade {
    
    private final CreateQuizScrapService createQuizScrapService;
    private final FindQuizScrapService findQuizScrapService;
    private final UserJpaRepository userRepository;
    private final UserQuizRepository userQuizRepository;
    
    @Loggable("퀴즈 스크랩 등록")
    @Transactional
    public void scrapQuiz(Long userId, Long userQuizId, Boolean isCorrectAtScrap) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId: " + userId));
        
        UserQuiz userQuiz = userQuizRepository.findById(userQuizId)
            .orElseThrow(() -> new UserQuizNotFoundException("UserQuiz를 찾을 수 없습니다. userQuizId: " + userQuizId));
        
        // 사용자의 UserQuiz인지 검증
        validateUserQuizOwnership(userId, userQuiz);
        
        createQuizScrapService.createQuizScrap(user, userQuiz, isCorrectAtScrap);
    }
    
    @Loggable("사용자 퀴즈 스크랩 목록 조회")
    public PageResponse<QuizScrapDto> getQuizScrapList(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다. userId: " + userId);
        }
        
        Page<QuizScrap> quizScrapPage = findQuizScrapService.findQuizScrapsByUserId(userId, pageable);
        Page<QuizScrapDto> quizScrapDtoPage = quizScrapPage.map(QuizScrapDto::from);
        return PageResponse.of(quizScrapDtoPage);
    }
    
    /**
     * UserQuiz가 해당 사용자의 것인지 검증
     */
    private void validateUserQuizOwnership(Long userId, UserQuiz userQuiz) {
        if (!userQuiz.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 사용자의 UserQuiz가 아닙니다. userId: " + userId + ", userQuizId: " + userQuiz.getId());
        }
    }
}
