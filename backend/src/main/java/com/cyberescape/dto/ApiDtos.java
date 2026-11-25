package com.cyberescape.dto;

import com.cyberescape.entity.GameStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public final class ApiDtos {

    private ApiDtos() {
    }

    public record RegisterPlayerRequest(
            @NotBlank @Size(min = 2, max = 30) String nickname) {
    }

    public record StartGameRequest(
            @NotBlank @Size(min = 2, max = 30) String nickname) {
    }

    public record PlayerResponse(Long id, String nickname) {
    }

    public record GameStateResponse(
            Long id,
            String nickname,
            int score,
            int lives,
            Long currentLevelId,
            String currentLevelName,
            GameStatus status,
            Instant startedAt) {
    }

    public record AnswerOptionResponse(Long id, String text) {
    }

    public record QuestionResponse(
            Long id,
            String prompt,
            Long levelId,
            String levelName,
            List<AnswerOptionResponse> options) {
    }

    public record AnswerRequest(
            @NotNull Long questionId,
            Long answerOptionId,
            @Size(max = 500) String answer) {
    }

    public record AnswerResponse(
            boolean correct,
            int pointsAwarded,
            int livesRemaining,
            int score,
            GameStatus status,
            GameStateResponse game) {
    }

    public record LeaderboardEntry(
            int rank,
            String nickname,
            int score,
            GameStatus status,
            Instant finishedAt) {
    }
}
