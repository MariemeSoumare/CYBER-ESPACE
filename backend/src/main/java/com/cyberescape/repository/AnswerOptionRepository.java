package com.cyberescape.repository;

import com.cyberescape.entity.AnswerOption;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
    List<AnswerOption> findByQuestionIdOrderByIdAsc(Long questionId);

    Optional<AnswerOption> findByIdAndQuestionId(Long id, Long questionId);
}
