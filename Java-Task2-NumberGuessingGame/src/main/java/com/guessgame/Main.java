package com.guessgame;

import com.guessgame.controller.GameController;
import com.guessgame.model.GameModel;
import com.guessgame.view.GameView;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameModel model = new GameModel();
            GameView view = new GameView();
            new GameController(model, view);
            view.setVisible(true);
        });
    }
}