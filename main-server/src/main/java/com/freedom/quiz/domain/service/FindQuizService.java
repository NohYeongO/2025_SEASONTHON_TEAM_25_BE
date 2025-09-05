package com.freedom.quiz.domain.service;

import com.freedom.common.exception.custom.QuizNotFoundException;
import com.freedom.quiz.domain.entity.Quiz;
import com.freedom.quiz.infra.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindQuizService {

    private final QuizRepository quizRepository;

    /**
     * 퀴즈 ID 리스트로 퀴즈들 조회
     */
    public Map<Long, Quiz> findQuizzesByIds(List<Long> quizIds) {
        List<Quiz> quizzes = quizRepository.findAllById(quizIds);
        return quizzes.stream()
                .collect(Collectors.toMap(Quiz::getId, quiz -> quiz));
    }

    /**
     * 단일 퀴즈 조회
     */
    public Quiz findById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("존재하지 않는 퀴즈입니다."));
    }
}
