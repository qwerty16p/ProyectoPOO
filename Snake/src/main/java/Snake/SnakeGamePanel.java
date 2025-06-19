package Snake;

import Service.GameService;
import Entities.Jugador;
import Entities.Partida;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGamePanel extends JPanel implements ActionListener, KeyListener {
    private final int TAM_SEGMENTO = 20;
    private final int CELDAS_HORIZONTALES = 30;
    private final int CELDAS_VERTICALES = 20;
    private final int ANCHO_REAL = CELDAS_HORIZONTALES * TAM_SEGMENTO;
    private final int ALTO_REAL = CELDAS_VERTICALES * TAM_SEGMENTO;

    private final ArrayList<Point> serpiente;
    private Point comida;
    private int dx = TAM_SEGMENTO, dy = 0;
    private boolean juegoTerminado = false;
    private final Timer temporizador;
    private final Random random;
    private int puntaje = 0;
    private boolean pausado = false;

    private GameService gameService;
    private Jugador jugador;
    private Partida partidaActual;

    public SnakeGamePanel(Jugador jugador) {
        this.jugador = jugador;
        this.gameService = new GameService();

        try {
            this.partidaActual = gameService.iniciarPartida(jugador.getId());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al iniciar partida", "Error", JOptionPane.ERROR_MESSAGE);
        }

        setPreferredSize(new Dimension(ANCHO_REAL, ALTO_REAL));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        random = new Random();
        serpiente = new ArrayList<>();

        int centroX = (CELDAS_HORIZONTALES / 2) * TAM_SEGMENTO;
        int centroY = (CELDAS_VERTICALES / 2) * TAM_SEGMENTO;
        serpiente.add(new Point(centroX, centroY));

        generarComida();

        temporizador = new Timer(125, this);
        temporizador.start();

        SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }

    private void generarComida() {
    int intentos = 0;
    do {
        // Evita bordes externos (al menos 2 celdas de margen)
        int celdaX = 2 + random.nextInt(CELDAS_HORIZONTALES - 4);
        int celdaY = 2 + random.nextInt(CELDAS_VERTICALES - 4);
        Point posible = new Point(celdaX * TAM_SEGMENTO, celdaY * TAM_SEGMENTO);
        if (!serpiente.contains(posible)) {
            comida = posible;
            break;
        }
        intentos++;
    } while (intentos < 100);
}



    @Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Marco externo del área de juego
    g.setColor(Color.DARK_GRAY);
    g.drawRect(0, 0, ANCHO_REAL - 1, ALTO_REAL - 1);

    // Dibujar cuadrícula interior (sin tocar el borde final)
    g.setColor(new Color(64, 64, 64, 100));
    for (int i = TAM_SEGMENTO; i < ANCHO_REAL - 1; i += TAM_SEGMENTO)
        g.drawLine(i, 0, i, ALTO_REAL);
    for (int i = TAM_SEGMENTO; i < ALTO_REAL - 1; i += TAM_SEGMENTO)
        g.drawLine(0, i, ANCHO_REAL, i);

    // Dibujar comida
    g.setColor(Color.RED);
    g.fillRect(comida.x, comida.y, TAM_SEGMENTO, TAM_SEGMENTO);
    g.setColor(Color.DARK_GRAY);
    g.drawRect(comida.x, comida.y, TAM_SEGMENTO, TAM_SEGMENTO);

    // Dibujar serpiente
    for (int i = 0; i < serpiente.size(); i++) {
        Point segmento = serpiente.get(i);
        g.setColor(i == 0 ? Color.YELLOW : Color.GREEN);
        g.fillRect(segmento.x, segmento.y, TAM_SEGMENTO, TAM_SEGMENTO);
        g.setColor(Color.BLACK);
        g.drawRect(segmento.x, segmento.y, TAM_SEGMENTO, TAM_SEGMENTO);
    }

    // Dibujar información (puntaje, jugador, longitud)
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 14));
    g.drawString("Puntaje: " + puntaje, 10, 20);
    g.drawString("Jugador: " + jugador.getPseudonimo(), 10, 40);
    g.drawString("Longitud: " + serpiente.size(), 10, 60);

    // Controles
    g.setFont(new Font("Arial", Font.PLAIN, 10));
    g.drawString(juegoTerminado ? "R: Reiniciar | ESC: Salir" : "P: Pausa | ESC: Salir", ANCHO_REAL - 140, 20);

    // Pausa
    if (pausado) {
        g.setColor(new Color(0, 0, 0, 128));
        g.fillRect(0, 0, ANCHO_REAL, ALTO_REAL);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("PAUSA", ANCHO_REAL / 2 - 60, ALTO_REAL / 2);
    }

    // Fin del juego
    if (juegoTerminado) {
        g.setColor(new Color(0, 0, 0, 128));
        g.fillRect(0, 0, ANCHO_REAL, ALTO_REAL);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("GAME OVER", ANCHO_REAL / 2 - 100, ALTO_REAL / 2 - 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Puntaje final: " + puntaje, ANCHO_REAL / 2 - 80, ALTO_REAL / 2);
        g.drawString("Longitud máxima: " + serpiente.size(), ANCHO_REAL / 2 - 100, ALTO_REAL / 2 + 20);

        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.YELLOW);
        g.drawString("Presiona R para jugar de nuevo", ANCHO_REAL / 2 - 140, ALTO_REAL / 2 + 60);
    }
}


    @Override
    public void actionPerformed(ActionEvent e) {
        if (!juegoTerminado && !pausado) {
            moverSerpiente();
            verificarColisiones();
            repaint();
        } else if (juegoTerminado) {
            temporizador.stop();
            try {
                gameService.finalizarPartida(partidaActual, puntaje);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar los resultados", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void moverSerpiente() {
        Point cabeza = serpiente.get(0);
        Point nuevaCabeza = new Point(cabeza.x + dx, cabeza.y + dy);
        serpiente.add(0, nuevaCabeza);
        if (nuevaCabeza.equals(comida)) {
            generarComida();
            puntaje += 10;
        } else {
            serpiente.remove(serpiente.size() - 1);
        }
    }

    private void verificarColisiones() {
        Point cabeza = serpiente.get(0);
        if (cabeza.x < 0 || cabeza.x >= ANCHO_REAL || cabeza.y < 0 || cabeza.y >= ALTO_REAL) {
            juegoTerminado = true;
        }
        for (int i = 1; i < serpiente.size(); i++) {
            if (cabeza.equals(serpiente.get(i))) {
                juegoTerminado = true;
                break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();
        if (tecla == KeyEvent.VK_P) {
            pausado = !pausado;
            repaint();
        } else if (tecla == KeyEvent.VK_R && juegoTerminado) {
            reiniciarJuego();
        } else if (tecla == KeyEvent.VK_ESCAPE) {
            int opcion = JOptionPane.showConfirmDialog(this, "¿Terminar partida y salir?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                juegoTerminado = true;
                actionPerformed(null);
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (frame != null) frame.dispose();
            }
        }

        if (pausado || juegoTerminado) return;

        if (tecla == KeyEvent.VK_LEFT && dx != TAM_SEGMENTO) {
            dx = -TAM_SEGMENTO; dy = 0;
        } else if (tecla == KeyEvent.VK_RIGHT && dx != -TAM_SEGMENTO) {
            dx = TAM_SEGMENTO; dy = 0;
        } else if (tecla == KeyEvent.VK_UP && dy != TAM_SEGMENTO) {
            dx = 0; dy = -TAM_SEGMENTO;
        } else if (tecla == KeyEvent.VK_DOWN && dy != -TAM_SEGMENTO) {
            dx = 0; dy = TAM_SEGMENTO;
        }
    }

    private void reiniciarJuego() {
        juegoTerminado = false;
        pausado = false;
        puntaje = 0;
        dx = TAM_SEGMENTO;
        dy = 0;
        serpiente.clear();
        int centroX = (CELDAS_HORIZONTALES / 2) * TAM_SEGMENTO;
        int centroY = (CELDAS_VERTICALES / 2) * TAM_SEGMENTO;
        serpiente.add(new Point(centroX, centroY));
        generarComida();
        try {
            this.partidaActual = gameService.iniciarPartida(jugador.getId());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al iniciar nueva partida", "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (!temporizador.isRunning()) {
            temporizador.restart();
        }
        repaint();
        requestFocusInWindow();
    }

    public void ganarFoco() {
        SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
