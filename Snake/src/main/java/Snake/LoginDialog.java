package Snake;

import Service.GameService;
import Entities.Jugador;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginDialog extends JDialog {
    private JTextField txtNombre;
    private JTextField txtPseudonimo;
    private Jugador jugador;
    
    public LoginDialog(JFrame parent) {
        super(parent, "Registro de Jugador", true);
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new GridLayout(3, 2, 5, 5));
        
        add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        add(txtNombre);
        
        add(new JLabel("Pseudónimo:"));
        txtPseudonimo = new JTextField();
        add(txtPseudonimo);
        
        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(e -> {
            try {
                GameService service = new GameService();
                jugador = service.buscarJugadorPorPseudonimo(txtPseudonimo.getText());
                
                if (jugador == null) {
                    jugador = service.registrarJugador(txtNombre.getText(), txtPseudonimo.getText());
                    if (jugador == null) {
                        JOptionPane.showMessageDialog(this, "Error al registrar jugador", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(btnAceptar);
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    public Jugador getJugador() {
        return jugador;
    }
}