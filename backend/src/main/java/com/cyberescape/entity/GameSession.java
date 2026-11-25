package com.cyberescape.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "game_session")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "current_level", nullable = false)
    private int currentLevelNumber;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int lives;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameStatus status;

    @Column(name = "started_at", nullable = false, updatable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    protected GameSession() {
    }

    public GameSession(Player player, Integer firstLevelNumber) {
        this.player = player;
        this.currentLevelNumber = firstLevelNumber == null ? 1 : firstLevelNumber;
        this.score = 0;
        this.lives = 3;
        this.status = firstLevelNumber == null ? GameStatus.WON : GameStatus.IN_PROGRESS;
        this.startedAt = Instant.now();
        this.finishedAt = firstLevelNumber == null ? this.startedAt : null;
    }

    @PreUpdate
    protected void onUpdate() {
        if (status != GameStatus.IN_PROGRESS && finishedAt == null) {
            finishedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public int getCurrentLevelNumber() {
        return currentLevelNumber;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void addPoints(int points) {
        score += points;
    }

    public void loseLife() {
        lives--;
        if (lives <= 0) {
            lives = 0;
            status = GameStatus.LOST;
            finishedAt = Instant.now();
        }
    }

    public void advanceToLevel(Integer levelNumber) {
        if (levelNumber == null) {
            status = GameStatus.WON;
            finishedAt = Instant.now();
        } else {
            currentLevelNumber = levelNumber;
        }
    }

    public void finish() {
        if (status == GameStatus.IN_PROGRESS) {
            status = GameStatus.LOST;
            finishedAt = Instant.now();
        }
    }
}
