package com.cyberescape.controller;

import com.cyberescape.dto.ApiDtos.PlayerResponse;
import com.cyberescape.dto.ApiDtos.RegisterPlayerRequest;
import com.cyberescape.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final GameService gameService;

    public PlayerController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerResponse createPlayer(@Valid @RequestBody RegisterPlayerRequest request) {
        return gameService.createPlayer(request.nickname());
    }
}
