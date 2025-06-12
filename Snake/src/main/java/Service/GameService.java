package Service;

import Data.JugadorDAO;
import Data.PartidaDAO;
import Entities.Jugador;
import Entities.Partida;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class GameService {
    private final JugadorDAO jugadorDAO;
    private final PartidaDAO partidaDAO;
    
    public GameService() {
        this.jugadorDAO = new JugadorDAO();
        this.partidaDAO = new PartidaDAO();
    }
    
    public Jugador registrarJugador(String nombre, String pseudonimo) throws SQLException {
        Jugador jugador = new Jugador(nombre, pseudonimo);
        int id = jugadorDAO.insert(jugador);
        if (id > 0) {
            jugador.setId(id);
            return jugador;
        }
        return null;
    }
    
    public Partida iniciarPartida(int idJugador) throws SQLException {
        Partida partida = new Partida(idJugador);
        int id = partidaDAO.insert(partida);
        if (id > 0) {
            partida.setId(id);
            return partida;
        }
        return null;
    }
    
    public void finalizarPartida(Partida partida, int puntaje) throws SQLException {
        partida.setFechaHoraFin(LocalDateTime.now());
        partida.setPuntaje(puntaje);
        partidaDAO.update(partida);
    }
    
    public List<Partida> obtenerHistorialJugador(int idJugador) throws SQLException {
        return partidaDAO.findByJugador(idJugador);
    }
    
    public List<Partida> obtenerTopPuntajes() throws SQLException {
        return partidaDAO.getTopScores();
    }
    
    public Jugador buscarJugadorPorPseudonimo(String pseudonimo) throws SQLException {
        return jugadorDAO.findByPseudonimo(pseudonimo);
    }
}