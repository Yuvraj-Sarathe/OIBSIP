package com.guessgame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
    Core game logic for one session of rounds.
    Knows nothing about Swing or the console: it only tracks state and
    returns a {@link GuessResult} the controller turns into user-visible text.
 */
public class GameModel {

    private Difficulty difficulty;
    private int targetNumber;
    private int attemptCount;
    private int roundNumber;
    private boolean roundActive;
    private boolean lastRoundWon;
    // list of all guesses till now
    private final List<Integer> pastGuesses = new ArrayList<>();
    // to show the range of possible answers
    private int rangeLow;
    private int rangeHigh;

    public GameModel() {
    }

    // Begins a fresh round: new secret number, reset counters, history/roundNumber kept
    public void startNewGame(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.attemptCount = 0;
        this.roundActive = true;
        this.lastRoundWon = false;
        this.roundNumber++;
        this.targetNumber = ThreadLocalRandom.current()
                .nextInt(difficulty.getMin(), difficulty.getMax() + 1);
        this.pastGuesses.clear();
        this.rangeLow = difficulty.getMin();
        this.rangeHigh = difficulty.getMax();
    }

    public GuessResult submitGuess(int guess) {
        if (!roundActive) {
            return GuessResult.GAME_OVER;
        }
        if (guess < difficulty.getMin() || guess > difficulty.getMax()) {
            return GuessResult.OUT_OF_RANGE;
        }
        attemptCount++;
        pastGuesses.add(guess);
        if (guess < targetNumber) {
            // Target is higher than this guess: raise the floor of the search window
            rangeLow = Math.max(rangeLow, guess + 1);
        } else if (guess > targetNumber) {
            // Target is lower than this guess: lower the ceiling of the search window
            rangeHigh = Math.min(rangeHigh, guess - 1);
        }
        if (guess == targetNumber) {
            roundActive = false;
            lastRoundWon = true;
            return GuessResult.CORRECT;
        }
        if (attemptCount == difficulty.getMaxAttempts()) {
            roundActive = false;
            lastRoundWon = false;
            return GuessResult.ATTEMPTS_EXCEEDED;
        }
        return guess < targetNumber ? GuessResult.TOO_LOW : GuessResult.TOO_HIGH;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getMin() {
        return difficulty.getMin();
    }

    public int getMax() {
        return difficulty.getMax();
    }

    public int getMaxAttempts() {
        return difficulty.getMaxAttempts();
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public boolean isRoundActive() {
        return roundActive;
    }

    public boolean isLastRoundWon() {
        return lastRoundWon;
    }

    public int getTargetNumber() {
        return targetNumber;
    }

    // Defensive copy so callers cannot mutate internal state
    public List<Integer> getPastGuesses() {
        return Collections.unmodifiableList(pastGuesses);
    }

    public int getRangeLow() {
        return rangeLow;
    }

    public int getRangeHigh() {
        return rangeHigh;
    }
}
