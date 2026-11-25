package com.cyberescape.repository;

import com.cyberescape.entity.Player;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByNicknameIgnoreCase(String nickname);
}
