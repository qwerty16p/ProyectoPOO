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
    private final int ANCHO_JUEGO = 600;
    private final int ALTO_JUEGO = 400;
    
    private ArrayList<Point> serpiente; 
    private Point comida;            
    private int dx = TAM_SEGMENTO, dy = 0;
    private boolean juegoTerminado = false;
    private Timer temporizador;
    private Random random;
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
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al iniciar partida", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        setPreferredSize(new Dimension(ANCHO_JUEGO, ALTO_JUEGO));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        random = new Random();
        serpiente = new ArrayList<>();
        // Posición inicial de la serpiente (centro del área de juego)
        serpiente.add(new Point(ANCHO_JUEGO/2, ALTO_JUEGO/2));
        generarComida();

        temporizador = new Timer(125, this);
        temporizador.start();
    }

    private void generarComida() {
        int x = random.nextInt(ANCHO_JUEGO / TAM_SEGMENTO) * TAM_SEGMENTO;
        int y = random.nextInt(ALTO_JUEGO / TAM_SEGMENTO) * TAM_SEGMENTO;
        comida = new Point(x, y);
        
        // Asegurarse de que la comida no aparezca sobre la serpiente
        while (serpiente.contains(comida)) {
            x = random.nextInt(ANCHO_JUEGO / TAM_SEGMENTO) * TAM_SEGMENTO;
            y = random.nextInt(ALTO_JUEGO / TAM_SEGMENTO) * TAM_SEGMENTO;
            comida.setLocation(x, y);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibujar la comida
        g.setColor(Color.RED);
        g.fillRect(comida.x, comida.y, TAM_SEGMENTO, TAM_SEGMENTO);
        
        // Dibujar la serpiente
        g.setColor(Color.GREEN);
        for (Point segmento : serpiente) {
            g.fillRect(segmento.x, segmento.y, TAM_SEGMENTO, TAM_SEGMENTO);
        }
        
        // Dibujar los bordes de los segmentos para mejor visibilidad
        g.setColor(Color.BLACK);
        for (Point segmento : serpiente) {
            g.drawRect(segmento.x, segmento.y, TAM_SEGMENTO, TAM_SEGMENTO);
        }
        
        // Mostrar puntaje
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Puntaje: " + puntaje, 10, 15);
        g.drawString("Jugador: " + jugador.getPseudonimo(), 10, 35);
        
        if (pausado) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String textoPausa = "PAUSA";
            int anchoTexto = g.getFontMetrics().stringWidth(textoPausa);
            g.drawString(textoPausa, (ANCHO_JUEGO - anchoTexto) / 2, ALTO_JUEGO / 2);
        }
        
        if (juegoTerminado) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String textoFin = "GAME OVER";
            int anchoTexto = g.getFontMetrics().stringWidth(textoFin);
            g.drawString(textoFin, (ANCHO_JUEGO - anchoTexto) / 2, ALTO_JUEGO / 2);
            
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            String textoPuntaje = "Puntaje final: " + puntaje;
            anchoTexto = g.getFontMetrics().stringWidth(textoPuntaje);
            g.drawString(textoPuntaje, (ANCHO_JUEGO - anchoTexto) / 2, ALTO_JUEGO / 2 + 40);
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
                ex.printStackTrace();
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

        // Colisión con bordes
        if (cabeza.x < 0 || cabeza.x >= ANCHO_JUEGO || cabeza.y < 0 || cabeza.y >= ALTO_JUEGO) {
            juegoTerminado = true;
        }

        // Colisión consigo misma (excepto la cabeza)
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
            return;
        }
        
        if (pausado) {
            return;
        }

        // Evitar movimiento inverso (ej: derecha -> izquierda)
        if ((tecla == KeyEvent.VK_LEFT) && (dx != TAM_SEGMENTO)) {
            dx = -TAM_SEGMENTO;
            dy = 0;
        } else if ((tecla == KeyEvent.VK_RIGHT) && (dx != -TAM_SEGMENTO)) {
            dx = TAM_SEGMENTO;
            dy = 0;
        } else if ((tecla == KeyEvent.VK_UP) && (dy != TAM_SEGMENTO)) {
            dx = 0;
            dy = -TAM_SEGMENTO;
        } else if ((tecla == KeyEvent.VK_DOWN) && (dy != -TAM_SEGMENTO)) {
            dx = 0;
            dy = TAM_SEGMENTO;
        } else if (tecla == KeyEvent.VK_ESCAPE) {
            int opcion = JOptionPane.showConfirmDialog(
                this, 
                "¿Terminar partida y salir?", 
                "Confirmar", 
                JOptionPane.YES_NO_OPTION);
            
            if (opcion == JOptionPane.YES_OPTION) {
                juegoTerminado = true;
                actionPerformed(null); // Forzar guardado de partida
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                frame.dispose();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}