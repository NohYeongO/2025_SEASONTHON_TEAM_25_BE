package com.freedom.admin.quiz.application;

import com.freedom.admin.quiz.api.request.CreateQuizRequest;
import com.freedom.admin.quiz.api.response.AdminQuizDetailResponse;
import com.freedom.admin.quiz.api.response.AdminQuizResponse;
import com.freedom.common.dto.PageResponse;
import com.freedom.common.exception.custom.QuizNotFoundException;
import com.freedom.common.logging.Loggable;
import com.freedom.quiz.domain.entity.Quiz;
import com.freedom.quiz.domain.entity.QuizType;
import com.freedom.quiz.infra.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminQuizService {

    private final QuizRepository quizRepository;

    @Transactional(readOnly = true)
    @Loggable("관리자 퀴즈 목록 조회")
    public PageResponse<AdminQuizResponse> getQuizList(Pageable pageable) {
        Page<Quiz> page = quizRepository.findAll(pageable);
        Page<AdminQuizResponse> mapped = page.map(AdminQuizResponse::from);
        return PageResponse.of(mapped);
    }

    @Transactional(readOnly = true)
    @Loggable("관리자 퀴즈 상세 조회")
    public AdminQuizDetailResponse getQuizDetail(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));
        return AdminQuizDetailResponse.from(quiz);
    }

    @Transactional
    @Loggable("퀴즈 생성")
    public AdminQuizResponse createQuiz(CreateQuizRequest request) {
        // 타입별 유효성 검사
        if (request.getType() == QuizType.OX) {
            request.validateOxQuiz();
        } else if (request.getType() == QuizType.MCQ) {
            request.validateMcqQuiz();
        }

        Quiz quiz = Quiz.builder()
                .type(request.getType())
                .difficulty(request.getDifficulty())
                .category(request.getCategoryDisplayName())
                .newsArticleId(request.getNewsArticleId())
                .question(request.getQuestion())
                .explanation(request.getExplanation())
                .oxAnswer(request.getOxAnswer())
                .mcqOption1(request.getMcqOption1())
                .mcqOption2(request.getMcqOption2())
                .mcqOption3(request.getMcqOption3())
                .mcqOption4(request.getMcqOption4())
                .mcqCorrectIndex(request.getMcqCorrectIndex())
                .build();

        Quiz savedQuiz = quizRepository.save(quiz);
        return AdminQuizResponse.from(savedQuiz);
    }

    @Transactional
    @Loggable("퀴즈 삭제")
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));
        
        quizRepository.delete(quiz);
    }
}
