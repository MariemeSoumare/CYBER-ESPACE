package com.cyberescape.repository;

import com.cyberescape.entity.GameSession;
import com.cyberescape.entity.GameStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findTop10ByStatusInOrderByScoreDescFinishedAtAsc(Collection<GameStatus> statuses);
}
