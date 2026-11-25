package com.cyberescape.repository;

import com.cyberescape.entity.Level;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository extends JpaRepository<Level, Long> {
    List<Level> findAllByOrderBySequenceOrderAsc();

    Optional<Level> findBySequenceOrder(Integer sequenceOrder);

    Optional<Level> findFirstBySequenceOrderGreaterThanOrderBySequenceOrderAsc(Integer sequenceOrder);
}
