package Snake;

import Entities.Jugador;
import javax.swing.*;

public class Snake {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            
            // Mostrar diálogo de login primero
            LoginDialog login = new LoginDialog(frame);
            login.setVisible(true);
            
            Jugador jugador = login.getJugador();
            if (jugador != null) {
                SnakeGamePanel panel = new SnakeGamePanel(jugador); 
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 400);
                frame.add(panel);
                frame.setResizable(false);
                frame.setVisible(true);
                panel.requestFocusInWindow();
            } else {
                System.exit(0);
            }
        });
    }
}