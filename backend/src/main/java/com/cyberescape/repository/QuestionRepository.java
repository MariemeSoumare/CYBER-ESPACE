package com.cyberescape.repository;

import com.cyberescape.entity.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByLevelIdOrderByIdAsc(Long levelId);

    long countByLevelId(Long levelId);
}
