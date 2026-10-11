package com.guessgame.view;

import com.guessgame.model.Difficulty;
import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {
    private JComboBox<Difficulty> difficultyComboBox;
    private JButton startButton;
    private JTextField guessTextField;
    private JButton guessButton;
    private JLabel hintLabel;
    private JLabel attemptsLabel;
    private JLabel rangeLabel;
    private JLabel guessesLabel;
    private JLabel roundLabel;
    private JLabel scoreHistoryLabel;
    private JTextArea historyArea;
    private JButton playAgainButton;

    public GameView() {
        setTitle("Number Guessing Game");
        setSize(450, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top Panel: Difficulty selection & Start button
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select Difficulty:"));
        difficultyComboBox = new JComboBox<>(Difficulty.values());
        topPanel.add(difficultyComboBox);
        startButton = new JButton("Start Game");
        topPanel.add(startButton);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Core game play components and hints
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        roundLabel = new JLabel("Round: 0");
        roundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(roundLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        attemptsLabel = new JLabel("Attempts left: -");
        attemptsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(attemptsLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        rangeLabel = new JLabel("Possible range: -");
        rangeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        rangeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(rangeLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        guessesLabel = new JLabel("Past guesses: none yet");
        guessesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        guessesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(guessesLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel guessPanel = new JPanel(new FlowLayout());
        guessPanel.add(new JLabel("Enter your guess:"));
        guessTextField = new JTextField(10);
        guessPanel.add(guessTextField);
        guessButton = new JButton("Guess");
        guessPanel.add(guessButton);
        guessPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(guessPanel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        hintLabel = new JLabel("Choose difficulty and click Start Game!");
        hintLabel.setFont(new Font("Arial", Font.BOLD, 13));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(hintLabel);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel: accumulating round history + last summary + Play Again button
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

        historyArea = new JTextArea(6, 30);
        historyArea.setEditable(false);
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setBorder(BorderFactory.createTitledBorder("Round History"));
        bottomPanel.add(historyScroll, BorderLayout.CENTER);

        JPanel bottomRow = new JPanel(new BorderLayout(5, 5));
        scoreHistoryLabel = new JLabel("No rounds played yet");
        scoreHistoryLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        bottomRow.add(scoreHistoryLabel, BorderLayout.CENTER);

        playAgainButton = new JButton("Play Again");
        playAgainButton.setEnabled(false);
        bottomRow.add(playAgainButton, BorderLayout.EAST);

        bottomPanel.add(bottomRow, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        showReadyForFirstRound();
    }

    public void showReadyForFirstRound() {
        guessTextField.setEnabled(false);
        guessButton.setEnabled(false);
        startButton.setEnabled(true);
        playAgainButton.setEnabled(false);
        difficultyComboBox.setEnabled(true);
    }

    public void showRoundActive() {
        guessTextField.setEnabled(true);
        guessButton.setEnabled(true);
        startButton.setEnabled(false);
        playAgainButton.setEnabled(false);
        difficultyComboBox.setEnabled(false);
    }

    public void showRoundOver() {
        guessTextField.setEnabled(false);
        guessButton.setEnabled(false);
        startButton.setEnabled(false);
        playAgainButton.setEnabled(true);
        difficultyComboBox.setEnabled(true);
    }

    public void appendHistoryLine(String line) {
        historyArea.append(line + "\n");
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    public void setRangeDisplay(String text) {
        rangeLabel.setText(text);
    }

    public void setGuessesDisplay(String text) {
        guessesLabel.setText(text);
    }

    public JComboBox<Difficulty> getDifficultyComboBox() { return difficultyComboBox; }
    public JButton getStartButton() { return startButton; }
    public JTextField getGuessTextField() { return guessTextField; }
    public JButton getGuessButton() { return guessButton; }
    public JButton getPlayAgainButton() { return playAgainButton; }
    public JLabel getHintLabel() { return hintLabel; }
    public JLabel getAttemptsLabel() { return attemptsLabel; }
    public JLabel getRoundLabel() { return roundLabel; }
    public JLabel getScoreHistoryLabel() { return scoreHistoryLabel; }
}