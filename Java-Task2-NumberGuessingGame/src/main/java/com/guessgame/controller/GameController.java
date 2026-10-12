package com.guessgame.controller;

import com.guessgame.model.Difficulty;
import com.guessgame.model.GameModel;
import com.guessgame.model.GuessResult;
import com.guessgame.view.GameView;

import java.util.List;

public class GameController {
    private final GameModel model;
    private final GameView view;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
        initController();
    }

    private void initController() {
        view.getMainButton().addActionListener(e -> startNewRound());
        view.getGuessButton().addActionListener(e -> processGuess());
        view.getGuessTextField().addActionListener(e -> processGuess());
    }

    private void startNewRound() {
        Difficulty selectedDifficulty = (Difficulty) view.getDifficultyComboBox().getSelectedItem();
        model.startNewGame(selectedDifficulty);

        view.showRoundActive();
        view.setRoundDisplay(model.getRoundNumber(), model.getMin(), model.getMax());
        view.setAttemptsDisplay(model.getAttemptCount(), model.getMaxAttempts());
        view.setRangeDisplay(model.getRangeLow(), model.getRangeHigh(), model.getMin(), model.getMax());
        view.clearGuessChips();
        view.setSummaryText(" ");
        view.showHint("Make your guess!", GameView.COLOR_IDLE);
        view.getGuessTextField().setText("");
        view.getGuessTextField().requestFocusInWindow();
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
            view.showHint("Please enter a valid integer!", GameView.COLOR_LOST);
            return;
        }

        GuessResult result = model.submitGuess(guess);
        view.getGuessTextField().setText("");
        view.getGuessTextField().requestFocusInWindow();
        view.setAttemptsDisplay(model.getAttemptCount(), model.getMaxAttempts());
        view.setRangeDisplay(model.getRangeLow(), model.getRangeHigh(), model.getMin(), model.getMax());

        switch (result) {
            case TOO_LOW:
                view.showHint("Too Low!", GameView.COLOR_TOO_LOW);
                view.addGuessChip(guess + " ↑", GameView.COLOR_TOO_LOW);
                break;
            case TOO_HIGH:
                view.showHint("Too High!", GameView.COLOR_TOO_HIGH);
                view.addGuessChip(guess + " ↓", GameView.COLOR_TOO_HIGH);
                break;
            case CORRECT:
                view.showHint("Correct! You won!", GameView.COLOR_CORRECT);
                view.addGuessChip(guess + " ✓", GameView.COLOR_CORRECT);
                finishRound(true);
                break;
            case OUT_OF_RANGE:
                view.showHint("Out of range! Enter " + model.getMin() + " – " + model.getMax(),
                        GameView.COLOR_LOST);
                break;
            case ATTEMPTS_EXCEEDED:
                view.showHint("You Lost! Number was " + model.getTargetNumber(), GameView.COLOR_LOST);
                view.addGuessChip(guess + (guess < model.getTargetNumber() ? " ↑" : " ↓"),
                        GameView.COLOR_LOST);
                finishRound(false);
                break;
            case GAME_OVER:
                view.showHint("Round is over — press Play Again.", GameView.COLOR_IDLE);
                break;
        }
    }

    private void finishRound(boolean won) {
        int attempts = model.getAttemptCount();
        String summary = won
                ? "Round " + model.getRoundNumber()
                        + " — guessed in " + attempts
                        + (attempts == 1 ? " attempt" : " attempts")
                : "Round " + model.getRoundNumber()
                        + " — lost; the number was " + model.getTargetNumber()
                        + " (" + attempts
                        + (attempts == 1 ? " attempt" : " attempts") + ")";
        view.setSummaryText(summary);
        refreshHistoryTable();
        view.showRoundOver();
    }

    private void refreshHistoryTable() {
        List<GameModel.RoundRecord> rounds = model.getFinishedRounds();
        Object[][] rows = new Object[rounds.size()][4];
        int wins = 0;
        int losses = 0;
        int best = Integer.MAX_VALUE;

        for (int i = 0; i < rounds.size(); i++) {
            GameModel.RoundRecord r = rounds.get(i);
            rows[i] = new Object[]{
                    r.roundNumber(),
                    r.difficulty().getLabel(),
                    r.won() ? "Won" : "Lost",
                    r.attempts()
            };
            if (r.won()) {
                wins++;
                best = Math.min(best, r.attempts());
            } else {
                losses++;
            }
        }
        view.setHistoryRows(rows);

        int total = wins + losses;
        int winRate = total == 0 ? 0 : Math.round(100.0f * wins / total);
        view.setStatsText("Wins: " + wins
                + " · Losses: " + losses
                + " · Win rate: " + winRate + "%"
                + " · Best: " + (best == Integer.MAX_VALUE
                        ? "—"
                        : best + (best == 1 ? " attempt" : " attempts")));
    }
}
