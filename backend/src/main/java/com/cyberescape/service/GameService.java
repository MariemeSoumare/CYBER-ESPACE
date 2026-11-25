package com.cyberescape.service;

import com.cyberescape.dto.ApiDtos.AnswerOptionResponse;
import com.cyberescape.dto.ApiDtos.AnswerRequest;
import com.cyberescape.dto.ApiDtos.AnswerResponse;
import com.cyberescape.dto.ApiDtos.GameStateResponse;
import com.cyberescape.dto.ApiDtos.LeaderboardEntry;
import com.cyberescape.dto.ApiDtos.PlayerResponse;
import com.cyberescape.dto.ApiDtos.QuestionResponse;
import com.cyberescape.entity.AnswerOption;
import com.cyberescape.entity.GameAnswer;
import com.cyberescape.entity.GameSession;
import com.cyberescape.entity.GameStatus;
import com.cyberescape.entity.Level;
import com.cyberescape.entity.Player;
import com.cyberescape.entity.Question;
import com.cyberescape.repository.AnswerOptionRepository;
import com.cyberescape.repository.GameAnswerRepository;
import com.cyberescape.repository.GameSessionRepository;
import com.cyberescape.repository.LevelRepository;
import com.cyberescape.repository.PlayerRepository;
import com.cyberescape.repository.QuestionRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GameService {

    private final PlayerRepository playerRepository;
    private final LevelRepository levelRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final GameSessionRepository gameSessionRepository;
    private final GameAnswerRepository gameAnswerRepository;

    public GameService(
            PlayerRepository playerRepository,
            LevelRepository levelRepository,
            QuestionRepository questionRepository,
            AnswerOptionRepository answerOptionRepository,
            GameSessionRepository gameSessionRepository,
            GameAnswerRepository gameAnswerRepository) {
        this.playerRepository = playerRepository;
        this.levelRepository = levelRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.gameAnswerRepository = gameAnswerRepository;
    }

    @Transactional
    public PlayerResponse createPlayer(String nickname) {
        String normalizedNickname = nickname.trim();
        if (playerRepository.findByNicknameIgnoreCase(normalizedNickname).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce pseudonyme est déjà utilisé.");
        }
        Player player = playerRepository.save(new Player(normalizedNickname));
        return new PlayerResponse(player.getId(), player.getNickname());
    }

    @Transactional
    public GameStateResponse startGame(String nickname) {
        String normalizedNickname = nickname.trim();
        Player player = playerRepository.findByNicknameIgnoreCase(normalizedNickname)
                .orElseGet(() -> playerRepository.save(new Player(normalizedNickname)));
        Integer firstLevelNumber = levelRepository.findAllByOrderBySequenceOrderAsc()
                .stream()
                .map(Level::getSequenceOrder)
                .findFirst()
                .orElse(null);
        GameSession session = gameSessionRepository.save(new GameSession(player, firstLevelNumber));
        return toGameState(session);
    }

    @Transactional
    public QuestionResponse getCurrentQuestion(Long gameId) {
        GameSession session = getSession(gameId);
        ensureInProgress(session);
        Question question = findCurrentQuestion(session);
        if (question == null) {
            gameSessionRepository.save(session);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Aucune question n'est disponible.");
        }
        return toQuestionResponse(question);
    }

    @Transactional
    public AnswerResponse submitAnswer(Long gameId, AnswerRequest request) {
        GameSession session = getSession(gameId);
        ensureInProgress(session);
        Question question = findCurrentQuestion(session);
        if (question == null) {
            gameSessionRepository.save(session);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Aucune question n'est disponible.");
        }
        if (!question.getId().equals(request.questionId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cette question n'est pas la question active.");
        }

        AnswerOption selectedOption = resolveAnswer(question, request);
        boolean correct = selectedOption.isCorrect();
        int pointsAwarded = 0;

        gameAnswerRepository.saveAndFlush(new GameAnswer(session, question, selectedOption, correct));
        if (correct) {
            pointsAwarded = question.getPoints();
            session.addPoints(pointsAwarded);
            findCurrentQuestion(session);
        } else {
            session.loseLife();
        }

        gameSessionRepository.save(session);
        GameStateResponse gameState = toGameState(session);
        return new AnswerResponse(
                correct,
                pointsAwarded,
                session.getLives(),
                session.getScore(),
                session.getStatus(),
                gameState);
    }

    @Transactional(readOnly = true)
    public GameStateResponse getGameState(Long gameId) {
        return toGameState(getSession(gameId));
    }

    @Transactional
    public GameStateResponse finishGame(Long gameId) {
        GameSession session = getSession(gameId);
        session.finish();
        gameSessionRepository.save(session);
        return toGameState(session);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getLeaderboard() {
        List<GameStatus> completedStatuses = List.of(GameStatus.WON, GameStatus.LOST);
        List<GameSession> sessions = gameSessionRepository
                .findTop10ByStatusInOrderByScoreDescFinishedAtAsc(completedStatuses);
        return java.util.stream.IntStream.range(0, sessions.size())
                .mapToObj(index -> {
                    GameSession session = sessions.get(index);
                    return new LeaderboardEntry(
                            index + 1,
                            session.getPlayer().getNickname(),
                            session.getScore(),
                            session.getStatus(),
                            session.getFinishedAt());
                })
                .toList();
    }

    private Question findCurrentQuestion(GameSession session) {
        while (session.getStatus() == GameStatus.IN_PROGRESS) {
            Level level = levelRepository.findBySequenceOrder(session.getCurrentLevelNumber()).orElse(null);
            if (level == null) {
                session.advanceToLevel(null);
                break;
            }

            List<Question> questions = questionRepository.findByLevelIdOrderByIdAsc(level.getId());
            for (Question question : questions) {
                if (!gameAnswerRepository.existsBySessionIdAndQuestionIdAndCorrectTrue(
                        session.getId(), question.getId())) {
                    return question;
                }
            }

            Integer nextLevelNumber = levelRepository
                    .findFirstBySequenceOrderGreaterThanOrderBySequenceOrderAsc(level.getSequenceOrder())
                    .map(Level::getSequenceOrder)
                    .orElse(null);
            session.advanceToLevel(nextLevelNumber);
        }
        return null;
    }

    private AnswerOption resolveAnswer(Question question, AnswerRequest request) {
        boolean hasOptionId = request.answerOptionId() != null;
        boolean hasTextAnswer = request.answer() != null && !request.answer().isBlank();
        if (hasOptionId == hasTextAnswer) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Fournissez soit answerOptionId, soit answer, mais pas les deux.");
        }

        if (hasOptionId) {
            return answerOptionRepository.findByIdAndQuestionId(request.answerOptionId(), question.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Cette proposition ne correspond pas à la question."));
        }

        String submittedAnswer = request.answer().trim().toLowerCase(Locale.ROOT);
        return answerOptionRepository.findByQuestionIdOrderByIdAsc(question.getId())
                .stream()
                .filter(option -> option.getText().trim().toLowerCase(Locale.ROOT).equals(submittedAnswer))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "La réponse ne correspond à aucune proposition."));
    }

    private GameSession getSession(Long gameId) {
        return gameSessionRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partie introuvable."));
    }

    private void ensureInProgress(GameSession session) {
        if (session.getStatus() != GameStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cette partie n'est plus en cours (" + session.getStatus() + ").");
        }
    }

    private QuestionResponse toQuestionResponse(Question question) {
        List<AnswerOptionResponse> options = answerOptionRepository
                .findByQuestionIdOrderByIdAsc(question.getId())
                .stream()
                .map(option -> new AnswerOptionResponse(option.getId(), option.getText()))
                .toList();
        return new QuestionResponse(
                question.getId(),
                question.getPrompt(),
                question.getLevel().getId(),
                question.getLevel().getName(),
                options);
    }

    private GameStateResponse toGameState(GameSession session) {
        Level currentLevel = levelRepository.findBySequenceOrder(session.getCurrentLevelNumber()).orElse(null);
        return new GameStateResponse(
                session.getId(),
                session.getPlayer().getNickname(),
                session.getScore(),
                session.getLives(),
                currentLevel == null ? null : currentLevel.getId(),
                currentLevel == null ? null : currentLevel.getName(),
                session.getStatus(),
                session.getStartedAt());
    }
}
