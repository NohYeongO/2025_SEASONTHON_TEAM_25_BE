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
    public void generateAndSaveFromNews(Long newsArticleId, String title, String summary, String plainText) {
        NewsQuizGenerationClient.QuizPack pack = quizClient.generateQuizzes(title, summary, plainText);
        if (pack == null) return;
        List<Quiz> toSave = new ArrayList<>();
        String category = pack.category() != null ? pack.category() : "뉴스 기사";
        if (pack.ox() != null) {
            NewsQuizGenerationClient.OxQuiz ox = pack.ox();
            boolean originalAnswer = ox.answer();
            boolean swap = ThreadLocalRandom.current().nextBoolean();
            boolean finalAnswer = originalAnswer;
            String finalQuestion = ox.question();
            if (swap) {
                finalAnswer = !originalAnswer;
                finalQuestion = "아닌가요? " + finalQuestion;
            }
            Quiz entity = Quiz.builder()
                    .type(QuizType.OX)
                    .difficulty(QuizDifficulty.MEDIUM)
                    .category(category)
                    .newsArticleId(newsArticleId)
                    .question(finalQuestion)
                    .explanation(ox.explanation())
                    .oxAnswer(finalAnswer)
                    .build();
            toSave.add(entity);
        }

        // 4지선다
        if (pack.mcq() != null && pack.mcq().options() != null && pack.mcq().options().size() == 4) {
            NewsQuizGenerationClient.McqQuiz mcq = pack.mcq();
            List<NewsQuizGenerationClient.McqOption> options = new ArrayList<>(mcq.options());
            Collections.shuffle(options, ThreadLocalRandom.current());
            int correctIdx = 0;
            for (int i = 0; i < 4; i++) {
                if (options.get(i).correct()) { correctIdx = i + 1; break; }
            }
            Quiz entity = Quiz.builder()
                    .type(QuizType.MCQ)
                    .difficulty(QuizDifficulty.MEDIUM)
                    .category(category)
                    .newsArticleId(newsArticleId)
                    .question(mcq.question())
                    .explanation(mcq.explanation())
                    .mcqOption1(options.get(0).text())
                    .mcqOption2(options.get(1).text())
                    .mcqOption3(options.get(2).text())
                    .mcqOption4(options.get(3).text())
                    .mcqCorrectIndex(correctIdx == 0 ? 1 : correctIdx)
                    .build();
            toSave.add(entity);
        }

        if (toSave.isEmpty()) return;
        quizRepository.saveAll(toSave);
    }
}


