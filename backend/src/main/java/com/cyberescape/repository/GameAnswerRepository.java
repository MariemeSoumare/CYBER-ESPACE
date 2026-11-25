package com.cyberescape.repository;

import com.cyberescape.entity.GameAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameAnswerRepository extends JpaRepository<GameAnswer, Long> {
    boolean existsBySessionIdAndQuestionIdAndCorrectTrue(Long sessionId, Long questionId);
}
