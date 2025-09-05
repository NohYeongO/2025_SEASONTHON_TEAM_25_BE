package com.freedom.scrap.domain.service;

import com.freedom.auth.domain.User;
import com.freedom.common.exception.custom.QuizScrapAlreadyExistsException;
import com.freedom.common.logging.Loggable;
import com.freedom.quiz.domain.entity.UserQuiz;
import com.freedom.scrap.domain.entity.QuizScrap;
import com.freedom.scrap.infra.QuizScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateQuizScrapService {
    
    private final QuizScrapRepository quizScrapRepository;
    
    @Loggable("퀴즈 스크랩 생성")
    public void createQuizScrap(User user, UserQuiz userQuiz, Boolean isCorrectAtScrap) {
        // 중복 스크랩 확인
        validateNotAlreadyScraped(user.getId(), userQuiz.getId());
        // 스크랩 생성
        QuizScrap quizScrap = QuizScrap.create(user, userQuiz, isCorrectAtScrap);
        // 저장
        quizScrapRepository.save(quizScrap);
    }
    
    /**
     * 이미 스크랩한 퀴즈인지 검증
     */
    private void validateNotAlreadyScraped(Long userId, Long userQuizId) {
        if (quizScrapRepository.existsByUserIdAndUserQuizId(userId, userQuizId)) {
            throw new QuizScrapAlreadyExistsException(userId, userQuizId);
        }
    }
}
