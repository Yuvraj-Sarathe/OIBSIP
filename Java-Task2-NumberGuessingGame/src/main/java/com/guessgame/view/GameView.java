package com.guessgame.view;

import com.guessgame.model.Difficulty;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;

public class GameView extends JFrame {

    public static final Color COLOR_BG = new Color(0xEE, 0xF1, 0xF7);
    public static final Color COLOR_CARD = Color.WHITE;
    public static final Color COLOR_IDLE = new Color(0x5F, 0x6B, 0x7A);
    public static final Color COLOR_TOO_HIGH = new Color(0xE6, 0x7E, 0x22);
    public static final Color COLOR_TOO_LOW = new Color(0x2E, 0x86, 0xDE);
    public static final Color COLOR_CORRECT = new Color(0x27, 0xAE, 0x60);
    public static final Color COLOR_LOST = new Color(0xC0, 0x39, 0x2B);
    private static final Color COLOR_AMBER = new Color(0xF3, 0x9C, 0x12);
    private static final Color COLOR_TRACK = new Color(0xD8, 0xDF, 0xEA);

    private JComboBox<Difficulty> difficultyComboBox;
    private JButton mainButton;
    private JTextField guessTextField;
    private JButton guessButton;
    private JLabel hintLabel;
    private JLabel roundLabel;
    private AttemptsBar attemptsBar;
    private RangeBar rangeBar;
    private JLabel rangeLabel;
    private JPanel chipsPanel;
    private JLabel summaryLabel;
    private JLabel statsLabel;
    private DefaultTableModel historyModel;
    private boolean roundActive;

    public GameView() {
        setTitle("Number Guessing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(520, 600));
        setSize(1180, 780);
        setLocationRelativeTo(null);

        JPanel gameCard = buildGameCard();
        JPanel historyCard = buildHistoryCard();

        JPanel cards = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 16));
        cards.setOpaque(false);
        cards.add(gameCard);
        cards.add(historyCard);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(COLOR_BG);
        root.add(cards);
        setContentPane(root);

