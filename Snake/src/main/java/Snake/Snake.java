package Snake;

import Entities.Jugador;
import javax.swing.*;
import java.awt.*;

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
                frame.setResizable(false);
                frame.add(panel);
                frame.pack(); // Calcula tamaño base con PreferredSize

                // Asegurar que el área visible del panel sea exacta (600x400)
                Insets insets = frame.getInsets();
                int anchoFinal = panel.getPreferredSize().width + insets.left + insets.right;
                int altoFinal = panel.getPreferredSize().height + insets.top + insets.bottom;
                frame.setSize(anchoFinal, altoFinal);

                frame.setLocationRelativeTo(null); // Centrar ventana
                frame.setVisible(true);
                panel.requestFocusInWindow();
            } else {
                System.exit(0);
            }
        });
    }
}
