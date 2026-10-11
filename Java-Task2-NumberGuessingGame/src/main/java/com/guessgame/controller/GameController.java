package com.guessgame.controller;

import com.guessgame.model.Difficulty;
import com.guessgame.model.GameModel;
import com.guessgame.model.GuessResult;
import com.guessgame.view.GameView;

import java.util.List;
import java.util.stream.Collectors;

public class GameController {
    private final GameModel model;
    private final GameView view;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
        initController();
    }

    private void initController() {
        view.getStartButton().addActionListener(e -> startNewRound());
        view.getGuessButton().addActionListener(e -> processGuess());
        view.getGuessTextField().addActionListener(e -> processGuess());
        view.getPlayAgainButton().addActionListener(e -> startNewRound());
    }

    private void startNewRound() {
        Difficulty selectedDifficulty = (Difficulty) view.getDifficultyComboBox().getSelectedItem();
        model.startNewGame(selectedDifficulty);

        view.showRoundActive();
        view.getRoundLabel().setText("Round " + model.getRoundNumber() + " (Range: " + model.getMin() + "–" + model.getMax() + ")");
        view.getAttemptsLabel().setText("Attempts left: " + (model.getMaxAttempts() - model.getAttemptCount()));
        view.getHintLabel().setText("Make your guess!");
        view.getGuessTextField().setText("");
        view.getGuessTextField().requestFocus();
        updateGuessTracking();
    }

    private void updateGuessTracking() {
        view.setRangeDisplay("Possible range: " + model.getRangeLow() + " – " + model.getRangeHigh());
        List<Integer> guesses = model.getPastGuesses();
        view.setGuessesDisplay(guesses.isEmpty()
                ? "Past guesses: none yet"
                : "Past guesses: " + guesses.stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }

    private void processGuess() {
        String text = view.getGuessTextField().getText().trim();
        if (text.isEmpty()) {
            return;
        }

        int guess;
        try {
            guess = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            view.getHintLabel().setText("Please enter a valid integer!");
            return;
        }

        GuessResult result = model.submitGuess(guess);
        int attemptsLeft = model.getMaxAttempts() - model.getAttemptCount();
        view.getAttemptsLabel().setText("Attempts left: " + attemptsLeft);
        view.getGuessTextField().setText("");
        view.getGuessTextField().requestFocus();

        if (result == GuessResult.TOO_LOW || result == GuessResult.TOO_HIGH
                || result == GuessResult.CORRECT || result == GuessResult.ATTEMPTS_EXCEEDED) {
            updateGuessTracking();
        }

        switch (result) {
            case TOO_LOW:
                view.getHintLabel().setText("Too Low!");
                break;
            case TOO_HIGH:
                view.getHintLabel().setText("Too High!");
                break;
            case CORRECT: {
                view.getHintLabel().setText("Correct! You won!");
                String summary = "Round " + model.getRoundNumber()
                        + " — guessed in " + model.getAttemptCount()
                        + (model.getAttemptCount() == 1 ? " attempt" : " attempts");
                view.getScoreHistoryLabel().setText(summary);
                view.appendHistoryLine(summary);
                view.showRoundOver();
                break;
            }
            case OUT_OF_RANGE:
                view.getHintLabel().setText("Out of range! Enter between " + model.getMin() + " and " + model.getMax());
                break;
            case ATTEMPTS_EXCEEDED: {
                view.getHintLabel().setText("You Lost! Number was " + model.getTargetNumber());
                String summary = "Round " + model.getRoundNumber()
                        + " — lost; the number was " + model.getTargetNumber()
                        + " (" + model.getAttemptCount()
                        + (model.getAttemptCount() == 1 ? " attempt" : " attempts") + ")";
                view.getScoreHistoryLabel().setText(summary);
                view.appendHistoryLine(summary);
                view.showRoundOver();
                break;
            }
            case GAME_OVER:
                view.getHintLabel().setText("Round is over — press Play Again.");
                break;
        }
    }
}