        getRootPane().setDefaultButton(mainButton);
        getRootPane().registerKeyboardAction(
                e -> setExtendedState(getExtendedState() ^ JFrame.MAXIMIZED_BOTH),
                KeyStroke.getKeyStroke("F11"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        ((AbstractDocument) guessTextField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string != null && string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null || text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        difficultyComboBox.addActionListener(e -> {
            if (!roundActive) {
                Difficulty d = (Difficulty) difficultyComboBox.getSelectedItem();
                if (d != null) refreshIdleDisplays(d);
            }
        });

        showReadyForFirstRound();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JPanel buildGameCard() {
        JPanel card = new JPanel(new java.awt.BorderLayout(16, 16));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8, 0xDF, 0xEA)),
                new EmptyBorder(24, 32, 24, 32)));
        card.setPreferredSize(new Dimension(560, 680));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topPanel.setOpaque(false);
        topPanel.add(new JLabel("Select Difficulty:"));
        difficultyComboBox = new JComboBox<>(Difficulty.values());
        difficultyComboBox.setSelectedItem(Difficulty.MEDIUM);
        topPanel.add(difficultyComboBox);
        card.add(topPanel, java.awt.BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        roundLabel = new JLabel("Round: —");
        roundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roundLabel.setFont(roundLabel.getFont().deriveFont(Font.BOLD, 15f));
        centerPanel.add(Box.createVerticalGlue(), 0);
        centerPanel.add(roundLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        attemptsBar = new AttemptsBar();
        attemptsBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        attemptsBar.setPreferredSize(new Dimension(460, 30));
        attemptsBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        centerPanel.add(attemptsBar);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        rangeBar = new RangeBar();
        rangeBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        rangeBar.setPreferredSize(new Dimension(460, 26));
        rangeBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        centerPanel.add(rangeBar);

        rangeLabel = new JLabel("Possible range: —");
        rangeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rangeLabel.setFont(rangeLabel.getFont().deriveFont(12f));
        rangeLabel.setForeground(COLOR_IDLE);
        centerPanel.add(rangeLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        chipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        chipsPanel.setOpaque(false);
        chipsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        chipsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        centerPanel.add(chipsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        JPanel guessPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        guessPanel.setOpaque(false);
        guessPanel.add(new JLabel("Enter your guess:"));
        guessTextField = new JTextField(10);
        guessPanel.add(guessTextField);
        guessButton = new JButton("Guess");
        guessPanel.add(guessButton);
        guessPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        guessPanel.setMaximumSize(guessPanel.getPreferredSize());
        centerPanel.add(guessPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        hintLabel = new JLabel();
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hintLabel.setFont(hintLabel.getFont().deriveFont(Font.BOLD, 28f));
        hintLabel.setForeground(COLOR_IDLE);
        centerPanel.add(hintLabel);
        centerPanel.add(Box.createVerticalGlue());

        card.add(centerPanel, java.awt.BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        mainButton = new JButton("Start Game");
        mainButton.setFont(mainButton.getFont().deriveFont(Font.BOLD, 15f));
        mainButton.setPreferredSize(new Dimension(220, 44));
        bottomPanel.add(mainButton);
        card.add(bottomPanel, java.awt.BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildHistoryCard() {
        JPanel card = new JPanel(new java.awt.BorderLayout(12, 12));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8, 0xDF, 0xEA)),
                new EmptyBorder(24, 24, 24, 24)));
        card.setPreferredSize(new Dimension(380, 680));

        summaryLabel = new JLabel(" ");
        summaryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        summaryLabel.setFont(summaryLabel.getFont().deriveFont(Font.BOLD, 14f));
        card.add(summaryLabel, java.awt.BorderLayout.NORTH);

        historyModel = new DefaultTableModel(
                new Object[]{"Round", "Difficulty", "Result", "Attempts"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable historyTable = new JTable(historyModel);
        historyTable.setRowHeight(26);
        historyTable.setFillsViewportHeight(true);
        historyTable.getTableHeader().setReorderingAllowed(false);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(75);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(80);

        DefaultTableCellRenderer resultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if ("Won".equals(value)) {
                    setForeground(COLOR_CORRECT);
                } else if ("Lost".equals(value)) {
                    setForeground(COLOR_LOST);
                }
                return c;
            }
        };
        historyTable.getColumnModel().getColumn(2).setCellRenderer(resultRenderer);

        JScrollPane tableScroll = new JScrollPane(historyTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Round History"));
        card.add(tableScroll, java.awt.BorderLayout.CENTER);

        statsLabel = new JLabel("Wins: 0 · Losses: 0 · Win rate: 0% · Best: —");
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statsLabel.setFont(statsLabel.getFont().deriveFont(Font.BOLD, 13f));
        card.add(statsLabel, java.awt.BorderLayout.SOUTH);

        return card;
    }

    private void refreshIdleDisplays(Difficulty d) {
        attemptsBar.setState(0, d.getMaxAttempts());
        rangeBar.setRange(d.getMin(), d.getMax(), d.getMin(), d.getMax());
        rangeLabel.setText("Possible range: " + d.getMin() + " – " + d.getMax());
    }

    public void showReadyForFirstRound() {
        roundActive = false;
        mainButton.setText("Start Game");
        mainButton.setEnabled(true);
        guessTextField.setEnabled(false);
        guessButton.setEnabled(false);
        difficultyComboBox.setEnabled(true);
        hintLabel.setText("Press Start to play!");
        hintLabel.setForeground(COLOR_IDLE);
        refreshIdleDisplays((Difficulty) difficultyComboBox.getSelectedItem());
    }

    public void showRoundActive() {
        roundActive = true;
        mainButton.setEnabled(false);
        guessTextField.setEnabled(true);
        guessButton.setEnabled(true);
        difficultyComboBox.setEnabled(false);
    }

    public void showRoundOver() {
        roundActive = false;
        mainButton.setText("Play Again");
        mainButton.setEnabled(true);
        guessTextField.setEnabled(false);
        guessButton.setEnabled(false);
        difficultyComboBox.setEnabled(true);
        mainButton.requestFocusInWindow();
    }

    public void showHint(String text, Color color) {
        hintLabel.setForeground(color);
        // shrink long messages so they never clip inside the card
        float size = text.length() > 20 ? 22f : 28f;
        hintLabel.setFont(hintLabel.getFont().deriveFont(Font.BOLD, size));
        hintLabel.setText(text);
    }

    public void setRoundDisplay(int round, int min, int max) {
        roundLabel.setText("Round: " + round + "  (Range: " + min + "–" + max + ")");
    }

    public void setAttemptsDisplay(int used, int max) {
        attemptsBar.setState(used, max);
    }

    public void setRangeDisplay(int low, int high, int min, int max) {
        rangeBar.setRange(low, high, min, max);
        rangeLabel.setText("Possible range: " + low + " – " + high);
    }

    public void clearGuessChips() {
        chipsPanel.removeAll();
        chipsPanel.revalidate();
        chipsPanel.repaint();
    }

    public void addGuessChip(String text, Color bg) {
        JLabel chip = new JLabel(text);
        chip.setOpaque(true);
        chip.setBackground(bg);
        chip.setForeground(Color.WHITE);
        chip.setFont(chip.getFont().deriveFont(Font.BOLD, 12f));
        chip.setBorder(new EmptyBorder(3, 10, 3, 10));
        chipsPanel.add(chip);
        chipsPanel.revalidate();
        chipsPanel.repaint();
    }

    public void setSummaryText(String text) {
        summaryLabel.setText(text.isEmpty() ? " " : "<html><center>" + text + "</center></html>");
    }

    public void setHistoryRows(Object[][] rows) {
        historyModel.setRowCount(0);
        for (Object[] row : rows) {
            historyModel.addRow(row);
        }
    }

    public void setStatsText(String text) {
        statsLabel.setText(text);
    }

    public JComboBox<Difficulty> getDifficultyComboBox() {
        return difficultyComboBox;
    }

    public JButton getMainButton() {
        return mainButton;
    }

    public JButton getGuessButton() {
        return guessButton;
    }

    public JTextField getGuessTextField() {
        return guessTextField;
    }

    private static class RangeBar extends JComponent {
        private int low = 1;
        private int high = 100;
        private int min = 1;
        private int max = 100;

        void setRange(int low, int high, int min, int max) {
            this.low = low;
            this.high = high;
            this.min = min;
            this.max = max;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int inset = 4;
            int trackH = h - 2 * inset;
            int arc = trackH;

            g2.setColor(COLOR_TRACK);
            g2.fillRoundRect(0, inset, w, trackH, arc, arc);

            double span = Math.max(1, max - min);
            int x0 = (int) ((low - min) / span * (w - 2.0 * inset)) + inset;
            int x1 = (int) ((high - min) / span * (w - 2.0 * inset)) + inset;
            int segW = Math.max(30, x1 - x0);
            g2.setPaint(new GradientPaint(x0, 0, COLOR_TOO_LOW, x0 + segW, 0, COLOR_CORRECT));
            g2.fillRoundRect(x0, inset, segW, trackH, arc, arc);
            g2.dispose();
        }
    }

    private static class AttemptsBar extends JProgressBar {
        private Color fill = COLOR_CORRECT;

        void setState(int used, int max) {
            setMaximum(max);
            setValue(used);
            setString("Attempt " + used + " / " + max);
            double leftFrac = max == 0 ? 0 : (max - used) / (double) max;
            fill = leftFrac > 0.5 ? COLOR_CORRECT : leftFrac > 0.25 ? COLOR_AMBER : COLOR_LOST;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int inset = 4;
            int trackH = h - 2 * inset;
            int arc = trackH;

            g2.setColor(COLOR_TRACK);
            g2.fillRoundRect(0, inset, w, trackH, arc, arc);

            double frac = getMaximum() == 0 ? 0 : getValue() / (double) getMaximum();
            int fillW = (int) ((w - 2.0 * inset) * frac);
            if (fillW > 0) {
                g2.setColor(fill);
                g2.fillRoundRect(inset, inset, Math.max(fillW, arc), trackH, arc, arc);
                if (fillW < w - 2 * inset) {
                    g2.setColor(fill);
                    g2.fillRect(inset, inset, fillW, trackH);
                }
            }

            String text = getString();
            if (text != null) {
                g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
                var fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(text)) / 2;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
                boolean overFill = frac > 0.45;
                g2.setColor(overFill ? new Color(0xFF, 0xFF, 0xFF) : COLOR_IDLE);
                g2.drawString(text, tx, ty);
            }
            g2.dispose();
        }
    }
}
