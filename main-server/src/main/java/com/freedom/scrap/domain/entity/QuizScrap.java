package com.freedom.scrap.domain.entity;

import com.freedom.auth.domain.User;
import com.freedom.common.entity.BaseEntity;
import com.freedom.quiz.domain.entity.UserQuiz;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
    name = "quiz_scrap",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_user_quiz",
        columnNames = {"user_id", "user_quiz_id"}
    ),
    indexes = {
        @Index(name = "idx_user_id_scrapped_date", columnList = "user_id, scrapped_date DESC")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizScrap extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_quiz_id", nullable = false)
    private UserQuiz userQuiz;
    
    @Column(name = "scrapped_date", nullable = false)
    private LocalDate scrappedDate;
    
    @Column(name = "is_correct_at_scrap", nullable = false)
    private Boolean isCorrectAtScrap;
    
    @Builder
    public QuizScrap(User user, UserQuiz userQuiz, LocalDate scrappedDate, Boolean isCorrectAtScrap) {
        this.user = user;
        this.userQuiz = userQuiz;
        this.scrappedDate = scrappedDate != null ? scrappedDate : LocalDate.now();
        this.isCorrectAtScrap = isCorrectAtScrap;
    }
    
    public static QuizScrap create(User user, UserQuiz userQuiz, Boolean isCorrectAtScrap) {
        return QuizScrap.builder()
                .user(user)
                .userQuiz(userQuiz)
                .scrappedDate(LocalDate.now())
                .isCorrectAtScrap(isCorrectAtScrap)
                .build();
    }
}
