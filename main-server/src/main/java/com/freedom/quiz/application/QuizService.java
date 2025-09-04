package com.freedom.quiz.application;

import com.freedom.admin.news.infra.client.NewsQuizGenerationClient;
import com.freedom.quiz.domain.entity.Quiz;
import com.freedom.quiz.domain.entity.QuizDifficulty;
import com.freedom.quiz.domain.entity.QuizType;
import com.freedom.quiz.infra.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final NewsQuizGenerationClient quizClient;

    @Transactional
    public void generateAndSaveFromNews(Long newsArticleId, String title, String summary, String plainText, String category) {
        NewsQuizGenerationClient.QuizPack pack = quizClient.generateQuizzes(title, summary, plainText);
        if (pack == null) return;

        List<Quiz> toSave = new ArrayList<>();

        // ========= OX =========
        if (pack.ox() != null) {
            var ox = pack.ox();

            Quiz entity = Quiz.builder()
                    .type(QuizType.OX)
                    .difficulty(QuizDifficulty.MEDIUM)
                    .category(category)
                    .newsArticleId(newsArticleId)
                    .question(ox.question().trim())
                    .explanation(ox.explanation().trim())
                    .oxAnswer(ox.answer())
                    .build();
            toSave.add(entity);
        }

        // ========= 4지선다 =========
        if (pack.mcq() != null && pack.mcq().options() != null && pack.mcq().options().size() == 4) {
            var mcq = pack.mcq();

            List<NewsQuizGenerationClient.McqOption> options = new ArrayList<>(mcq.options());
            Collections.shuffle(options, ThreadLocalRandom.current());

            // correct=true 개수 검증
            int correctCount = 0;
            int correctIdx1Based = 0;
            for (int i = 0; i < 4; i++) {
                if (Boolean.TRUE.equals(options.get(i).correct())) {
                    correctCount++;
                    correctIdx1Based = i + 1;
                }
            }

            if (correctCount == 1) { // 답이 정확히 1개일 때만 저장
                Quiz entity = Quiz.builder()
                        .type(QuizType.MCQ)
                        .difficulty(QuizDifficulty.MEDIUM)
                        .category(category)
                        .newsArticleId(newsArticleId)
                        .question(mcq.question().trim())
                        .explanation(mcq.explanation().trim())
                        .mcqOption1(options.get(0).text())
                        .mcqOption2(options.get(1).text())
                        .mcqOption3(options.get(2).text())
                        .mcqOption4(options.get(3).text())
                        .mcqCorrectIndex(correctIdx1Based)
                        .build();
                toSave.add(entity);
            }
        }

        if (!toSave.isEmpty()) {
            quizRepository.saveAll(toSave);
        }
    }

}
