package com.cyberescape.controller;

import com.cyberescape.dto.ApiDtos.AnswerRequest;
import com.cyberescape.dto.ApiDtos.AnswerResponse;
import com.cyberescape.dto.ApiDtos.GameStateResponse;
import com.cyberescape.dto.ApiDtos.LeaderboardEntry;
import com.cyberescape.dto.ApiDtos.QuestionResponse;
import com.cyberescape.dto.ApiDtos.StartGameRequest;
import com.cyberescape.service.GameService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/game/start")
    @ResponseStatus(HttpStatus.CREATED)
    public GameStateResponse startGame(@Valid @RequestBody StartGameRequest request) {
        return gameService.startGame(request.nickname());
    }

    @GetMapping("/game/{id}/question")
    public QuestionResponse getQuestion(@PathVariable Long id) {
        return gameService.getCurrentQuestion(id);
    }

    @PostMapping("/game/{id}/answer")
    public AnswerResponse submitAnswer(
            @PathVariable Long id,
            @Valid @RequestBody AnswerRequest request) {
        return gameService.submitAnswer(id, request);
    }

    @GetMapping("/game/{id}")
    public GameStateResponse getGameState(@PathVariable Long id) {
        return gameService.getGameState(id);
    }

    @PostMapping("/game/{id}/finish")
    public GameStateResponse finishGame(@PathVariable Long id) {
        return gameService.finishGame(id);
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardEntry> getLeaderboard() {
        return gameService.getLeaderboard();
    }
}
