package Data;
import Entities.Jugador;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAO {
    private static final String INSERT_SQL = "INSERT INTO Jugadores (Nombre, Pseudonimo) VALUES (?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM Jugadores WHERE IdJugador = ?";
    private static final String SELECT_BY_PSEUDONIMO_SQL = "SELECT * FROM Jugadores WHERE Pseudonimo = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM Jugadores";
    

    public int insert(Jugador jugador) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, jugador.getNombre());
            pstmt.setString(2, jugador.getPseudonimo());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
    
    
    public Jugador findById(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Jugador jugador = new Jugador();
                    jugador.setId(rs.getInt("IdJugador"));
                    jugador.setNombre(rs.getString("Nombre"));
                    jugador.setPseudonimo(rs.getString("Pseudonimo"));
                    jugador.setFechaRegistro(rs.getTimestamp("FechaRegistro").toLocalDateTime());
                    return jugador;
                }
            }
        }
        return null;
    }
    
    
    public Jugador findByPseudonimo(String pseudonimo) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_PSEUDONIMO_SQL)) {
            
            pstmt.setString(1, pseudonimo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Jugador jugador = new Jugador();
                    jugador.setId(rs.getInt("IdJugador"));
                    jugador.setNombre(rs.getString("Nombre"));
                    jugador.setPseudonimo(rs.getString("Pseudonimo"));
                    jugador.setFechaRegistro(rs.getTimestamp("FechaRegistro").toLocalDateTime());
                    return jugador;
                }
            }
        }
        return null;
    }
    
    
    public List<Jugador> findAll() throws SQLException {
        List<Jugador> jugadores = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            
            while (rs.next()) {
                Jugador jugador = new Jugador();
                jugador.setId(rs.getInt("IdJugador"));
                jugador.setNombre(rs.getString("Nombre"));
                jugador.setPseudonimo(rs.getString("Pseudonimo"));
                jugador.setFechaRegistro(rs.getTimestamp("FechaRegistro").toLocalDateTime());
                jugadores.add(jugador);
            }
        }
        return jugadores;
    }
}