package com.guessgame;

import com.guessgame.controller.GameController;
import com.guessgame.model.GameModel;
import com.guessgame.view.GameView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            GameModel model = new GameModel();
            GameView view = new GameView();
            new GameController(model, view);
            view.setVisible(true);
        });
    }
}
